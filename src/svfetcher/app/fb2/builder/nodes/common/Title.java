package svfetcher.app.fb2.builder.nodes.common;

import svfetcher.app.fb2.builder.nodes.ContainerNode;

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
