package svfetcher.app.sv.fb2.nodes.common;

import svfetcher.app.sv.fb2.nodes.TextNode;
import svfetcher.app.sv.fb2.nodes.ContainerNode;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Title extends ContainerNode {

  public Title() {
  }

  public Title(String title) {
    add(new P(title));
  }

}
