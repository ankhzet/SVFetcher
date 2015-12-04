package svfetcher.app.fb2;

import ankh.xml.dom.Node;
import ankh.xml.dom.XMLParser;
import ankh.xml.fix.Fixer;
import svfetcher.app.fb2.fix.fixtures.FB2Fixer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FB2Cleaner {

  private static final Fixer fixer = new FB2Fixer();

  public static String cleanup(String html) {
    html = html.replaceAll("([\n\r]+[ \t]*)|(</(p|dd)>)", "");
    html = html.replaceAll("(?i)<(p|br|dd)(\\s*/)?>", "<br />\n");
    html = html.replace("\u00A0", " ");

    XMLParser p = new XMLParser(html);
    Node root = p.parse();

    root = fixer.fix(root);

    return root.toString();
  }

  protected Fixer fixer() {
    return fixer;
  }

}
