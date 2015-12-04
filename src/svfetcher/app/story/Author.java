package svfetcher.app.story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Author {

  private Source source;
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  @Override
  public String toString() {
    return String.format("%s [%s]", getName(), getSource());
  }

}
