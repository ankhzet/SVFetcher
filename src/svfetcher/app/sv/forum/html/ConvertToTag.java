package svfetcher.app.sv.forum.html;

import org.w3c.dom.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConvertToTag extends NodeSerializer {

  String tag;

  public ConvertToTag(String tag) {
    this.tag = tag;
  }

  @Override
  protected String tag(Node node) {
    return tag;
  }

}
