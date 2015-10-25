package svfetcher.app.sv.fb2.nodes;

import java.util.Collection;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ContainerContainerNode extends ContainerNode {

  public ContainerContainerNode() {
  }

  public ContainerContainerNode(Collection<Node> childs) {
    super(childs);
  }

  @Override
  public String tagName() {
    return null;
  }

}
