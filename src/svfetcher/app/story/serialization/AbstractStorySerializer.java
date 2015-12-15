package svfetcher.app.story.serialization;

import svfetcher.app.serializer.Serializable;
import svfetcher.app.serializer.Writable;
import svfetcher.app.story.Section;
import svfetcher.app.story.Story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <T>
 */
public abstract class AbstractStorySerializer<T extends Section<?>> implements Serializable, Writable {

  protected final Story<T> story;

  public AbstractStorySerializer(Story<T> story) {
    this.story = story;
  }

  @Override
  public String filename() {
    String author = story.getAuthor().getName();
    String title = story.getTitle();
    return transliterate(String.format("%s - %s", author, title));
  }

  static String transliterate(String str) {
    str = str.replace("_", " ").replace(":", " - ");
    str = str.replaceAll("[\\*\\?]", "");
    str = str.replaceAll(" {2,}", " ");

    return str;
  }

}
