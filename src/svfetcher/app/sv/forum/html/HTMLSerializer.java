package svfetcher.app.sv.forum.html;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class HTMLSerializer extends NodeSerializer {

  static final String DIV = "div";
  static final String ART = "article";
  static final String BR = "br";
  static final String P = "p";
  static final String I = "i";
  static final String B = "b";
  static final String BLOCKQUOTE = "blockquote";
  static final String INLINE = "strong|emphasis|a|img";

  public String serialize(Node node) {
    StringBuilder sb = new StringBuilder();
    serialize(sb, node);

    String result = sb.toString();
//    result = result.replaceAll("([\n\r]+\\s*)*</p>(\\s*[\n\r]+)*([ \t\u00A0]*<(" + INLINE + ")>)", "$3");
//    result = result.replaceAll("(</(" + INLINE + ")>\\s*)<p>(\\s*)", "$1$3");

    result = result.replaceAll("</p><p>(<(" + INLINE + ")>)", "$1");
    result = result.replaceAll("(</(" + INLINE + ")>)</p><p>", "$1");
    
    result = result.replaceAll("<p>\\s*</p>", "");
    result = result.replaceAll("</p>[ \t\u00A0]<p>", "</p>\n<p>");
    result = result.replace("<p>Click to expand...</p>", "");
    result = result.replaceAll("(" + INLINE + ")>([^\\s,\\.!\\?\"'])", "$1> $2");

    return result;
  }

  public void cleanup(Node node) {
    boolean isArticle = isNode(node, ART);
    if (isArticle || isNode(node, BLOCKQUOTE))
      setSerializer(node, new NoTagSerializer());

    if (isNode(node, BR))
      setSerializer(node, new NoTagSerializer());

    if (isNode(node, I))
      setSerializer(node, new ConvertToTag("emphasis"));

    if (isNode(node, B))
      setSerializer(node, new ConvertToTag("strong"));

    if (isNode(node, DIV))
      cleanupDIVNode(node);

    if (node.hasChildNodes()) {
      boolean inline = isInlineTag(node);

      NodeList childs = node.getChildNodes();
      for (int i = 0; i < childs.getLength(); i++) {
        Node child = childs.item(i);
        cleanup(child);

        NodeSerializer s = pullSerializer(child);
        if (!isTextNode(child))
          if (s instanceof TagWrapper) {
            setSerializer(child, s);
            continue;
          }

        if (isEmptyTextNode(child) || inline)
          continue;
        
        if (!insideInline(child.getParentNode()))
          setSerializer(child, new TagWrapper(P, s == null ? this : s));
      }
    }
  }

  boolean insideInline(Node node) {
    if (node == null)
      return false;

    if (isInlineTag(node))
      return true;

    return insideInline(node.getParentNode());
  }

  void cleanupDIVNode(Node node) {
    String style = attr(node, "style");
    if (style != null)
      if (style.equals("text-align: center")) {
        setSerializer(node, new ConvertToCenter());
        return;
      }

    setSerializer(node, new ConvertToTag("i"));
  }

}
