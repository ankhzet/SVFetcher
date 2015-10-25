package svfetcher.app.sv.forum;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SVParser<T> {

  protected static final XPath xPath = XPathFactory.newInstance().newXPath();

  public T fromPage(Node node, Object... args) {
    return null;
  }

  public T fromPost(Node post, Object... args) {
    return null;
  }

  protected static NodeList xPath(Document doc, String xpath) {
    return xPath(doc.getDocumentElement(), xpath);
  }

  protected static NodeList xPath(Node node, String xpath) {
    try {
      NodeList nodes = (NodeList) xPath.evaluate(xpath, node, XPathConstants.NODESET);
      return new BackedList(nodes).keepChilds(node);
    } catch (XPathExpressionException ex) {
      throw new RuntimeException(ex);
    }
  }

  protected static Node first(NodeList nodes) {
    return nodes.getLength() > 0 ? nodes.item(0) : null;
  }

  protected static Node last(NodeList nodes) {
    return nodes.getLength() > 0 ? nodes.item(nodes.getLength() - 1) : null;
  }

  protected static String attr(Node node, String attr) {
    NamedNodeMap attribs = node.getAttributes();
    if (attribs == null)
      return null;

    Node attribute = attribs.getNamedItem(attr);
    if (attribute == null)
      return null;

    return attribute.getTextContent();
  }

  public static boolean isNode(Node node, String tag) {
    return node.getNodeName().toLowerCase().equals(tag);
  }

}

class BackedList extends ArrayList<Node> implements NodeList {

  public BackedList() {
  }

  public BackedList(Collection<? extends Node> c) {
    super(c);
  }

  public BackedList(NodeList c) {
    for (int i = 0; i < c.getLength(); i++)
      add(c.item(i));
  }

  public BackedList keepChilds(Node node) {
    BackedList copy = (BackedList) this.clone();
    copy.removeIf((child) -> {
      short t = node.compareDocumentPosition(child);
      return (t & Node.DOCUMENT_POSITION_CONTAINED_BY) == 0;
    });
    return copy;
  }

  @Override
  public Node item(int index) {
    return get(index);
  }

  @Override
  public int getLength() {
    return size();
  }

}
