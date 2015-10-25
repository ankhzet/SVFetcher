package svfetcher.app.sv.fb2.nodes.description;

import svfetcher.app.sv.fb2.nodes.ContainerNode;
import svfetcher.app.sv.fb2.nodes.common.P;
import svfetcher.app.sv.html.Cleaner;

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

  private String cleanupHTML(String html) {
//    html = html.replace("<br></br>", "<br />");

    return html;
  }

  private String annotation(String post) {
    int len = post.length();
    int pick = Math.min(len, 1024 * 10);
    if (pick < len) {
      post = post.substring(0, pick - 1);
      post = Cleaner.cleanup(post);
    }
    return cleanupHTML(post);
  }

}
