package svfetcher.app.story.serialization;

import svfetcher.app.serializer.Writable;
import svfetcher.app.story.Author;
import svfetcher.app.story.Section;
import svfetcher.app.story.Story;
import svfetcher.app.sv.forum.User;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <T>
 */
public abstract class AbstractStorySerializer<T extends Section<?>> implements Writable {

  protected final Story<T> story;

  public AbstractStorySerializer(Story<T> story) {
    this.story = story;
  }

  @Override
  public String filename() {
    Author author = story.getAuthor();
    String authorName = (author instanceof User) ? ((User) author).getNickName() : author.getName();
    String title = story.getTitle();
    return transliterate(String.format("%s - %s", authorName, title));
  }

  static String transliterate(String str) {
    str = str.replace("_", " ").replace(":", " - ");
    str = str.replaceAll("[\\*\\?]", "");
    str = str.replaceAll(" {2,}", " ");

    return str;
  }

}
