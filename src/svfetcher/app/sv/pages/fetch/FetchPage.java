package svfetcher.app.sv.pages.fetch;

import ankh.IoC;
import ankh.pages.AbstractPage;
import ankh.pages.Page;
import ankh.utils.Utils;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;
import svfetcher.app.sv.forum.Link;
import svfetcher.app.sv.forum.Story;
import svfetcher.app.sv.pages.compose.ComposePage;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FetchPage extends AbstractPage {

  Node node;

  private LinkPopover popOver;

  FetchTask fetchTask = null;

  @Override
  public String title() {
    return "Fetching...";
  }

  Story story() {
    return navDataAtIndex(0, () -> new Story());
  }

  @Override
  public Node getNode() {
    if (node == null) {
      LinkList view = new LinkList(FXCollections.observableArrayList(story().threadmarks()));
      view.setOnSelect((cell) -> {
        if (popOver != null && popOver.isShowing())
          popOver.hide(Duration.ZERO);

        Link link = cell.getItem();
        popOver = new LinkPopover();
        popOver.setLink(link);
        popOver.showNear(cell);
      });

      node = view;
    }

    return node;
  }

  @Override
  public boolean navigateIn(Page from, Object... args) {
    return Utils.pass(super.navigateIn(from, args), (n) -> {
      if (n)
        ready();
      return n;
    });
  }

  @Override
  public boolean navigateOut(Page to) {
    return Utils.<Boolean>pass(super.navigateOut(to), (n) -> {
      if (n && stop()) {
        dissmissNotifier();
        node = null;
        return true;
      }
      return false;
    });
  }

  boolean fetch() {
    if (!Utils.safely(() -> {
      fetchTask = IoC.resolve(new FetchTask(story()));
    }))
      return false;

    if (!fetchTask.hasUnfinished()) {
      done();
      return true;
    }

    fetchTask.setOnSucceeded((h) -> {
      fetch();
    });

    fetchTask.setOnFailed((h) -> {
      stop();
    });

    fetchTask.setOnCancelled((h) -> {
      ready();
    });

    if (perform(fetchTask)) {
      dissmissNotifier();

      return true;
    } else
      return false;
  }

  boolean stop() {
    return (fetchTask == null) || !fetchTask.isRunning() || fetchTask.isCancelled() || fetchTask.cancel();
  }

  void done() {
    stop();

    getNavigator().navigateTo(ComposePage.class, story());
  }

  void ready() {
    notify("Ready to fetch posts", new Action("Fetch", (h) -> {
      if (fetch())
//        getNavigator().dissmissNotifier();
      ;
    }));
  }

}
