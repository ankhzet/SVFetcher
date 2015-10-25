package svfetcher.app.sv.fb2;

import svfetcher.app.sv.fb2.nodes.ContainerNode;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FB2 extends ContainerNode {

  static final String xmlns = " xmlns=\"http://www.gribuser.ru/xml/fictionbook/2.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:l=\"http://www.w3.org/1999/xlink\"";

  public FB2() {
  }

  @Override
  public String tagName() {
    return "FictionBook";
  }

  @Override
  public String serialize() {
    String contents = getContents();
    String tag = tagName();
    return String.format("<%s %s>\n%s\n</%s>", tag, xmlns, contents, tag);
  }

}
