package svfetcher.app.sv.pages.pick;

import svfetcher.app.sv.pages.fetch.FetchPage;
import ankh.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import ankh.pages.Page;
import ankh.utils.Utils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.action.Action;
import svfetcher.app.sv.SV;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class LinkPage extends AbstractPage {

  @DependencyInjection()
  protected SV sv;

  Node node;
  TextField urlField;
  Button fetchButton;

  @Override
  public String title() {
    return "Pick url to fetch";
  }

  @Override
  public boolean navigateOut(Page to) {
    return Utils.<Boolean>pass(super.navigateOut(to), (n) -> {
      if (n) {
        node = null;
        dissmissNotifier();
      }
      return n;
    });
  }

  @Override
  public Node getNode() {
    if (node == null) {
      urlField = new TextField("keeper-worm-x-dungeon-keeper.18920");
      urlField.setPromptText("Enter forum thread URL here...");
      urlField.textProperty().addListener((l, o, n) -> {
        ready();
      });

      node = new VBox(8, urlField);
      ready();
    }
    return node;
  }

  void ready() {
    String url = urlField.getText();

    if (url == null || url.isEmpty()) {
      dissmissNotifier();
      return;
    }

    notify("Ready to check threadmarks", new Action("Check", (h) -> {
      fetch(urlField.getText());
    }));
  }

  void fetch(String url) {
    Utils.safely(() -> {
      if (url == null || url.isEmpty() || !sv.isSVLink(url))
        throw new Exception("Can't parse link... Is it pointing at SV thread?");

      sv.threadmarks(url, (request, story) -> {
        if (story != null)
          getNavigator().navigateTo(FetchPage.class, story);
        else
          error("Failed to check threadmarks at " + url, request.getFailure());
      });
    }, (e) -> {
      error("Failed to check threadmarks at " + url, e);
    });
  }

}
