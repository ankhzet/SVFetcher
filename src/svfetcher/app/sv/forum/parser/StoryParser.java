package svfetcher.app.sv.forum.parser;

import ankh.utils.Strings;
import ankh.xml.dom.crawler.Crawler;
import java.util.ArrayList;
import java.util.List;
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

  public String parseTitle(Node node) {
    return parseTitle(node, null);
  }
  
  public List<Source> parseThreadmarks(Node node) {
    return parseThreadmarks(node, null);
  }

  @Override
  public Story fromPage(Node node, Object... args) {
    XMatcher matcher = pickMatcher(node);

    Story s = new Story();
    s.setTitle(parseTitle(node, matcher));

    List<Source> threadmarks = parseThreadmarks(node, matcher);

    for (Source source : threadmarks) {
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

  private XMatcher pickMatcher(Node node) {
    Crawler dom = new Crawler(node);
    XMatcher matcher = matchers.pickMatcher(dom);
    if (matcher == null)
      throw new RuntimeException("Can't parse threadmarks");
    return matcher;
  }

  private String parseTitle(Node node, XMatcher matcher) {
    if (matcher == null)
      matcher = pickMatcher(node);
    
    return matcher.title();
  }

  private List<Source> parseThreadmarks(Node node, XMatcher matcher) {
    if (matcher == null)
      matcher = pickMatcher(node);

    List<Source> threadmarks = new ArrayList<>();
    for (Node n : matcher.threadmarks()) {
      String url = attr(n, "href");

      if (url.matches("(?i)(https?://)?([^/]+)/members/[^\\.]+\\.\\d+.*"))
        continue;

      Source source = new Source(url);
      source.setName(Strings.trim(n.getTextContent(), "-, \t\r\n"));

      threadmarks.add(source);
    }

    return threadmarks;
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
