package ankh.fb2.fix.fixtures;

import ankh.xml.fix.Fixer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FB2Fixer extends Fixer {

  public FB2Fixer() {
    add(new ScriptsFix());
    add(new SchemaFix());
    add(new ParagraphFix());
    add(new EntityFix());
  }

}
