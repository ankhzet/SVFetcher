package svfetcher.app.sv.fb2.nodes.body;

import svfetcher.app.sv.fb2.nodes.TextContainerNode;
import svfetcher.app.sv.fb2.nodes.ContainerNode;
import svfetcher.app.sv.fb2.nodes.common.Title;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Section extends ContainerNode {

  public Section() {
  }

  public Section(String title, String contents) {
    add(new Title(title));
    add(new TextContainerNode(cleanupHTML(contents)));
  }

  final String cleanupHTML(String html) {
    html = html.replace("<br></br>", "<br />");
    
    return html;
  }
}
