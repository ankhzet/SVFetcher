package svfetcher.app.pages.fetch;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class LinkPopover extends PopOver {

  private Source source;

  private Pane node;
  private Label nameLabel;
  private Label hrefLabel;

  public LinkPopover() {
    super();
    setDetachable(true);
    setDetached(false);
    setArrowSize(8);
    setCornerRadius(8);
    setArrowLocation(PopOver.ArrowLocation.LEFT_CENTER);
  }

  public void setSource(Source source) {
    this.source = source;

  }

  public void showNear(Node node) {
    setContentNode(getNode());
    setDetachedTitle("Section: " + source.getName());

    nameLabel.setText(source.getName());
    hrefLabel.setText(source.getUrl());

    super.show(node);
  }
  
  public void hideImmediately() {
    hide(Duration.ZERO);
  }

  Node getNode() {
    if (node == null) {
      nameLabel = new Label();
      hrefLabel = new Label();
      node = new VBox(8, nameLabel, hrefLabel);
      node.setPadding(new Insets(8));
      node.setPrefWidth(200);
    }

    return node;
  }

}
