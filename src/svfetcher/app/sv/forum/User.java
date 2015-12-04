package svfetcher.app.sv.forum;

import svfetcher.app.story.Author;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class User extends Author {

  private String title;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getName() {
    String name = super.getName();
    if (title != null)
      name += " (" + title + ")";
    return name;
  }

  public String getNickName() {
    return super.getName();
  }

}
