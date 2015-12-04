package svfetcher.app.fb2.builder.nodes.common;

import java.text.SimpleDateFormat;
import svfetcher.app.fb2.builder.nodes.TextNode;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Date extends TextNode {

  static final SimpleDateFormat READABLE_FORMAT = new SimpleDateFormat("d.M.yyyy");
  static final SimpleDateFormat DIGITAL_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public Date() {
    this(new java.util.Date());
  }

  public Date(java.util.Date date) {
    super(READABLE_FORMAT.format(date));
    setAttributes(String.format("value=\"%s\"", DIGITAL_FORMAT.format(date)));
  }

}
