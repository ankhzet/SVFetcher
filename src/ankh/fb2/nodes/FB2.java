package ankh.fb2.nodes;

import ankh.fb2.nodes.ContainerNode;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FB2 extends ContainerNode {

  static final String xmlns = "xmlns=\"http://www.gribuser.ru/xml/fictionbook/2.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:l=\"http://www.w3.org/1999/xlink\"";

  public FB2() {
    setAttributes(xmlns);
  }

  @Override
  public String tagName() {
    return "FictionBook";
  }

}
