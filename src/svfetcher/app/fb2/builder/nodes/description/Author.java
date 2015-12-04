package svfetcher.app.fb2.builder.nodes.description;

import svfetcher.app.fb2.builder.nodes.ContainerNode;

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