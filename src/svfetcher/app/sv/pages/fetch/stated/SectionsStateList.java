package svfetcher.app.sv.pages.fetch.stated;

import ankh.ui.lists.stated.StatedItem;
import ankh.ui.lists.stated.StatedItemsList;
import javafx.collections.ObservableList;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Item>
 */
public class SectionsStateList<Item extends Source> extends StatedItemsList<Item, StatedSource<Item>> {

  public SectionsStateList() {
  }

  public SectionsStateList(ObservableList<? extends Item> c) {
    super(c);
  }

  public StatedSource<Item> stated(Source source) {
    for (StatedSource<Item> item : this)
      if (item.getItem().equals(source))
        return item;

    return null;
  }

  public Item firstUnfinished() {
    for (StatedItem<Item> source : this)
      if (!source.hasState(StatedSource.STATE_FETCHED))
        return source.getItem();

    return null;
  }

  public boolean hasUnfinished() {
    return firstUnfinished() != null;
  }

  @Override
  protected StatedSource<Item> newStated(Item item) {
    return new StatedSource<>(item);
  }

}
