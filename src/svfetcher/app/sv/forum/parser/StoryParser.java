package svfetcher.app.sv.forum.parser;

import ankh.utils.Strings;
import ankh.xml.dom.crawler.Crawler;
import java.util.ArrayList;
import org.w3c.dom.Node;
import svfetcher.app.story.Source;
import svfetcher.app.sv.forum.Post;
import svfetcher.app.sv.forum.Story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class StoryParser extends Parser<Story> {

  private static final XMatchers matchers = new XMatchers() {
    {
      add(new XMatcher(
        "//fieldset[contains(@class, 'breadcrumb')]/span/span/a/span",
        "//li[contains(@class, 'threadmarkItem')]/a"
      ));
      add(new XMatcher(
        "//fieldset[contains(@class, 'breadcrumb')]/span/span/a/span",
        "//li[contains(@class, 'memberListItem')]/a"
      ));
      add(new XMatcher(
        "//div[contains(@class, 'titleBar')]/h1",
        "(//div[contains(@class, 'messageContent')])[1]/article//a"
      ));
    }
  };

  static final String threadmarkXPath = "//li[contains(@class, 'threadmarkItem')]/a";
  static final String titleXPath = "//fieldset[contains(@class, 'breadcrumb')]/span/span/a/span";

  static final String titlePostXPath = "//div[contains(@class, 'titleBar')]/h1";
  static final String postXPath = "//div[contains(@class, 'messageContent')]/article";
  static final String linkXPath = "//a";

  @Override
  public Story fromPage(Node node, Object... args) {
    Story s = new Story();

    Crawler dom = new Crawler(node);
    XMatcher matcher = matchers.pickMatcher(dom);
    if (matcher == null)
      throw new RuntimeException("Can't parse threadmarks");

    s.setTitle(matcher.title());

    for (Node n : matcher.threadmarks()) {
      String url = attr(n, "href");

      if (url.matches("(?i)(https?://)?([^/]+)/members/[^\\.]+\\.\\d+.*"))
        continue;

      Source source = new Source(url);
      source.setName(Strings.trim(n.getTextContent(), "-, \t\r\n"));

      Post p = new Post();
      p.setSource(source);

      s.addSection(p);
    }

    return s;
  }

  @Override
  public Story fromPost(Node node, Object... args) {
    return fromPage(node, args);
  }

}

class XMatcher {

  private final String title;
  private final String threadmark;
  private final Crawler dom;

  private Crawler threadmarksNodes;
  private Node titleNode;

  public XMatcher(String title, String threadmark) {
    this(title, threadmark, null);
  }

  public XMatcher(String title, String threadmark, Crawler dom) {
    this.title = title;
    this.threadmark = threadmark;
    this.dom = dom;
  }

  String title() {
    if (titleNode == null && dom != null)
      titleNode = dom.filter(title).last();

    if (titleNode == null)
      return null;

    return titleNode.getTextContent();
  }

  Crawler threadmarks() {
    if (threadmarksNodes == null && dom != null)
      threadmarksNodes = dom.filter(threadmark);
    return threadmarksNodes;
  }

  XMatcher matches(Crawler dom) {
    XMatcher matcher = new XMatcher(title, threadmark, dom);
    return matcher.threadmarks().size() > 0 ? matcher : null;
  }

}

class XMatchers extends ArrayList<XMatcher> {

  XMatcher pickMatcher(Crawler dom) {
    XMatcher matched;
    for (XMatcher matcher : this)
      if ((matched = matcher.matches(dom)) != null)
        return matched;

    return null;
  }

}
