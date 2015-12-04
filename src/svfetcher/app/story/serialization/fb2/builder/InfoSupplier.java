package svfetcher.app.story.serialization.fb2.builder;

import svfetcher.app.story.serialization.fb2.builder.nodes.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Source>
 */
public interface InfoSupplier<Source> {

  public Node has(Source data, Node node, String path);
  
}
