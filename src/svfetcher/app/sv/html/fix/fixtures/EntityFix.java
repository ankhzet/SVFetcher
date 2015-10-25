package svfetcher.app.sv.html.fix.fixtures;

import java.util.HashMap;
import svfetcher.app.sv.html.fix.AbstractFixture;
import svfetcher.app.sv.html.fix.Node;
import svfetcher.app.sv.html.fix.TextNode;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class EntityFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, (child) -> {
      if (child instanceof TextNode) {
        String contents = child.contents();
        if (contents != null && !contents.trim().isEmpty())
          child.setContents(
            convertEntities(contents)
          );
      }

      return child;
    });
  }

  static final HashMap<String, String> entityMapping = new HashMap<String, String>() {
    {
      put("trade", "\u2122");
      put("copy", "\u00A9");
      put("larr", "\u2190");
      put("uarr", "\u2191");
      put("rarr", "\u2192");
      put("darr", "\u2193");
      put("nbsp", "\u00A0");
    }
  };

  static String convertEntities(String html) {
    return runPattern(html, "&([\\w\\d_-]+);", (m) -> {
      String entity = m.group(1);
      String mapped = entityMapping.get(entity);
//      System.out.printf("%s -> %s", entity, mapped);
      return (mapped != null) ? mapped : "&" + entity + ";";
    }).replace("<", "&lt;").replace(">", "&gt;");
  }

}
