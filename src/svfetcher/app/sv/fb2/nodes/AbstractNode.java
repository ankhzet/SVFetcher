package svfetcher.app.sv.fb2.nodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class AbstractNode implements Node {

  static final Pattern p = Pattern.compile("([A-Z][^A-Z]*+)");

  String attributes;

  public String getAttributes() {
    return attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  @Override
  public String serialize() {
    String contents = getContents();
    if (contents == null || contents.isEmpty())
      return "";

    String tag = tagName();
    return (tag != null) ? wrap(contents, attributes != null ? tag + " " + attributes : tag, tag) : contents;
  }

  @Override
  public String tagName() {
    String name = getClass().getSimpleName();

    Matcher m = p.matcher(name);
    StringBuilder sb = new StringBuilder();
    while (m.find()) {
      if (sb.length() > 0)
        sb.append('-');
      sb.append(m.group(1).toLowerCase());
    }
    return sb.toString();
  }

  @Override
  public String toString() {
    return serialize();
  }

  protected static String wrap(String what, String openTag, String closeTag) {
    return String.format("<%s>%s</%s>", openTag, what, closeTag);
  }

}
