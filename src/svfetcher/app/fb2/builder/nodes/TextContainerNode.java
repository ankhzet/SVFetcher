package svfetcher.app.fb2.builder.nodes;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class TextContainerNode extends TextNode {

  public TextContainerNode() {
  }

  public TextContainerNode(String contents) {
    super(contents);
  }

  @Override
  public String tagName() {
    return null;
  }

}
