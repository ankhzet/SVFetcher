package svfetcher.app.sv.html.fix;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class HtmlParser {

  String html;

  public HtmlParser(String html) {
    this.html = html.trim();
  }

  public Node parse() {
    Node root = new RootNode();

    try (StringReader r = new StringReader(html)) {
      Node current = root;

      int contentStart = 0;

      int i = 0;
      for (;;) {
        int c = r.read();
        if (c < 0)
          break;

        switch (c) {
        case '<':
          int start = i;
          i++;
          if (start - contentStart > 0)
            current.addChild(new TextNode(html, contentStart, start - contentStart));

          int j = consume(r, "/");
          if (j < 0)
            continue;

          boolean close = j > 0;
          i += j;

          int tagEnd = expectTag(r);
          if (tagEnd <= 0)
            continue;

          String tag = html.substring(i, i += tagEnd);
          String att = html.substring(i, i += expectArguments(r));

          boolean slash = att.endsWith("/");
          if (slash)
            att = att.substring(0, att.length() - 1);

          boolean unary = slash || tag.startsWith("!") || isUnary(tag);

          j = consume(r, ">");
          if (j <= 0)
            break;
          i += j;

          contentStart = i;

          if (close) {
            Node picked = pickToClose(current, tag);
            if (picked != null) {
              current = picked;
              current.close();
              current.length = start - current.offset;
              current = current.parent;
            }
          } else {
            Node n = new Node(unary, html, tag, att);
            current.addChild(n);
            if (!unary) {
              current = n;
              current.offset = i;
            }
          }
          continue;

        default:
          i++;
          continue;
        }

        break;
      }

      if (i - contentStart > 0)
        current.addChild(new TextNode(html, contentStart, i - contentStart));

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    return root;
  }

  Node pickToClose(Node current, String tag) {
    if (tag.equalsIgnoreCase(current.tag))
      return current;

    if (current.unary)
      return null;

    LOG.fine(String.format("Closing <%s> when current tag is <%s>\n", tag, current.tag));

    ArrayList<Node> l = new ArrayList<>();
    for (Node child : current)
      l.add(0, child);

    Node picked = null;
    for (Node child : l)
      if (child.tag != null && child.tag.equalsIgnoreCase(tag) && !child.isClosed()) {
        picked = child;
        break;
      }

    if (picked != null) {
      LOG.fine(String.format(" ...closing prev unclosed sibling...\n"));
      return picked;
    }

    current.close();
    Node n = current;
    while ((n != null) && (!n.tag.equalsIgnoreCase(tag)))
      n = n.parent;

    if (n == null)
      throw new RuntimeException(String.format(
        "closing %s when %s is current tag, but no %s parent tag found\n",
        tag,
        current.tag,
        tag));
    else
      current = n;

    LOG.fine(String.format(" ...no unclosed siblings, closing matched parent...\n"));
    return current;
  }

  int expectTag(Reader r) throws IOException {
    int i = 0;
    for (;;) {
      r.mark(1);
      int c = r.read() & 0xffff;
      switch (c) {
      case -1:
      case '<':
      case '>':
      case '/':
      case ' ':
        r.reset();
        return i;
      }
      i++;
    }
  }

  int expectArguments(Reader r) throws IOException {
    int i = 0;
    for (;;) {
      r.mark(1);
      int c = r.read() & 0xffff;
      switch (c) {
      case -1:
      case '>':
        r.reset();
        return i;
      case '"':
        int j = seek(r, "\"");
        if (j <= 0)
//          r.reset();
          return i;
        i += j;
      }
      i++;
    }
  }

  int expect(Reader r, String match) throws IOException {
    int l = match.length();
    r.mark(l);
    try {
      return seek(r, match);
    } finally {
      r.reset();
    }
  }

  int seek(Reader r, String match) throws IOException {
    int i = 0;
    for (;;) {
      int j = consume(r, match);
      if (j != 0)
        return i + j;

      i++;
      r.skip(1);
    }
  }

  int consume(Reader r, String match) throws IOException {
    int l = match.length();
    r.mark(l);

    char[] buf = new char[l];

    if (r.read(buf) < l)
      return 0;

    if (match.equals(new String(buf)))
      return l;

    r.reset();
    return 0;
  }

  static final Set<String> unaries = new HashSet<String>() {
    {
      add("meta");
      add("link");
      add("base");
      add("br");
      add("img");
      add("hr");
    }
  };

  public static boolean isUnary(String tag) {
    return tag != null && unaries.contains(tag);
  }

  static final Logger LOG = Logger.getLogger(HtmlParser.class.getSimpleName());
  
}
