package svfetcher.app.sv.forum.parser;

import ankh.ioc.annotations.DependencyInjection;
import ankh.utils.Utils;
import ankh.xml.dom.crawler.Crawler;
import org.w3c.dom.Node;
import svfetcher.app.story.Source;
import svfetcher.app.sv.forum.Post;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class PostParser extends Parser<Post> {

  static final String pageXPath = "//li%s";

  @DependencyInjection()
  protected UserParser userParser;

  @Override
  public Post fromPage(Node node, Object... args) {
    String selector = Utils.pass(Utils.isAny(args), (uid) -> {
      return ((uid == null) || uid.isEmpty()) ? "post" : uid;
    });

    Crawler dom = Crawler.filter(node, "//li[contains(@id, '%s')]", selector);

    return dom.size() > 0 ? fromPost(dom.first(), args) : null;
  }

  @Override
  public Post fromPost(Node post, Object... args) {
    Crawler dom = Crawler.filter(post, "//article");

    dom.remove("//*[contains(@class, 'JsOnly')]");

    Post p = new Post();
    p.setSource(Utils.anyOf(args, Source.class));
    p.setAuthor(userParser.fromPost(post));
    p.setContentsFromXML(dom.first());

    return p;
  }

}
