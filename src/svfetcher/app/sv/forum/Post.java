package svfetcher.app.sv.forum;

import ankh.xml.dom.serializer.NodeSerializer;
import org.w3c.dom.Node;
import ankh.fb2.fix.FB2Cleaner;
import svfetcher.app.story.Section;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Post extends Section<Node> {

  private String cachedContents;

  @Override
  public String stringContents() {
    if (cachedContents == null) {
      NodeSerializer serializer = new NodeSerializer();
      Node contents = getContents();

      if (contents == null)
        return "";

      cachedContents = serializer.serialize(contents);
      cachedContents = FB2Cleaner.cleanup(cachedContents);
    }
    return cachedContents;
  }

}
