package ankh.fb2.fix.fixtures;

import ankh.xml.dom.Node;
import ankh.xml.fix.fixtures.AbstractFixture;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ScriptsFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, c -> {
      if (c.is("script")) {
        c.setContents(
          null
        );
        c.tag = null;
      }

      return c;
    });
  }

}
