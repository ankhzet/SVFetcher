package svfetcher.app.pages.fetch;

import svfetcher.app.pages.fetch.stated.StatedSource;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SourceCell extends ListCell<StatedSource<Source>> {

  Label title = new Label();
  Label link = new Label();
  StackPane status;
  ProgressIndicator progress;

  Node graphic;
  
  ObservableList<StatedSource<Source>> list;

  public SourceCell(ObservableList<StatedSource<Source>> list) {
    this.list = list;
    setText(null);
    getStyleClass().add("cell");

    title.getStyleClass().add("title");
    link.getStyleClass().add("link");

    progress = new ProgressIndicator();
    progress.getStyleClass().add("progress");

    Button delete = new Button("X");
    delete.getStyleClass().add("delete");
    delete.setOnAction((h) -> {
      StatedSource<Source> stated = getItem();
      if (stated != null)
        list.remove(stated);
    });

    status = new StackPane(progress, delete);
    status.getStyleClass().add("status");
    status.setAlignment(Pos.CENTER);
    status.setOnMouseClicked(h -> {
      StatedSource<Source> stated = getItem();
      stated.setDeleting(!stated.isDeleting());
    });

    VBox i = new VBox(title, link);
    i.getStyleClass().add("info");
    HBox.setHgrow(i, Priority.ALWAYS);
    HBox.setHgrow(i, Priority.ALWAYS);

    HBox hbox = new HBox(8, status, i);
    hbox.setAlignment(Pos.CENTER);

    graphic = hbox;
  }

  @Override
  protected void updateItem(StatedSource<Source> item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null)
      setGraphic(null);
    else {
      title.setText(item.getItem().getName());
      link.setText(item.getItem().getUrl());

      fetchingPropertyImpl().bind(item.stateProperty(FETCHING_PSEUDOCLASS.getPseudoClassName()));
      fetchedPropertyImpl().bind(item.stateProperty(FETCHED_PSEUDOCLASS.getPseudoClassName()));
      deletingPropertyImpl().bind(item.stateProperty(DELETING_PSEUDOCLASS.getPseudoClassName()));

      setGraphic(graphic);
    }
  }

  private ReadOnlyBooleanWrapper fetching;

  private ReadOnlyBooleanWrapper fetchingPropertyImpl() {
    if (fetching == null)
      fetching = new PseudoClassStateProperty(this, FETCHING_PSEUDOCLASS);
    return fetching;
  }

  private ReadOnlyBooleanWrapper fetched;

  private ReadOnlyBooleanWrapper fetchedPropertyImpl() {
    if (fetched == null)
      fetched = new PseudoClassStateProperty(this, FETCHED_PSEUDOCLASS) {

        @Override
        protected void invalidated() {
          super.invalidated();

          StatedSource<Source> stated = getItem();
          if (get() && (stated != null))
            Platform.runLater(() -> {
              Source item = stated.getItem();
              title.setText(item.getName());
              link.setText(item.getUrl());
            });
        }

      };

    return fetched;
  }

  private ReadOnlyBooleanWrapper deleting;

  private ReadOnlyBooleanWrapper deletingPropertyImpl() {
    if (deleting == null)
      deleting = new PseudoClassStateProperty(this, DELETING_PSEUDOCLASS);

    return deleting;
  }

  private static final PseudoClass FETCHING_PSEUDOCLASS = PseudoClass.getPseudoClass(StatedSource.STATE_FETCHING);
  private static final PseudoClass FETCHED_PSEUDOCLASS = PseudoClass.getPseudoClass(StatedSource.STATE_FETCHED);
  private static final PseudoClass DELETING_PSEUDOCLASS = PseudoClass.getPseudoClass(StatedSource.STATE_DELETING);

}

class PseudoClassStateProperty extends ReadOnlyBooleanWrapper {

  private final Node node;
  private final PseudoClass pseudoclass;

  public PseudoClassStateProperty(Node node, PseudoClass pseudoclass) {
    this.node = node;
    this.pseudoclass = pseudoclass;
  }

  @Override
  protected void invalidated() {
    node.pseudoClassStateChanged(pseudoclass, get());
  }

  @Override
  public String getName() {
    return pseudoclass.getPseudoClassName();
  }

}
