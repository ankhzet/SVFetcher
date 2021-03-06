package svfetcher.app.pages.fetch;

import ankh.ui.PseudoClassStateProperty;
import java.lang.ref.WeakReference;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import svfetcher.app.pages.fetch.stated.StatedSource;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SourceCell extends ListCell<StatedSource<Source>> {

  public interface PickHandler {

    void accept(WeakReference<SourceCell> cell);

  }

  Label title = new Label();
  Label link = new Label();
  StackPane status;
  ProgressIndicator progress;

  Node graphic;
  TextField edit;
  HBox linkHolder;

  ObservableList<StatedSource<Source>> list;

  public SourceCell(ObservableList<StatedSource<Source>> list) {
    this.list = list;

    setEditable(true);
    setText(null);
    getStyleClass().add("cell");

    title.getStyleClass().add("title");
    link.getStyleClass().add("link");

    progress = new ProgressIndicator();
    progress.getStyleClass().add("progress");

    Button delete = new Button("X");
    delete.getStyleClass().add("delete");

    status = new StackPane(progress, delete);
    status.getStyleClass().add("status");
    status.setAlignment(Pos.CENTER);

    Button info = new Button("i");
    info.getStyleClass().add("info-button");

    linkHolder = new HBox(link);

    VBox i = new VBox(title, linkHolder);
    i.getStyleClass().add("info");
    HBox.setHgrow(i, Priority.ALWAYS);

    Button pick = new Button("Pick this as fetch source");
    VBox d = new VBox(pick);
    d.getStyleClass().add("details");
    d.setAlignment(Pos.CENTER_LEFT);
    HBox.setHgrow(d, Priority.ALWAYS);

    StackPane middle = new StackPane(i, d);
    HBox.setHgrow(middle, Priority.ALWAYS);

    HBox hbox = new HBox(8, status, middle, info);

    hbox.setAlignment(Pos.CENTER);

    graphic = hbox;

    status.setOnMouseClicked(this::toggleDeleting);
    setOnMouseClicked(this::toggleDeleting);
    delete.setOnAction(this::deleteItem);
    info.setOnAction(this::toggleDetails);
    pickHandlerProperty().addListener((l, o, handler) -> {
      if (handler == null)
        pick.setOnAction(null);
      else
        pick.setOnAction(e -> handler.accept(new WeakReference<>(this)));
    });
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

  @Override
  public void startEdit() {
    super.startEdit();

    TextField editNode = getEditNode();
    editNode.setText(link.getText());

    editNode.selectAll();
    linkHolder.getChildren().setAll(editNode);
    editNode.requestFocus();
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    linkHolder.getChildren().setAll(link);
  }

  private TextField getEditNode() {
    if (edit == null) {
      edit = new TextField();
      HBox.setHgrow(edit, Priority.ALWAYS);

      edit.focusedProperty().addListener((l, o, focused) -> {
        if (!focused) {
          StatedSource<Source> item = getItem();
          item.getItem().setUrl(edit.getText());
          commitEdit(item);
        }
      });

      edit.setOnKeyPressed((KeyEvent t) -> {
        if (t.getCode() == KeyCode.ENTER) {
          StatedSource<Source> item = getItem();
          item.getItem().setUrl(edit.getText());
          commitEdit(item);
        } else if (t.getCode() == KeyCode.ESCAPE)
          cancelEdit();
      });

      edit.getStyleClass().add("link");
    }
    return edit;
  }

  private void deleteItem(ActionEvent e) {
    StatedSource<Source> stated = getItem();
    if (stated != null)
      list.remove(stated);
  }

  private void toggleDetails(ActionEvent e) {
    boolean detailsShown = getPseudoClassStates().contains(DETAILS_PSEUDOCLASS);
    pseudoClassStateChanged(DETAILS_PSEUDOCLASS, !detailsShown);
  }

  private void toggleDeleting(MouseEvent e) {
    StatedSource<Source> stated = getItem();
    if (stated != null)
      stated.setDeleting(e.getSource() == status);

    e.consume();
  }

  private ObjectProperty<PickHandler> pickHandler;

  public final ObjectProperty<PickHandler> pickHandlerProperty() {
    if (pickHandler == null)
      pickHandler = new SimpleObjectProperty<>(this, "pickHandler", null);
    return pickHandler;
  }

  public void setPickHandler(PickHandler pickHandler) {
    pickHandlerProperty().set(pickHandler);
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

  private static final PseudoClass DETAILS_PSEUDOCLASS = PseudoClass.getPseudoClass("details");

}
