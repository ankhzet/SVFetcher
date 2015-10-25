package svfetcher.app.sv.forum.html;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import svfetcher.app.sv.forum.SVParser;
import svfetcher.app.sv.forum.Story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class NodeSerializer extends SVParser<Story> {

  static final String key = NodeSerializer.class.getSimpleName();
  static final Pattern whitespacer = Pattern.compile("\\S");

  static final Set<String> noContentTags = new HashSet<String>() {
    {
      add("br");
      add("img");
    }
  };

  static final Set<String> inlineTags = new HashSet<String>() {
    {
      add("i");
      add("b");
      add("s");
      add("sup");
      add("sub");
      add("a");
      add("img");
    }
  };

  static final Set<String> skipAttribute = new HashSet<String>() {
    {
      add("class");
      add("style");
    }
  };

  NodeSerializer parent;

  public void serialize(StringBuilder sb, Node node) {
    serialize(sb, null, node);
  }

  NodeSerializer parent() {
    return (parent != null) ? parent : this;
  }

  public void serialize(StringBuilder sb, NodeSerializer parent, Node node) {
    this.parent = parent;

    NodeSerializer s = pullSerializer(node);
    if (s != null) {
      s.serialize(sb, parent(), node);
      return;
    }

    switch (node.getNodeType()) {
    case Node.CDATA_SECTION_NODE:
    case Node.TEXT_NODE:
    case Node.ENTITY_NODE:
      String contents = node.getTextContent();
      Matcher m = whitespacer.matcher(contents);
      if (m.find())
        contents = contents.substring(m.start());

      sb.append(contents);
      break;
    default:
      boolean noContent = noContent(node);

      String tag = tag(node);
      if (tag != null) {
        sb.append("<").append(tag);
        flatenAttributes(sb, node.getAttributes());
        if (noContent)
          sb.append(" /");
        sb.append(">");
      }

      if (!noContent) {
        serializeChilds(sb, node);

        if (tag != null)
          sb.append("</").append(tag).append(">");
      }
    }
  }

  void serializeChilds(StringBuilder sb, Node node) {
    NodeSerializer p = parent();

    NodeList childs = node.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++)
      p.serialize(sb, p, childs.item(i));
  }

  protected String tag(Node node) {
    return node.getNodeName();
  }

  protected boolean noContent(Node node) {
    return noContentTags.contains(tag(node));
  }

  protected boolean noAttribute(Node node, String name) {
    return true;//skipAttribute.contains(name) || name.startsWith("data-");
  }

  void flatenAttributes(StringBuilder sb, NamedNodeMap attr) {
    for (int i = 0; i < attr.getLength(); i++) {
      Node a = attr.item(i);
      String name = a.getNodeName();
      if (noAttribute(a, name))
        continue;

      sb.append(" ").append(name)
              .append("=\"").append(a.getNodeValue()).append("\"");
    }
  }

  protected void setSerializer(Node node, NodeSerializer s) {
    node.setUserData(NodeSerializer.key, s, null);
  }

  protected boolean hasSerializer(Node node) {
    return node.getUserData(NodeSerializer.key) != null;
  }

  protected NodeSerializer pullSerializer(Node node) {
    NodeSerializer s = (NodeSerializer) node.getUserData(NodeSerializer.key);
    setSerializer(node, null);
    return s;
  }

  boolean isInlineTag(Node node) {
    return inlineTags.contains(tag(node));
  }

  boolean isTextNode(Node node) {
    switch (node.getNodeType()) {
    case Node.CDATA_SECTION_NODE:
    case Node.TEXT_NODE:
    case Node.ENTITY_NODE:
      return true;
    default:

    }
    return false;
  }

  boolean isEmptyTextNode(Node node) {
    if (isTextNode(node))
      return (node.getTextContent()).trim().isEmpty();
    return false;
  }

}
