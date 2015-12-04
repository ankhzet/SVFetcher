package svfetcher.app.fb2.builder.nodes.description;

import ankh.xml.fix.XMLCleaner;
import svfetcher.app.fb2.builder.nodes.ContainerNode;
import svfetcher.app.fb2.builder.nodes.common.P;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Annotation extends ContainerNode {

  public Annotation() {
    super();
  }

  public Annotation(String contents) {
    add(new P(annotation(contents)));
  }

  private String annotation(String post) {
    int len = post.length();
    int pick = Math.min(len, 1024 * 10);
    if (pick < len) {
      post = post.substring(0, pick - 1);
      post = XMLCleaner.cleanup(post);
    }
    return post;
  }

}
