package svfetcher.app.sv.pages.fetch.stated;

import ankh.ui.lists.stated.StatedItem;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <S>
 */
public class StatedSource<S extends Source> extends StatedItem<S> {

  public static final String STATE_FETCHED = "fetched";
  public static final String STATE_FETCHING = "fetching";
  public static final String STATE_DELETING = "deleting";

  public StatedSource() {
  }

  public StatedSource(S item) {
    super(item);
  }

  public void setFetching(boolean set) {
    setState(STATE_FETCHING, set);
  }

  public void setFetched(boolean set) {
    setState(STATE_FETCHED, set);
  }

  public void setDeleting(boolean set) {
    setState(STATE_DELETING, set);
  }

  public boolean isDeleting() {
    return hasState(STATE_DELETING);
  }

}
