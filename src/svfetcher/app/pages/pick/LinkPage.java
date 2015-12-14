package svfetcher.app.pages.pick;

import ankh.http.cached.CacheableClient;
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

  @DependencyInjection()
  protected CacheableClient cache;

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

      ready();
    });

    urlField.setText(navDataAtIndex(0));

    return new VBox(8, urlField);
  }

  @Override
  protected void ready() {
    setTitle("Pick url to fetch");
    showNotifier();
    urlField.setDisable(false);
  }

  void showNotifier() {
    Action action;
    String message = "Ready to fetch thread";
    if (isSVLink())
      action = new Action("Fetch", h -> fetchLinks(urlField.getText()));
    else {
      message = "Ready to fetch HTML page";
      action = new Action("Fetch", h -> convertPage(urlField.getText()));
    }

    notify(message, action);
  }

  boolean isSVLink() {
    String threadSlug = sv.isSVLink(urlField.getText());
    return threadSlug != null;
  }

  boolean fetchLinks(String url) {
    return followup((TaskedResultSupplier<Story>) supplier -> {
      return supplier.get(() -> {
        urlField.setDisable(true);
        cache.forget(sv.threadLink(url));
        cache.forget(sv.threadmarksLink(url));

        return new FetchStoryLinksTask(sv, url);
      })
        .setOnCancelled(h -> ready())
        .setOnFailed(h -> ready())
        .setError("Failed to fetch thread at " + url)
        .schedule(story -> {
          proceed(FetchPage.class, story);
        });
    });
  }

  boolean convertPage(String path) {
    return followup((TaskedResultSupplier<Document>) supplier -> {
      return supplier.get(() -> {
        urlField.setDisable(true);
        return new DocumentFetchTask(new URL(path));
      })
        .setOnCancelled(h -> ready())
        .setOnFailed(h -> ready())
        .setError("Failed to load HTML page at " + path)
        .schedule(document -> {
          proceed(DocumentPage.class, document, path);
        });
    });
  }

}
