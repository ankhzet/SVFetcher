package svfetcher.app.sv.fb2.nodes.common;

import java.text.SimpleDateFormat;
import svfetcher.app.sv.fb2.nodes.TextNode;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Date extends TextNode {

  public Date() {
    this(new java.util.Date());
  }

  public Date(java.util.Date date) {
    super(new SimpleDateFormat("d.m.yy").format(date));
  }

}
