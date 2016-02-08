package svfetcher.app.pages.config.ui;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.util.StringConverter;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class CheckboxConfigNode extends ConfigNode {

  private final String checkboxCaption;

  public CheckboxConfigNode(String caption, StringProperty property) {
    super("", property);
    this.checkboxCaption = caption;
  }

  public Node buildNode() {
    if (editorNode == null) {
      CheckBox checkbox = new CheckBox(checkboxCaption);
      if (property != null) {
        String value = property.get();
        property.bindBidirectional(checkbox.selectedProperty(), new StringConverter<Boolean>() {

          @Override
          public String toString(Boolean object) {
            return object != null ? object.toString() : Boolean.toString(false);
          }

          @Override
          public Boolean fromString(String string) {
            return Boolean.valueOf(string);
          }
        });
        property.set(value);
      } else
        checkbox.setDisable(true);
      editorNode = checkbox;
    }

    return editorNode;
  }

}
