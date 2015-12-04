package svfetcher.app.story.serialization.fb2.builder.nodes.description;

import java.util.Collection;
import svfetcher.app.story.serialization.fb2.builder.nodes.ContainerNode;
import svfetcher.app.story.serialization.fb2.builder.nodes.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Description extends ContainerNode {

  public Description() {
  }

  public Description(Collection<Node> childs) {
    super(childs);
  }

}
