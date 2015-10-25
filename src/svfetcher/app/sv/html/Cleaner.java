package svfetcher.app.sv.html;

import svfetcher.app.sv.html.fix.Fixer;
import svfetcher.app.sv.html.fix.HtmlParser;
import svfetcher.app.sv.html.fix.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Cleaner extends Fixer {

  static final Cleaner cleaner = new Cleaner();

  public static String cleanup(String html) {
    HtmlParser p = new HtmlParser(html);
    Node root = p.parse();

    root = cleaner.fix(root);

    return root.toString();
  }

}
