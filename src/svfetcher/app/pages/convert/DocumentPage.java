package svfetcher.app.pages.convert;

import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import ankh.tasks.RunTask;
import ankh.xml.dom.crawler.Crawler;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javax.xml.parsers.DocumentBuilder;
import org.controlsfx.control.action.Action;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import svfetcher.app.story.Source;
import svfetcher.app.sv.forum.Post;
import svfetcher.app.sv.forum.Story;
import svfetcher.app.pages.fetch.FetchPage;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DocumentPage extends AbstractPage {

  @DependencyInjection()
  protected DocumentBuilder builder;

  DataFetcher data;

  @Override
  public String pathFragment() {
    return "Document";
  }

  @Override
  protected javafx.scene.Node buildNode() {
    return new VBox(8, new Label(String.format(
                    "Document:\n\t%s\n\n"
                    + "Author:\n\t%s\n\n"
                    + "Title:\n\t%s",
                    basePath(),
                    data.getAuthor(),
                    data.getTitle()
                  )));
  }

  @Override
  protected void ready() {
    setTitle("Document");
    data = new DataFetcher(basePath(), document());
    notify("Ready to convert " + basePath(), new Action("Convert", h -> {
      convert();
    }));
  }

  Document document() {
    return navDataAtIndex(0);
  }

  String basePath() {
    return navDataAtIndex(1);
  }

  boolean convert() {
    return followup((TaskedResultSupplier<Node>) supplier -> {
      return supplier.get(() -> new RunTask<>(() -> filterSamLibDocument()))
        .setError("Failed to convert HTML")
        .schedule(root -> {
          Source base = new Source(data.getBasePath());
          base.setName(data.getTitle());

          Post post = new Post();
          post.setSource(base);
          post.setTitle(data.getTitle());
          post.setAuthor(data.getAuthor());
          post.setContentsFromXML(root);

          Story story = new Story();
          story.setSource(base);
          story.setTitle(data.getTitle());
          story.setAnnotation(data.getAnnotation());
          story.addSection(post);

          proceed(FetchPage.class, story);
        });
    });
  }

  Node filterSamLibDocument() throws IOException, SAXException {
    // todo: move to custom DataFetcher implementation
    Document document = document();

    Crawler dom = new Crawler(document.getDocumentElement());
    Crawler dds = dom.filter("//body/dd");
    Node hr = new Crawler(dds.last()).filter("//hr[@noshade]").first();

    if (hr != null) {
      Node parent = hr.getParentNode();
      while (hr != null) {
        Node n = hr;
        hr = hr.getNextSibling();

        parent.removeChild(n);
      }
    }

    String html = "<html><body><article>" + dds.toString() + "</article></body></html>";

    Document d = builder.parse(new ByteArrayInputStream(html.getBytes()));
    return Crawler.filter(d, "//article").first();
  }

}
