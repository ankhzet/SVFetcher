package svfetcher.app.sv.html.fix;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class RootNode extends Node {

  public RootNode() {
    super(false, null, null, null);
  }

  @Override
  public String toString() {
    return contents();
  }

}
