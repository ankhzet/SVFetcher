package svfetcher.app.sv.fb2.nodes;

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
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    for (Node child : childs)
      sb.append(child.serialize()).append("\n");
    return sb.toString();
  }
  
}
