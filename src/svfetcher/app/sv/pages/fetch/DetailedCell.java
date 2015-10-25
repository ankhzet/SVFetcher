package svfetcher.app.sv.pages.fetch;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Item>
 */
public class DetailedCell<Item> extends ListCell<Item> {

  static final String DEFAULT_STYLE2 = "-fx-background-color: transparent; -fx-text-fill: black;";
  static final String DEFAULT_STYLE = "-fx-background-color: white; -fx-text-fill: black;";
  static final Image accessoryImage = new Image(DetailedCell.class.getResource("/resources/accessory.png").toString());

  private final BorderPane graphic;
  private final BorderPane contents;
  private final VBox accPane;
  private final Pane infoHolder;

  private Node details = null;

  boolean folded = true;

  public DetailedCell() {
    setText(null);

    ImageView accessory = new ImageView(accessoryImage);

    accPane = new VBox(accessory);
    accPane.setPrefSize(24, 24);
    accPane.setPadding(new Insets(4));
    accPane.setBackground(Background.EMPTY);
    accPane.setEffect(new DropShadow(4, 2, 2, Color.BLACK));

    setAccessory(accessory, Pos.TOP_CENTER);

    contents = new BorderPane();
    infoHolder = new VBox();
    contents.setBottom(infoHolder);
    setContent(null);
    setDetail(null);

    graphic = new BorderPane();
    graphic.setLeft(accPane);
    graphic.setCenter(contents);
    setStyle(DEFAULT_STYLE2);
  }

  final public void setAccessory(Node accessory, Pos alignment) {
    accPane.getChildren().setAll(accessory);
    accPane.setAlignment(alignment);

    accessory.setOnMousePressed((event) -> {
      event.consume();
    });
    accessory.setOnMouseClicked((event) -> {
      event.consume();
      folded = !folded;
      updateFolded();
    });
  }

  final public void setContent(Node content) {
    contents.setTop(content);
  }

  final public void setDetail(Node content) {
    details = content;
    accPane.setVisible(details != null);
  }

  @Override
  protected void updateItem(Item item, boolean empty) {
    super.updateItem(item, empty);

    folded = true;
    if (empty || (item == null))
      setGraphic(null);
    else
      setGraphic(graphic);
  }

  @Override
  public void updateSelected(boolean selected) {
    super.updateSelected(selected);

    setStyle(DEFAULT_STYLE2);

    Node content = contents.getTop();
    if (content != null)
      if (selected)
        content.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
      else
        content.setStyle(DEFAULT_STYLE);

    if (selected)
      infoHolder.setStyle("-fx-background-color: lightblue; -fx-text-fill: black;");
    else
      infoHolder.setStyle(DEFAULT_STYLE);
  }

  void updateFolded() {
    if (accPane.getChildren().size() > 0)
      accPane.getChildren().get(0).setRotate(folded ? 0 : 90);

    if (folded) {
      infoHolder.getChildren().clear();
      infoHolder.setPadding(Insets.EMPTY);
    } else {
      infoHolder.getChildren().setAll(details);

      if (details != null)
        infoHolder.setPadding(new Insets(5, 0, 2, 10));
    }
  }

}
