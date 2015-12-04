package svfetcher.app.story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <SectionType>
 */
public class Story<SectionType extends Section<?>> extends Sections<SectionType> {

  private Author author;

  private String title;
  private String annotation;

  private Source source;

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAnnotation() {
    return annotation;
  }

  public void setAnnotation(String annotation) {
    this.annotation = annotation;
  }

  public void addSection(SectionType section) {
    put(section.getSource(), section);
  }

}
