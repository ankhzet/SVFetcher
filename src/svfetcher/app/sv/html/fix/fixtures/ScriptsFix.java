package svfetcher.app.sv.html.fix.fixtures;

import svfetcher.app.sv.html.fix.AbstractFixture;
import svfetcher.app.sv.html.fix.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ScriptsFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, c -> {
      if (c.is("script")) {
        String contents = c.contents();
        if (contents != null)
          c.setContents(
            contents.replace("&", "&amp;")
          );
      }

      return c;
    });
  }

}
