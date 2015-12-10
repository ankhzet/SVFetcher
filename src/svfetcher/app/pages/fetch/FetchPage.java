package svfetcher.app.pages.fetch;

import svfetcher.app.pages.fetch.stated.SectionsStateList;
import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.action.Action;
import svfetcher.app.story.Source;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.Post;
import svfetcher.app.sv.forum.Story;
import svfetcher.app.pages.compose.ComposePage;
import svfetcher.app.pages.fetch.stated.StatedSource;
import svfetcher.app.pages.pick.LinkPage;
import svfetcher.app.utils.SectionMapping;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FetchPage extends AbstractPage {

  @DependencyInjection()
  protected SV sv;

  FetchTask fetchTask = null;

  SectionsStateList<Source> sectionsList;

  @Override
  public String pathFragment() {
    return "Fetching";
  }

  @Override
  protected Node buildNode() {
    SourceList list = new SourceList(sectionsList);
    list.setPickHandler(ref -> {
      SourceCell cell = ref.get();
      if (cell == null)
        return;

      StatedSource<Source> stated = cell.getItem();
      if (stated == null)
        return;

      Source source = stated.getItem();

      proceed(LinkPage.class, source.getUrl());
    });

    Button delete = new Button("X");
    delete.setOnAction(h -> {
      List<StatedSource<Source>> selected = new ArrayList(list.getSelectionModel().getSelectedItems());
      sectionsList.removeAll(selected);
      Story story = story();
      for (StatedSource<Source> stated : selected) {
        Source source = stated.getItem();
        story.remove(source);

        String url = source.getUrl();
        SectionMapping mapping = SectionMapping.model().find(url).first();
        if (mapping == null)
          mapping = new SectionMapping(url);

        mapping.setSuppressed(true);

        SectionMapping.saveModel(mapping);
      }
    });

    HBox del = new HBox(delete);
    HBox.setHgrow(del, Priority.ALWAYS);

    Button revoke = new Button("Revoke");
    revoke.setOnAction(h -> showNotification());
    HBox hbox = new HBox(del, revoke);
    VBox vbox = new VBox(8, list, hbox);
    VBox.setVgrow(list, Priority.ALWAYS);

    return vbox;
  }

  @Override
  protected void done() {
    super.done();
    _story = null;
  }

  @Override
  protected void ready() {
    setTitle("Fetching...");
    Story story = story();
    sectionsList = new SectionsStateList(FXCollections.observableArrayList(story.sections()));

    _story = filtered(story);

    showNotification();
  }

  private Story _story;

  Story story() {
    if (_story == null) {
      Story source = navDataAtIndex(0, () -> new Story());
      _story = source;
      Map<Source, Post> posts = filterSuppressed(_story);
      _story.clear();
      _story.putAll(posts);
    }
    return _story;
  }

  Map<Source, Post> filterSuppressed(Map<Source, Post> posts) {
    Map<String, Source> sourcesMap = new LinkedHashMap<>();
    List<String> urls = new ArrayList<>();
    for (Source source : posts.keySet()) {
      String url = source.getUrl();
      sourcesMap.put(url, source);
      urls.add(url);
    }

    List<SectionMapping> mappings = SectionMapping.model()
      .whereIn("url", urls.toArray())
      .get();

    Map<String, SectionMapping> filtered = SectionMapping.filterMappings(mappings, urls);

    Map<Source, Post> filteredSources = new LinkedHashMap<>();
    for (String url : filtered.keySet()) {
      Source source = sourcesMap.get(url);

      SectionMapping mapping = filtered.get(url);
      if (mapping != null && mapping.getTitle() != null)
        source.setName(mapping.getTitle());

      filteredSources.put(source, posts.get(source));
    }

    return filteredSources;
  }

  Map<Source, Post> filterEmpty(Map<Source, Post> sources) {
    LinkedHashMap<Source, Post> nonEmpty = new LinkedHashMap<>();

    for (Source source : sources.keySet()) {
      Post post = sources.get(source);
      if (post != null && post.getContents() != null)
        nonEmpty.put(source, post);
    }

    return nonEmpty;
  }

  Story filtered(Story story) {
    Map<Source, Post> posts = filterEmpty(story);
    story.clear();
    story.putAll(posts);

    Map<Source, StatedSource<Source>> sections = new LinkedHashMap<>();
    for (StatedSource<Source> section : sectionsList) {
      Source source = section.getItem();
      sections.put(source, section);
    }

    for (Post post : posts.values()) {
      StatedSource<Source> section = sections.get(post.getSource());
      section.setFetched(post.getContents() != null);
    }

    return story;
  }

  void showNotification() {
    boolean hasUnfinished = sectionsList.hasUnfinished();
    notify(
      hasUnfinished ? "Ready to fetch posts" : "All sections checked",
      new Action(
        hasUnfinished ? "Fetch" : "Proceed",
        h -> fetch()
      )
    );
  }

  boolean fetch() {
    if (!sectionsList.hasUnfinished())
      return complete();

    return followup((TaskedResultSupplier<Post>) supplier -> {
      return supplier.get(() -> fetchTask = new FetchTask(sv, sectionsList))
        .setError("Failed to fetch post")
        .setOnFailed(h -> stop())
        .setOnCancelled(h -> showNotification())
        .schedule(post -> {
          story().addSection(post);
          fetch();
        });
    });
  }

  boolean stop() {
    return (fetchTask == null)
           || !fetchTask.isRunning()
           || fetchTask.isCancelled()
           || fetchTask.cancel();
  }

  boolean complete() {
    stop();

    return proceed(ComposePage.class, filtered(story()));
  }

}
