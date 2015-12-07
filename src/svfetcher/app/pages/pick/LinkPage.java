package svfetcher.app.pages.pick;

import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import java.net.URL;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.action.Action;
import org.w3c.dom.Document;
import svfetcher.app.sv.DocumentFetchTask;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.Story;
import svfetcher.app.pages.convert.DocumentPage;
import svfetcher.app.pages.fetch.FetchPage;

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
    return "Pick url";
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

    String src = navDataAtIndex(0);
    if (src == null) {
//    src = "keeper-worm-x-dungeon-keeper.18920";
//    src = "who-needs-enemies-altpower-taylor-worm.22004";
//      src = "http://samlib.ru/r/raavasta/hollycow.shtml";
//      src = "http://samlib.ru/p/plotnikow_sergej_aleksandrowich/propavshaya.shtml";
    }

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
    return followup((TaskedResultSupplier<Document>) supplier -> {
      return supplier.get(() -> {
        if (path == null || path.isEmpty())
          throw new Exception("Can't parse link...");

        return new DocumentFetchTask(new URL(path));
      })
        .setError("Failed to load page at " + path)
        .schedule(document -> {
          proceed(DocumentPage.class, document, path);
        });
    });
  }

}
