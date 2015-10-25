package svfetcher.app.sv.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static svfetcher.app.sv.forum.SVParser.xPath;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Story extends LinkedHashMap<Link, Post> implements Parseable<Story> {

  String title;
  String base;
  User creator;

  List<Link> threadmarks = new ArrayList<>();

  public Story() {
  }

  public Story(String title, List<Link> threadmarks) {
    this.title = title;
    this.threadmarks = threadmarks;
  }

  public void setPost(Link link, Post post) {
    if (creator == null)
      creator = post.creator;

    HashMap<Link, Post> copy = (HashMap<Link, Post>) clone();
    copy.put(link, post);
    
    clear();
    for (Link threadmark : threadmarks) {
      Post p = copy.get(threadmark);
      if (p != null)
        put(threadmark, p);
    }
    
    post.link = link;
    link.setFetched(true);
  }

  public List<Link> threadmarks() {
    return threadmarks;
  }

  public User creator() {
    return creator;
  }

  public String getTitle() {
    return title;
  }

  public String getBase() {
    return base;
  }

  public void setBase(String base) {
    this.base = base;
    for (Link threadmark : threadmarks)
      threadmark.setBase(base);
  }

  public Post firstPost() {
    if (threadmarks.size() <= 0)
      return null;

    Link link = threadmarks.get(0);
    return get(link);
  }

  public Post lastPost() {
    if (threadmarks.size() <= 0)
      return null;

    Link link = threadmarks.get(threadmarks.size() - 1);
    return get(link);
  }

  @Override
  public String toString() {
    long size = 0;
    for (Post post : values())
      size += post.getContents().length();

    return String.format(
            "Author: %s (members/%s/)\n"
            + "Title: %s\n"
            + "Chapters: %d\n"
            + "Size: %d",
            creator.getNickname(),
            creator.getProfile(),
            title,
            threadmarks.size(),
            size
    );
  }

  @Override
  public SVParser<Story> parser() {
    return new StoryParser();
  }

}

class StoryParser extends SVParser<Story> {

  static final String threadmarkXPath = "//li[contains(@class, 'threadmarkItem')]/a";
  static final String titleXPath = "//fieldset[contains(@class, 'breadcrumb')]/span/span/a/span";

  @Override
  public Story fromPage(Node node, Object... args) {
    NodeList nodes = xPath(node, threadmarkXPath);

    int total = nodes.getLength();

    ArrayList<Link> list = new ArrayList<>(total);
    for (int i = 0; i < total; i++) {
      Node n = nodes.item(i);
      Link link = new Link(
              null,
              n.getTextContent(),
              attr(n, "href")
      );
      list.add(link);
    }

    Node title = last(xPath(node, titleXPath));

    return new Story(title.getTextContent(), list);
  }

}
