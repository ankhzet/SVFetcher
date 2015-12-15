package svfetcher.app.pages.config.ui;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AccessoriedConfigNode extends ConfigNode {

  EventHandler<ActionEvent> handler;
  String accessoryCaption = "...";

  public AccessoriedConfigNode(String caption, StringProperty property, EventHandler<ActionEvent> handler) {
    super(caption, property);
    this.handler = handler;
  }

  public void setAccessoryCaption(String accessoryCaption) {
    this.accessoryCaption = accessoryCaption;
  }

  @Override
  public Node buildNode() {
    Node node = super.buildNode();
    Button button = new Button(accessoryCaption);
    button.setOnAction(handler);
    HBox box = new HBox(2, node, button);
    HBox.setHgrow(node, Priority.ALWAYS);
    box.setAlignment(Pos.BASELINE_LEFT);
    return box;
  }

}
