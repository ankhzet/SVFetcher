package svfetcher.app.sv.forum.html;

import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConvertToCenter extends ConvertToTag {

  public ConvertToCenter() {
    super("center");
  }

  @Override
  void flatenAttributes(StringBuilder sb, NamedNodeMap attr) {
  }

}
