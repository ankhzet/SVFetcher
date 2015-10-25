package svfetcher.app.sv.html.fix.fixtures;

import svfetcher.app.sv.html.fix.AbstractFixture;
import svfetcher.app.sv.html.fix.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class CommentsFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, c -> {
      if ((c.tag != null) && (c.tag.startsWith("!--") || c.tag.startsWith("![")))
        c.tag = null;

      return c;
    });
  }

}
