package svfetcher.app.fb2.builder.nodes;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface Node {

  String tagName();

  String getContents();

  String serialize();

}
