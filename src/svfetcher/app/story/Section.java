package svfetcher.app.story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Content>
 */
public class Section<Content> {

  private Source source;

  private Author author;
  private String title;

  private Content contents;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
    if (source != null)
      setTitle(source.getName());
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public Content getContents() {
    return contents;
  }

  public void setContents(Content contents) {
    this.contents = contents;
  }

  public String stringContents() {
    return contents != null ? contents.toString() : null;
  }

}
