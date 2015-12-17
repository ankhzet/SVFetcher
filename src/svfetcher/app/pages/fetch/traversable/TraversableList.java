package svfetcher.app.pages.fetch.traversable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <E>
 */
public abstract class TraversableList<E> {

  private final ObservableList<E> allItems;
  private ObservableList<E> remaining;

  public TraversableList(Collection<E> allItems) {
    this.allItems = FXCollections.observableArrayList(allItems);
  }

  public ObservableList<E> getAllItems() {
    return allItems;
  }

  public abstract ObservableList<E> getBase();

  public ObservableList<E> getRemaining() {
    if (remaining == null) {
      remaining = FXCollections.observableArrayList(getAllItems());
      remaining.removeAll(getBase());
    }
    return remaining;
  }

  protected Collection<E> move(Collection<E> items, boolean fromBase) {
    ObservableList<E> from = getBase();
    ObservableList<E> to = getRemaining();
    if (!fromBase) {
      ObservableList<E> t = from;
      from = to;
      to = t;
    }

    return traverse(items, to, from);
  }

  protected Collection<E> traverse(Collection<E> items, ObservableList<E> already, ObservableList<E> rest) {
    Collection<E> untouched = new ArrayList<>(already);
    Collection<E> updated = traverse(already, items);

    Set<E> newTo = new LinkedHashSet<>();
    newTo.addAll(allItems);
    newTo.removeAll(already);

    rest.setAll(newTo);

    updated.removeAll(untouched);

    return updated;
  }

  protected Collection<E> traverse(ObservableList<E> already, Collection<E> toUpdate) {
    boolean all = toUpdate.isEmpty();

    Collection<E> update;
    if (all)
      update = new ArrayList<>(allItems);
    else {
      update = new LinkedHashSet<>(allItems.size());

      for (E item : allItems)
        if (already.contains(item) || toUpdate.contains(item))
          update.add(item);
    }

    already.setAll(update);

    return update;
  }

}
