package svfetcher.app.sv.fb2.nodes;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface Node {

  String tagName();

  String getContents();

  String serialize();

}
