package ankh.fb2.fix.fixtures;

import ankh.xml.dom.Node;
import ankh.xml.dom.TextNode;
import ankh.xml.fix.fixtures.AbstractFixture;

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
            contents.replace("\u00A0", " ").replace("&", "&amp;").replaceAll("&amp;([\\w\\d\\-_]+);", "&$1;")
          );
      }

      return child;
    });
  }

}
