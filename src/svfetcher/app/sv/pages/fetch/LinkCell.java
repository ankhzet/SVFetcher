package svfetcher.app.sv.pages.fetch;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import svfetcher.app.sv.forum.Link;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class LinkCell extends ListCell<Link> {

  Label title = new Label();
  Label link = new Label();
  StackPane status;
  ProgressIndicator progress;
  
  Node graphic;

  public LinkCell() {
    setText(null);
    getStyleClass().add("cell");
    
    title.getStyleClass().add("title");
    link.getStyleClass().add("link");
    
    progress = new ProgressIndicator();
    progress.getStyleClass().add("progress");
    
    status = new StackPane(progress);
    status.getStyleClass().add("status");
    status.setAlignment(Pos.CENTER);
    
    graphic = new HBox(8, status, new VBox(title));
  }

  @Override
  protected void updateItem(Link item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null)
      setGraphic(null);
    else {
      title.setText(item.getName());
      link.setText(item.getHref());
      
      fetchingPropertyImpl().bind(item.fetchingProperty());
      fetchedPropertyImpl().bind(item.fetchedProperty());

      setGraphic(graphic);
    }
  }

  private ReadOnlyBooleanWrapper fetching;

  protected final void setFetching(boolean value) {
    fetchingPropertyImpl().set(value);
  }

  public final boolean isFetching() {
    return fetching == null ? false : fetching.get();
  }

  public final ReadOnlyBooleanProperty fetchingProperty() {
    return fetchingPropertyImpl().getReadOnlyProperty();
  }

  private ReadOnlyBooleanWrapper fetchingPropertyImpl() {
    if (fetching == null)
      fetching = new ReadOnlyBooleanWrapper() {

        @Override
        protected void invalidated() {
          pseudoClassStateChanged(FETCHING_PSEUDOCLASS_STATE, get());
        }

        @Override
        public Object getBean() {
          return LinkCell.this;
        }

        @Override
        public String getName() {
          return "fetching";
        }
      };
    return fetching;
  }

  private ReadOnlyBooleanWrapper fetched;

  protected final void setFetched(boolean value) {
    fetchedPropertyImpl().set(value);
  }

  public final boolean isFetched() {
    return fetched == null ? false : fetched.get();
  }

  public final ReadOnlyBooleanProperty fetchedProperty() {
    return fetchedPropertyImpl().getReadOnlyProperty();
  }

  private ReadOnlyBooleanWrapper fetchedPropertyImpl() {
    if (fetched == null)
      fetched = new ReadOnlyBooleanWrapper() {

        @Override
        protected void invalidated() {
          pseudoClassStateChanged(FETCHED_PSEUDOCLASS_STATE, get());
        }

        @Override
        public Object getBean() {
          return LinkCell.this;
        }

        @Override
        public String getName() {
          return "fetched";
        }
      };
    return fetched;
  }

  private static final PseudoClass FETCHING_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("fetching");

  private static final PseudoClass FETCHED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("fetched");

}
