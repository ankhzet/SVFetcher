package svfetcher.app.sv.html.fix;

import java.util.ArrayList;
import svfetcher.app.sv.html.fix.fixtures.*;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Fixer extends ArrayList<AbstractFixture> {

  public Fixer() {
    add(new CommentsFix());
    add(new EntityFix());
    add(new UnaryFix());
    add(new AttributesFix());
    add(new ScriptsFix());
  }

  public Node fix(Node root) {
    for (AbstractFixture fix : this)
      root = fix.apply(root);

    return root;
  }

}
