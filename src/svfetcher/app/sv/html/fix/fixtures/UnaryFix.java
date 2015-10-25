package svfetcher.app.sv.html.fix.fixtures;

import svfetcher.app.sv.html.fix.AbstractFixture;
import svfetcher.app.sv.html.fix.HtmlParser;
import svfetcher.app.sv.html.fix.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class UnaryFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, (child) -> {
      if (HtmlParser.isUnary(child.tag))
        child.close();

      return child;
    });
  }

}
