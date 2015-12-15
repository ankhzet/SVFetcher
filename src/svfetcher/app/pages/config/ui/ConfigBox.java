package svfetcher.app.pages.config.ui;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConfigBox extends ConfigNode {

  protected int nodeToNodeSpace = 8;

  ArrayList<ConfigNode> childs = new ArrayList<>();

  public ConfigBox(String caption) {
    super(caption, null);
  }

  public void add(ConfigNode... nodes) {
    for (ConfigNode n : nodes)
      childs.add(n);
  }

  private Node node;

  Node getNode() {
    if (node == null)
      node = new TitledPane(caption, buildNode());

    return node;
  }

  public Node buildNode() {
    VBox vbox = new VBox(nodeToNodeSpace);
    ObservableList<Node> list = vbox.getChildren();
    for (ConfigNode configNode : childs)
      list.add(configNode.getNode());
    return vbox;
  }

}
