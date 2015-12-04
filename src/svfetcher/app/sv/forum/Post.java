package svfetcher.app.sv.forum;

import ankh.xml.dom.serializer.NodeSerializer;
import org.w3c.dom.Node;
import svfetcher.app.fb2.FB2Cleaner;
import svfetcher.app.story.Section;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Post extends Section<Node> {

  private String cachedContents;

  public String stringContents() {
    if (cachedContents == null) {
      NodeSerializer serializer = new NodeSerializer();
      Node contents = getContents();
      assert contents != null;
      if (contents == null)
        return "";
      cachedContents = serializer.serialize(contents);
      cachedContents = FB2Cleaner.cleanup(cachedContents);
    }
    return cachedContents;
  }

}
