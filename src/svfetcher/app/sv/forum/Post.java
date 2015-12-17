package svfetcher.app.sv.forum;

import ankh.fb2.fix.FB2Cleaner;
import ankh.xml.dom.serializer.NodeSerializer;
import org.w3c.dom.Node;
import svfetcher.app.story.Section;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Post extends Section<String> {

  public boolean isEmpty() {
    String contents = getContents();
    return contents == null || contents.isEmpty();
  }

  public boolean setContentsFromXML(Node node) {
    NodeSerializer serializer = new NodeSerializer();
    String serialized = serializer.serialize(node);
    serialized = FB2Cleaner.cleanup(serialized);

    setContents(serialized);

    return !serialized.isEmpty();
  }

}
