package svfetcher.app.sv.fb2.nodes.description;

import svfetcher.app.sv.fb2.nodes.ContainerNode;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Author extends ContainerNode {

  public Author() {
  }

  public Author(String firstName) {
    add(new FirstName(firstName));
  }

}
