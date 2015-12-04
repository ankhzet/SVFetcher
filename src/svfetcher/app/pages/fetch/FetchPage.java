package svfetcher.app.pages.fetch;

import svfetcher.app.pages.fetch.stated.SectionsStateList;
import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FetchPage extends AbstractPage {

  @DependencyInjection()
  protected SV sv;

  private LinkPopover popOver;

  FetchTask fetchTask = null;

  SectionsStateList<Source> sectionsList;

  @Override
  public String pathFragment() {
    return "Fetching";
  }

  @Override
  protected Node buildNode() {
    SourceList list = new SourceList(sectionsList);

    Button delete = new Button("X");
    delete.setOnAction(h -> {
      ObservableList<StatedSource<Source>> selected = list.getSelectionModel().getSelectedItems();
      sectionsList.removeAll(new ArrayList(selected));
      Story story = story();
      for (StatedSource<Source> source : selected)
        story.remove(source.getItem());
    });

    Button revoke = new Button("Revoke");
    revoke.setOnAction(h -> showNotification());
    HBox hbox = new HBox(delete, revoke);
    VBox vbox = new VBox(8, list, hbox);
    VBox.setVgrow(list, Priority.ALWAYS);
//    view.setOnSelect((cell) -> {
//      if (popOver != null && popOver.isShowing())
//        popOver.hideImmediately();
//
//      Source source = cell.getItem().getItem();
//      popOver = new LinkPopover();
//      popOver.setSource(source);
//      popOver.showNear(cell);
//    });

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
//    Story source = navDataAtIndex(0, () -> new Story());
    ObservableList<Source> sources = FXCollections.observableArrayList(story.sections());
    sectionsList = new SectionsStateList(sources);

    LinkedHashMap<Source, Post> done = new LinkedHashMap<>();
    for (StatedSource<Source> section : sectionsList) {
      Post post = story.get(section.getItem());
      if (post != null) {
        section.setFetched(post.getContents() != null);
        done.put(post.getSource(), post);
      }
    }

    story.clear();
    story.putAll(done);

    showNotification();
  }

  private Story _story;

  Story story() {
    if (_story == null) {
      Story source = navDataAtIndex(0, () -> new Story());
      _story = new Story();
      _story.setSource(source.getSource());
      _story.setTitle(source.getTitle());
      _story.setAuthor(source.getAuthor());
      _story.setAnnotation(source.getAnnotation());
      _story.putAll(source);
    }
    return _story;
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

    return proceed(ComposePage.class, story());
  }

}
