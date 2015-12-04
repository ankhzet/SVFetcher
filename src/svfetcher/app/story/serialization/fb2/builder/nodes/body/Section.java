package svfetcher.app.story.serialization.fb2.builder.nodes.body;

import svfetcher.app.story.serialization.fb2.builder.nodes.TextContainerNode;
import svfetcher.app.story.serialization.fb2.builder.nodes.ContainerNode;
import svfetcher.app.story.serialization.fb2.builder.nodes.common.Title;

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
