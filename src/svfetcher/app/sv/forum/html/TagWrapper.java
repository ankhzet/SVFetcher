package svfetcher.app.sv.forum.html;

import org.w3c.dom.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class TagWrapper extends NodeSerializer {

  String tag;
  NodeSerializer serializer;

  public TagWrapper(String tag, NodeSerializer serializer) {
    this.tag = tag;
    this.serializer = serializer;
  }

  @Override
  public void serialize(StringBuilder sb, NodeSerializer parent, Node node) {
    StringBuilder temp = new StringBuilder();
    serializer.serialize(temp, parent, node);

    if (temp.length() > 0) {
      sb.append("<").append(tag).append(">");
      sb.append(temp);
      sb.append("</").append(tag).append(">");
    }
  }

}
