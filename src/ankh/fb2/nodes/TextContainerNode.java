package ankh.fb2.nodes;

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
