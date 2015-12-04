package svfetcher.app.fb2.fix.fixtures;

import ankh.xml.dom.Node;
import ankh.xml.dom.TextNode;
import ankh.xml.fix.fixtures.AbstractFixture;
import java.util.HashMap;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SchemaFix extends AbstractFixture {

  private final String allowed = "section|title|subtitle|p|poem|v|epigraph|a|image|style|strong|emphasis|empty\\-line|cite";
  private final String outlined = "div|blockquote";

  private final HashMap<String, String> converted = new HashMap<String, String>() {
    {
      put("img", "image");
      put("i", "emphasis");
      put("b", "strong");
      put("aside", "cite");
      put("hr", "empty-line");
      put("dd", "p");
      put("h1", "subtitle");
      put("h2", "subtitle");
      put("h3", "subtitle");
      put("h4", "subtitle");
      put("h5", "subtitle");
      put("h6", "subtitle");
      put("h7", "subtitle");
      put("h8", "subtitle");
      put("ul", "poem");
      put("ol", "poem");
      put("li", "v");
//      put("tr", "v");
//      put("table", "poem");
//      put("tr", "v");
    }
  };

  private final String elHtml = "\n<empty-line />\n";

  @Override
  public Node apply(Node node) {
    return recursive(node, c -> {
      if (c.tag == null)
        return c;

      if (c.tag.matches("(?i)" + outlined) && !(c instanceof TextNode))
        c.addChild(emptyLineNode());

      String convert = converted.get(c.tag);
      if (convert != null)
        c.tag = convert;

      if (!c.tag.matches("(?i)" + allowed))
        if (c.attributes.contains("text-align: center")) {
          c.attributes = null;
          c.tag = "subtitle";
        } else
          c.tag = null;

      return c;
    });
  }

  Node emptyLineNode() {
    return new TextNode(elHtml, 0, elHtml.length());
  }

}
