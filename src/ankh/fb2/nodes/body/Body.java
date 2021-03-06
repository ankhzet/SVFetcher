package ankh.fb2.nodes.body;

import java.util.Collection;
import ankh.fb2.nodes.ContainerNode;
import ankh.fb2.nodes.common.Title;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Body extends ContainerNode {

  public Body() {
  }

  public Body(String title, Collection<Section> sections) {
    add(new Title(title));
    for (Section section : sections)
      add(section);
  }

}
