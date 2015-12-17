package svfetcher.app.pages.fetch.traversable;

import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import svfetcher.app.pages.fetch.stated.SectionsStateList;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SourcesListHelper extends TraversableList<Source> {

  private SectionsStateList<Source> stated;

  public SourcesListHelper(Collection<Source> allItems) {
    super(allItems);
  }

  public SectionsStateList<Source> statedList() {
    if (stated == null)
      stated = new SectionsStateList<>(getBase());
    return stated;
  }

  public void showAll(Source... sources) {
    showAll(new HashSet<>(Arrays.asList(sources)));
  }

  public void hideAll(Source... sources) {
    hideAll(new HashSet<>(Arrays.asList(sources)));
  }

  public void showAll(Collection<Source> sources) {
    move(sources, false);
  }

  public void hideAll(Collection<Source> sources) {
    move(sources, true);
  }

  private ObservableList<Source> hiddenItemsList;

  public ObservableList<Source> getBase() {
    if (hiddenItemsList == null)
      hiddenItemsList = FXCollections.observableArrayList(getShownItems());
    return hiddenItemsList;
  }

  public Collection<Source> getShownItems() {
    return getAllItems();
  }

  public final ObservableList<Source> getHiddenItems() {
    return getRemaining();
  }

}
