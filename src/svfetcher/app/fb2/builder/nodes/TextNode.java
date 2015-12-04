package svfetcher.app.fb2.builder.nodes;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class TextNode extends AbstractNode {

  protected String contents;

  public TextNode() {
  }

  public TextNode(String contents) {
    this.contents = contents;
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  @Override
  public String getContents() {
    return contents;
  }

}
