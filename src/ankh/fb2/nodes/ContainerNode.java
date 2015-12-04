package ankh.fb2.nodes;

import ankh.utils.Strings;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ContainerNode extends AbstractNode {

  ArrayList<Node> childs = new ArrayList<>();

  public ContainerNode() {
  }

  public ContainerNode(Collection<Node> childs) {
    this.childs.addAll(childs);
  }

  public void setChilds(ArrayList<Node> childs) {
    this.childs.clear();
    this.childs.addAll(childs);
  }

  public void add(Node child) {
    childs.add(child);
  }

  public void addAll(Collection<Node> childs) {
    childs.addAll(childs);
  }

  @Override
  public String getContents() {
    boolean tabulate = tagName() != null;

    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    for (Node child : childs) {
      String serializedChild = child.serialize();
      sb.append(tabulate ? tabulate(serializedChild) : serializedChild)
        .append("\n");
    }

    return sb.toString();
  }

  protected String tabulate(String text) {
    return "\t" + Strings.trimr(text.replace("\n", "\n\t"), "\t");
  }

}
