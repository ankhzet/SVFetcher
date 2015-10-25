package svfetcher.app.sv.fb2;

import svfetcher.app.sv.fb2.nodes.Node;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Source>
 */
public interface InfoSupplier<Source> {

  public Node has(Source data, Node node, String path);
  
}
