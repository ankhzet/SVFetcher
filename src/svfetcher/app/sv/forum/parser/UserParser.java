package svfetcher.app.sv.forum.parser;

import ankh.utils.Utils;
import svfetcher.app.sv.html.dom.crawler.Crawler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Node;
import svfetcher.app.story.Source;
import svfetcher.app.sv.forum.User;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UserParser extends Parser<User> {

  static final String pageXPath = "//li%s";
  static final String cntXPath = "//article";

  @Override
  public User fromPage(Node node, Object... args) {
    String userId = Utils.isAny(args);

    Crawler dom = new Crawler(node).filter("//h3[contains(@class, 'userText')]");

    if (userId == null)
      return fromPost(dom.first(), args);

    Source source = new Source(userId);
    for (Node n : dom) {
      User u = fromPost(n, args);
      if (u.getSource().equals(source))
        return u;
    }

    return null;
  }

  @Override
  public User fromPost(Node post, Object... args) {
    Node user = isNode(post, "h3")
                ? post
                : Crawler.filter(post, "//h3[contains(@class, 'userText')]").first();

    Crawler uDom = new Crawler(user);

    Node a = uDom.filter("a[contains(@class, 'username')]").first();
    Node em = uDom.filter("em[contains(@class, 'userTitle')]").first();
    String title = (em != null) ? em.getTextContent() : "";

    Source source = new Source("members/" + getProfileFragment(attr(a, "href")) + "/");

    User u = new User();
    u.setSource(source);
    u.setName(a.getTextContent());
    u.setTitle(title);
    return u;
  }

  static String getProfileFragment(String link) {
    Pattern p = Pattern.compile("members/([^/]+)/");
    Matcher m = p.matcher(link);
    if (m.find())
      return m.group(1);

    throw new RuntimeException(String.format("Can't parse user link [%s]", link));
  }

}
