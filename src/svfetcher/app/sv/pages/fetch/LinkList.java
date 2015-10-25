package svfetcher.app.sv.pages.fetch;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.function.Consumer;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import svfetcher.app.sv.forum.Link;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class LinkList extends ListView<Link> {

  Consumer<LinkCell> onSelect;

  public LinkList(ObservableList<Link> items) {
    super(items);
    getStyleClass().add("link-list");

    setCellFactory((ListView<Link> param) -> new LinkCell());

    getSelectionModel()
            .getSelectedItems()
            .addListener((ListChangeListener.Change<? extends Link> c) -> {
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
                      
                      ObservableList<Link> it = getItems();

                      for (Link link : c.getAddedSubList()) {
                        Node n = flow.getCell(it.indexOf(link));
                        if (n != null && (n instanceof LinkCell))
                          onSelect.accept((LinkCell) n);
                      }

                      break;
                    }
                }
            });

  }

  public void setOnSelect(Consumer<LinkCell> callback) {
    onSelect = callback;
  }

  @Override
  public String getUserAgentStylesheet() {
    return getClass().getResource("link-list.css").toString();
  }

}
