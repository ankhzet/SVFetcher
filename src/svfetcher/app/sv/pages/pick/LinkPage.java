package svfetcher.app.sv.pages.pick;

import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import java.net.URL;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.action.Action;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.Story;
import svfetcher.app.sv.pages.fetch.FetchPage;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class LinkPage extends AbstractPage {

  @DependencyInjection()
  protected SV sv;

  TextField urlField;

  @Override
  public String pathFragment() {
    return "Link";
  }

  @Override
  public Node buildNode() {
    urlField = new TextField();
    urlField.setPromptText("Enter forum thread URL here...");
    urlField.textProperty().addListener((l, o, url) -> {
      if (url == null || url.isEmpty()) {
        dissmissNotifier();
        return;
      }

      notify("Ready to check threadmarks",
             new Action("Check", h -> fetchLinks(urlField.getText())),
             new Action("HTML -> FB2", h -> convertPage(urlField.getText()))
      );
    });

    String src = "";
    urlField.setText(src);

    return new VBox(
      8, urlField);
  }

  @Override
  protected void ready() {
    setTitle("Pick url to fetch");
  }

  boolean fetchLinks(String url) {
    return followup((TaskedResultSupplier<Story>) supplier -> {
      return supplier.get(() -> {
        String threadSlug = sv.isSVLink(url);
        if (threadSlug == null)
          throw new Exception("Can't parse link... Is it pointing at SV thread?");

        return new FetchStoryLinksTask(sv, url);
      })
        .setError("Failed to check threadmarks at " + url)
        .schedule(story -> {
          proceed(FetchPage.class, story);
        });
    });
  }

  boolean convertPage(String path) {
    });
  }

}
