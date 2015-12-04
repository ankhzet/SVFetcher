package svfetcher.app.pages.fetch;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.function.Consumer;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import ankh.ui.lists.stated.StatedItem;
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

    setCellFactory(listView -> new SourceCell(items));

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

  }

  public void setOnSelect(Consumer<SourceCell> callback) {
    onSelect = callback;
  }

  @Override
  public String getUserAgentStylesheet() {
    return getClass().getResource("link-list.css").toString();
  }

}
