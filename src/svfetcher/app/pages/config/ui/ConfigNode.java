package svfetcher.app.pages.config.ui;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConfigNode {

  protected int labelNodeSpace = 8;
  protected int labelWidth = 120;

  protected String caption;
  protected StringProperty property;

  public ConfigNode(String caption, StringProperty property) {
    this.caption = caption;
    this.property = property;
  }

  private Node node;

  Node getNode() {
    if (node == null) {
      Node editor = buildNode();

      Label label = new Label(caption);
      label.setLabelFor(editor);
      label.setAlignment(Pos.BASELINE_RIGHT);
      label.setPrefWidth(labelWidth);
      label.setMinWidth(labelWidth);
      HBox.setHgrow(editor, Priority.ALWAYS);

      HBox hbox = new HBox(labelNodeSpace, label, editor);
      hbox.setAlignment(Pos.BASELINE_LEFT);

      node = hbox;
    }

    return node;
  }

  protected Node editorNode;

  public Node buildNode() {
    if (editorNode == null) {
      TextField textField = new TextField();
      if (property != null)
        textField.textProperty().bindBidirectional(property);
      else
        textField.setDisable(true);
      editorNode = textField;
    }

    return editorNode;
  }

}
