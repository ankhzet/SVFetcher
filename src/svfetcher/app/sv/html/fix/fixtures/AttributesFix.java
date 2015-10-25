package svfetcher.app.sv.html.fix.fixtures;

import svfetcher.app.sv.html.fix.AbstractFixture;
import svfetcher.app.sv.html.fix.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AttributesFix extends AbstractFixture {

  @Override
  public Node apply(Node node) {
    return recursive(node, c -> {
      if (c.attributes != null)
        c.attributes = runPattern(c.attributes, "(\\s+(([\\w\\d]+)=([^\"'\\s<>]+)))([^>]*)", (m) -> {
          String attr = m.group(3);
          String val = m.group(4);
          String rest = m.group(5);
          String htm = String.format(" %s=\"%s\"%s", attr, val, rest);
          return htm;
        });

      return c;
    });
  }

}
