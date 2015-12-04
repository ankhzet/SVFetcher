package svfetcher.app.sv.pages.convert;

import ankh.utils.Strings;
import ankh.xml.dom.XMLParser;
import ankh.xml.dom.crawler.Crawler;
import java.net.MalformedURLException;
import java.net.URL;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import svfetcher.app.fb2.fix.fixtures.FB2Fixer;
import svfetcher.app.story.Author;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DataFetcher {

  URL url;
  Document document;
  private Author author;
  private String title;
  private String annotation;

  public DataFetcher(String url, Document document) {
    try {
      this.url = new URL(url);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
    this.document = document;
  }

  Author getAuthor() {
    if (author == null)
      fetch();
    return author;
  }

  String getTitle() {
    if (title == null)
      fetch();
    return title;
  }

  String getBasePath() {
    return url.toString().replaceAll("\\.shtml$", "");
  }

  String getAnnotation() {
    if (annotation == null)
      fetch();
    return annotation;
  }

  void fetch() {
    Node titleNode = Crawler.xPath(document, "/html/head/title").first();
    String titleText = titleNode.getTextContent();
    Node a = Crawler.xPath(document, "/html/body/div/h3/small/a").first();
    Node h3 = a.getParentNode().getParentNode();
    String authorLink = a.getAttributes().getNamedItem("href").getTextContent();
    String authorStr = h3.getTextContent().trim();
    int i = 0, tl = titleText.length();
    for (; i < authorStr.length(); i++)
      if (i >= tl || titleText.charAt(i) != authorStr.charAt(i))
        break;
    title = Strings.trim(titleText.substring(i), "\n\r \t.");
    String authorName = authorStr.substring(0, i);
    
    author = new Author();
    author.setSource(new Source(authorLink));
    author.setName(authorName);

    Crawler annotationNode = new Crawler(Crawler.xPath(document, "/html/body/center/table//ul//font").first());

    XMLParser parser = new XMLParser(annotationNode.toString());

    ankh.xml.dom.Node aN = parser.parse();
    FB2Fixer fixer = new FB2Fixer();
    aN = fixer.fix(aN);

    annotation = aN.toString();
  }

}
