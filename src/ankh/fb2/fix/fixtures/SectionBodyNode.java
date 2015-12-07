package ankh.fb2.fix.fixtures;

import ankh.xml.dom.Node;
import ankh.xml.dom.RootNode;
import ankh.xml.dom.XMLParser;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SectionBodyNode extends RootNode {

  String elTag = "<empty-line />";

  public SectionBodyNode(Node contents) {
    super();

    String html = contents.contents();

    html = html.replace("Click to expand...", "");

    html = html.replace("\n\n", "\n");
    html = html.replaceAll("\n*" + elTag + "\n*", "\n\n");

    html = "<p>" + html.replace("\n", "</p>\n<p>") + "</p>";

    html = html.replaceAll("(\\s*<p>\\s*</p>\\s*)+", "\n" + elTag + "\n\n");

    XMLParser p = new XMLParser(html);
    Node root = p.parse();

    addChild(root);
  }

}
