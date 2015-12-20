package svfetcher.app.pages.fetch;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.function.Consumer;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import ankh.ui.lists.stated.StatedItem;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import svfetcher.app.story.Source;
import svfetcher.app.pages.fetch.stated.StatedSource;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SourceList extends ListView<StatedSource<Source>> {

  Consumer<SourceCell> onSelect;

  public SourceList(ObservableList<StatedSource<Source>> items) {
    super(items);
    getStyleClass().add("link-list");

    double clientWidth = clientWidth();
    setCellFactory(listView -> {
      SourceCell cell = new SourceCell(items);
      setCellPrefWidth(cell, clientWidth);
      cell.pickHandlerProperty().bind(pickHandlerProperty());
      return cell;
    });
    setEditable(true);

    getSelectionModel()
      .setSelectionMode(SelectionMode.MULTIPLE);

    getSelectionModel()
      .getSelectedItems()
      .addListener((ListChangeListener.Change<? extends StatedItem<Source>> c) -> {
        while (c.next())
          if (c.wasPermutated()) {
//                  for (int i = c.getFrom(); i < c.getTo(); ++i) {
//                    //permutate
//                  }
          } else if (c.wasUpdated()) {
            //update item
          } else if (onSelect != null) {
            ObservableList<Node> ch = getChildrenUnmodifiable();
            for (Node node : ch)
              if (node instanceof VirtualFlow) {
                VirtualFlow flow = (VirtualFlow) node;

                ObservableList<StatedSource<Source>> it = getItems();

                for (StatedItem<Source> link : c.getAddedSubList()) {
                  Node n = flow.getCell(it.indexOf(link));
                  if (n != null && (n instanceof SourceCell))
                    onSelect.accept((SourceCell) n);
                }

                break;
              }
          }
      });

    widthProperty().addListener(this::widthChanged);
    ScrollBar scroll = getViewVerticalScrollBar();
    if (scroll != null)
      scroll.visibleProperty().addListener((l, o, visible) -> {
        widthChanged(null, null, getWidth());
      });
  }

  public void setOnSelect(Consumer<SourceCell> callback) {
    onSelect = callback;
  }

  @Override
  public String getUserAgentStylesheet() {
    return getClass().getResource("link-list.css").toString();
  }

  private void widthChanged(ObservableValue<? extends Number> observable, Number oldWidth, Number width) {
    double clientWidth = clientWidth();

    for (Node children : getChildren())
      if (children instanceof ListCell)
        setCellPrefWidth((ListCell) children, clientWidth);
  }

  private void setCellPrefWidth(ListCell cell) {
    setCellPrefWidth(cell, clientWidth());
  }

  private void setCellPrefWidth(ListCell cell, double width) {
    cell.setPrefWidth(width);
    cell.setMaxWidth(width);
  }

  private double clientWidth() {
    double insets = insetsSize(getInsets());
    double scrollBarWidth = scrollBarWidth();

    return getWidth() - insets - scrollBarWidth;
  }

  private double insetsSize(Insets insets) {
    return insets.getLeft() + insets.getRight();
  }

  private ScrollBar getViewVerticalScrollBar() {
    Set<Node> nodes = lookupAll(".scroll-bar");
    for (final Node node : nodes)
      if (node instanceof ScrollBar) {
        ScrollBar scrollBar = (ScrollBar) node;
        if (scrollBar.getOrientation() == Orientation.VERTICAL)
          return scrollBar;
      }

    return null;
  }

  private double scrollBarWidth() {
    ScrollBar scrollBar = getViewVerticalScrollBar();

    if (scrollBar != null)
      if (scrollBar.isVisible())
        return scrollBar.getWidth();

    return 0.;
  }

  private ObjectProperty<SourceCell.PickHandler> pickHandler;

  private ObjectProperty<SourceCell.PickHandler> pickHandlerProperty() {
    if (pickHandler == null)
      pickHandler = new SimpleObjectProperty<>(this, "pickHandler", null);
    return pickHandler;
  }

  public void setPickHandler(SourceCell.PickHandler pickHandler) {
    pickHandlerProperty().set(pickHandler);
  }

}
