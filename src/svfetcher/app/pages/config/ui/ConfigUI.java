package svfetcher.app.pages.config.ui;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConfigUI extends ArrayList<ConfigNode> {

  protected int nodeToNodeSpace = 8;

  Node node;

  public ConfigUI(ConfigNode... nodes) {
    for (ConfigNode n : nodes)
      add(n);
  }

  public Node getNode() {
    if (node == null) {
      VBox vbox = new VBox(nodeToNodeSpace);
      ObservableList<Node> list = vbox.getChildren();
      for (ConfigNode configNode : this)
        list.add(configNode.getNode());
      node = vbox;
    }

    return node;
  }

}
