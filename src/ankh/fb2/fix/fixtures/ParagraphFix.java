package ankh.fb2.fix.fixtures;

import ankh.xml.dom.Node;
import ankh.xml.fix.fixtures.AbstractFixture;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ParagraphFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return new SectionBodyNode(node);
  }

}
