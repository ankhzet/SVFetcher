package svfetcher.app.sv.forum.parser;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Result>
 */
public class Parser<Result> {

  public Result fromPage(Node node, Object... args) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  public Result fromPost(Node post, Object... args) {
    throw new UnsupportedOperationException("Not implemented.");
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
