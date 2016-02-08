package svfetcher.app.story.serialization.fb2;

import svfetcher.app.story.Section;
import svfetcher.app.story.serialization.fb2.builder.SVStoryFB2Builder;
import svfetcher.app.story.Story;
import svfetcher.app.story.serialization.AbstractStorySerializer;
import ankh.fb2.nodes.Node;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <T>
 */
public class FB2StorySerializer<T extends Section<?>> extends AbstractStorySerializer<T> {

  public FB2StorySerializer(Story<T> story) {
    super(story);
  }

  @Override
  public String filename() {
    return super.filename() + ".fb2";
  }

  @Override
  public InputStream serialize() {
    SVStoryFB2Builder<T> builder = new SVStoryFB2Builder<>();

    Node node = builder.build(story);
    String contents = node.serialize();

    return new ByteArrayInputStream(contents.getBytes());
  }

}
