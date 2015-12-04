package svfetcher.app.sv.forum;

import java.util.ArrayList;
import java.util.List;
import svfetcher.app.story.Source;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Story extends svfetcher.app.story.Story<Post> {

  @Override
  public void addSection(Post section) {
    if (getAuthor() == null)
      setAuthor(section.getAuthor());

    super.addSection(section);
  }

  public List<Source> sections() {
    return new ArrayList<>(keySet());
  }

  @Override
  public void setSource(Source source) {
    super.setSource(source);

//    for (Source s : this.keySet())
//      s.setUrl(source.getUrl());
  }

  @Override
  public String toString() {
    long charLength = 0;
    for (Post post : values())
      charLength += post.stringContents().length();

    return String.format(
      "Author: %s\n"
      + "Title: %s\n"
      + "Chapters: %d\n"
      + "Size: %d",
      getAuthor(),
      getTitle(),
      size(),
      charLength
    );
  }

}
