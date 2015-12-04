package ankh.fb2.nodes;

import java.util.Collection;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class NamedNode extends ContainerNode {

  String name;

  public NamedNode(String name) {
    this.name = name;
  }

  public NamedNode(String name, Collection<Node> childs) {
    super(childs);
    this.name = name;
  }

  public NamedNode(String name, String textContent) {
    this(name);
    add(new TextContainerNode(textContent));
  }

  @Override
  public String tagName() {
    return name;
  }

}
