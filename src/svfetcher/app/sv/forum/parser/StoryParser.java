package svfetcher.app.sv.forum.parser;

import svfetcher.app.sv.html.dom.crawler.Crawler;
import org.w3c.dom.Node;
import svfetcher.app.story.Source;
import svfetcher.app.sv.forum.Post;
import svfetcher.app.sv.forum.Story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class StoryParser extends Parser<Story> {

  static final String threadmarkXPath = "//li[contains(@class, 'threadmarkItem')]/a";
  static final String titleXPath = "//fieldset[contains(@class, 'breadcrumb')]/span/span/a/span";

  static final String titlePostXPath = "//div[contains(@class, 'titleBar')]/h1";
  static final String postXPath = "//div[contains(@class, 'messageContent')]/article";
  static final String linkXPath = "//a";

  @Override
  public Story fromPage(Node node, Object... args) {
    Story s = new Story();

    Crawler dom = new Crawler(node);
    Node title = dom.filter(titleXPath).last();
    s.setTitle(title.getTextContent());

    Crawler threadmarks = dom.filter(threadmarkXPath);
    for (Node n : threadmarks) {
      Source source = new Source(attr(n, "href"));
      source.setName(n.getTextContent());

      Post p = new Post();
      p.setSource(source);

      s.addSection(p);
    }

    return s;
  }

  @Override
  public Story fromPost(Node node, Object... args) {
    Story s = new Story();

    Crawler dom = new Crawler(node);
    Node title = dom.filter(titlePostXPath).last();
    s.setTitle(title.getTextContent());

    Crawler post = new Crawler(dom.filter(postXPath).first());
    Crawler links = post.filter(linkXPath);
    for (Node n : links) {
      Source source = new Source(attr(n, "href"));
      source.setName(n.getTextContent());

      Post p = new Post();
      p.setSource(source);

      s.addSection(p);
    }

    return s;
  }

}
