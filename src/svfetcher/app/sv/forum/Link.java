package svfetcher.app.sv.forum;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static svfetcher.app.sv.forum.SVParser.xPath;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Link {

  String base, name, href;

  public Link() {
  }

  public Link(String base, String name, String href) {
    this.base = base;
    this.name = name;
    this.href = href;
  }

  public String getBase() {
    return base;
  }

  public void setBase(String base) {
    this.base = base;
  }

  public String getHref() {
    return href;
  }

  public String getName() {
    return name;
  }

  private ObjectProperty<Boolean> fetchingProperty;

  public ObjectProperty<Boolean> fetchingProperty() {
    if (fetchingProperty == null)
      fetchingProperty = new SimpleObjectProperty<>(this, "fetching", false);
    return fetchingProperty;
  }

  public boolean isFetching() {
    return fetchingProperty().get();
  }

  public void setFetching(boolean fetching) {
    fetchingProperty().set(fetching);
  }

  private ObjectProperty<Boolean> fetchedProperty;

  public ObjectProperty<Boolean> fetchedProperty() {
    if (fetchedProperty == null)
      fetchedProperty = new SimpleObjectProperty<>(this, "fetched", false);
    return fetchedProperty;
  }

  public boolean isFetched() {
    return fetchedProperty().get();
  }

  public void setFetched(boolean fetching) {
    fetchedProperty().set(fetching);
  }

  @Override
  public String toString() {
    return name;
  }

}

