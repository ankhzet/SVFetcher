package svfetcher.app.sv.forum;

import ankh.utils.Utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class User implements Parseable<User> {

  String profile, nickname, title;

  public User() {
  }

  public User(String profile, String nickname, String title) {
    this.profile = profile;
    this.nickname = nickname;
    this.title = title;
  }

  public String getNickname() {
    return nickname;
  }

  public String getProfile() {
    return profile;
  }

  public String getTitle() {
    return title;
  }

  @Override
  public SVParser<User> parser() {
    return new UserParser();
  }

}

class UserParser extends SVParser<User> {

  static final String pageXPath = "//li%s";
  static final String cntXPath = "//article";

  @Override
  public User fromPage(Node node, Object... args) {
    String userId = Utils.isAny(args);

    NodeList nodes = xPath(node, "//h3[contains(@class, 'userText')]");

    if (userId == null)
      return fromPost(first(nodes), args);

    int total = nodes.getLength();
    for (int i = 0; i < total; i++) {
      Node h = nodes.item(i);
      User u = fromPost(h, args);
      if (userId.equals(u.profile))
        return u;
    }

    return null;
  }

  @Override
  public User fromPost(Node post, Object... args) {
    Node user = isNode(post, "h3")
                ? post
                : first(xPath(post, "//h3[contains(@class, 'userText')]"));

    Node a = first(xPath(user, "a[contains(@class, 'username')]"));
    Node em = first(xPath(user, "em[contains(@class, 'userTitle')]"));
    String title = (em != null) ? em.getTextContent() : "";

    return new User(
            getProfileFragment(attr(a, "href")),
            a.getTextContent(),
            title
    );
  }

  static String getProfileFragment(String link) {
    Pattern p = Pattern.compile("members/([^/]+)/");
    Matcher m = p.matcher(link);
    if (m.find())
      return m.group(1);

    throw new RuntimeException(String.format("Can't parse user link [%s]", link));
  }

}
