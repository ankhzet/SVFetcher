package svfetcher.app.sv.forum;

import ankh.utils.Utils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import svfetcher.app.sv.forum.html.HTMLSerializer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Post implements Parseable<Post> {

  Link link;
  User creator;

  Node contents;

  public Post() {
  }

  public Post(Link link, User creator, Node contents) {
    this.link = link;
    this.creator = creator;
    this.contents = contents;
  }

  public Link getLink() {
    return link;
  }

  public User getCreator() {
    return creator;
  }

  private String cachedContents;

  public String getContents() {
    if (cachedContents == null) {
      HTMLSerializer cleaner = new HTMLSerializer();
      cleaner.cleanup(contents);
      cachedContents = cleaner.serialize(contents);
    }
    return cachedContents;
  }

  @Override
  public SVParser<Post> parser() {
    return new PostParser();
  }

}

class PostParser extends SVParser<Post> {

  static final String pageXPath = "//li%s";

  @Override
  public Post fromPage(Node node, Object... args) {
    String selector = Utils.pass(Utils.isAny(args), (uid) -> {
      return ((uid == null) || uid.isEmpty()) ? "[contains(@id, 'post')]" : "[contains(@id, '" + uid + "')]";
    });

    NodeList nodes = xPath(node, "//li" + selector);

    int total = nodes.getLength();

    return (total > 0) ? fromPost(nodes.item(0), args) : null;
  }

  @Override
  public Post fromPost(Node post, Object... args) {
    Node article = first(xPath(post, "//article"));

    return new Post(
            Utils.anyOf(args, Link.class),
            new User().parser().fromPost(post),
            article
    );
  }

}
