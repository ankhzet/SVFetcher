package svfetcher.app.story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Source {

  private String url;
  private String name;

  public Source(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    String aName = getName();
    String anUrl = getUrl();

    return (aName != null) ? String.format("%s [%s]", aName, anUrl) : anUrl;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Source))
      return false;

    return url.equals(((Source) obj).url);
  }

  @Override
  public int hashCode() {
    return url.hashCode();
  }

}
