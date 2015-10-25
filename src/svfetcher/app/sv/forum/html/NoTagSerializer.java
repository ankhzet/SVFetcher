package svfetcher.app.sv.forum.html;

import org.w3c.dom.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class NoTagSerializer extends NodeSerializer {

  @Override
  protected String tag(Node node) {
    return null;
  }

}
