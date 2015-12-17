package svfetcher.app.pages.fetch;

import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import java.util.*;
import javafx.beans.InvalidationListener;
import javafx.collections.*;
import javafx.collections.ListChangeListener.Change;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.action.Action;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.Post;
import svfetcher.app.sv.forum.Story;
import svfetcher.app.pages.compose.SavePage;
import svfetcher.app.pages.fetch.stated.SectionsStateList;
import svfetcher.app.pages.fetch.stated.StatedSource;
import svfetcher.app.pages.fetch.traversable.MappableSourcesListHelper;
import svfetcher.app.pages.fetch.traversable.SourcesListHelper;
import svfetcher.app.pages.pick.LinkPage;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FetchPage extends AbstractPage {

  @DependencyInjection()
  protected SV sv;

  SourcesListHelper sections;

  Button restore;

  @Override
  public String pathFragment() {
    return "Fetching";
  }

  @Override
  protected Node buildNode() {
    sections = new MappableSourcesListHelper(story().sections());

    SourceList list = new SourceList(sections.statedList());
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
      List<Source> selected = new ArrayList<>();
      for (StatedSource<Source> stated : selectedItems(list))
        selected.add(stated.getItem());

      sections.hideAll(selected);
    });
    list.getSelectionModel()
      .getSelectedIndices()
      .addListener((InvalidationListener) i
        -> delete.setDisable(selectedItems(list).isEmpty())
      );
    delete.setDisable(selectedItems(list).isEmpty());

    restore = new Button("Show deleted links");

    ObservableList<Source> hiddenItems = sections.getHiddenItems();
    hiddenItems.addListener(this::onHiddenSourcesListChanged);
    disableRestoreButton(hiddenItems.isEmpty());

    restore.setOnAction(h -> sections.showAll());

    sections.getBase().addListener(this::onShowedSourcesListChanged);

    HBox del = new HBox(8, delete, restore);
    HBox.setHgrow(del, Priority.ALWAYS);

    Button revoke = new Button("Revoke");
    revoke.setOnAction(h -> showNotification());

    HBox hbox = new HBox(del, revoke);
    VBox vbox = new VBox(8, list, hbox);
    VBox.setVgrow(list, Priority.ALWAYS);

    return vbox;
  }

  protected void onHiddenSourcesListChanged(Change<? extends Source> c) {
    disableRestoreButton(c.getList().isEmpty());
  }

  protected void onShowedSourcesListChanged(Change<? extends Source> c) {
    Story story = story();
    SectionsStateList<Source> statedList = sections.statedList();
    ObservableList<? extends Source> showed = c.getList();

    for (Source source : showed) {
      Post post = story.get(source);
      if (post == null || post.isEmpty()) {
        StatedSource<Source> stated = statedList.stated(source);
        stated.setFetched(false);
      }
    }
  }

  void disableRestoreButton(boolean disable) {
    restore.setDisable(disable);
  }

  ObservableList<StatedSource<Source>> selectedItems(SourceList list) {
    return list.getSelectionModel().getSelectedItems();
  }

  @Override
  protected void done() {
    super.done();
    sections = null;
  }

  @Override
  protected void ready() {
    Story story = story();
    setTitle("Fetching \"" + story.getTitle() + "\"");

    showNotification();
  }

  Story story() {
    return navDataAtIndex(0, () -> new Story());
  }

  void showNotification() {
    boolean hasUnfinished = sections.statedList().hasUnfinished();
    notify(
      hasUnfinished ? "Ready to fetch posts" : "All sections checked",
      new Action(
        hasUnfinished ? "Fetch" : "Proceed",
        h -> fetch()
      )
    );
  }

  boolean fetch() {
    if (!sections.statedList().hasUnfinished())
      return proceed();

    return followup((TaskedResultSupplier<Post>) supplier -> {
      return supplier.get(() -> new FetchTask(sv, sections.statedList()))
        .setError("Failed to fetch post")
        .setOnFailed(h -> {})
        .setOnCancelled(h -> showNotification())
        .schedule(post -> {
          story().addSection(post);
          fetch();
        });
    });
  }

  boolean proceed() {
    Story story = story();
    LinkedHashMap<Source, Post> fetched = new LinkedHashMap<>();
    for (Source source : sections.getBase()) {
      Post post = story.get(source);
      if (post != null && !post.isEmpty())
        fetched.put(source, post);
    }
    story.clear();
    story.putAll(fetched);
    return proceed(SavePage.class, story);
  }

}
