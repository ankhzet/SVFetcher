package svfetcher.app.sv.forum;

import java.util.ArrayList;
import java.util.List;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Story extends svfetcher.app.story.Story<Post> {

  private boolean hasThreadmarks;

  public boolean hasThreadmarks() {
    return hasThreadmarks;
  }

  public void setHasThreadmarks(boolean hasThreadmarks) {
    this.hasThreadmarks = hasThreadmarks;
  }

  @Override
  public void addSection(Post section) {
    if (getAuthor() == null)
      setAuthor(section.getAuthor());

    super.addSection(section);
  }

  public List<Source> sections() {
    return new ArrayList<>(keySet());
  }

  public long contentsLength() {
    long charLength = 0;
    for (Post post : values())
      charLength += post.stringContents().length();
    return charLength;
  }

  @Override
  public String toString() {
    return String.format(
      "Author: %s\n"
      + "Title: %s\n"
      + "Chapters: %d\n"
      + "Size: %d",
      getAuthor(),
      getTitle(),
      size(),
      contentsLength()
    );
  }

}
