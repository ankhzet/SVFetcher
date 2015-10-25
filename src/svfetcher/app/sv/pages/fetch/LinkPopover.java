package svfetcher.app.sv.pages.fetch;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import svfetcher.app.sv.forum.Link;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class LinkPopover extends PopOver {

  private Link link;

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

  public void setLink(Link link) {
    this.link = link;

  }

  public void showNear(Node node) {
    setContentNode(getNode());
    setDetachedTitle("Threadmark: " + link.getName());

    nameLabel.setText(link.getName());
    hrefLabel.setText(link.getHref());

    super.show(node);
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
