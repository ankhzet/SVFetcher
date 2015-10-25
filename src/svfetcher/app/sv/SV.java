package svfetcher.app.sv;

import svfetcher.app.sv.forum.Link;
import ankh.annotations.DependencyInjection;
import ankh.utils.Strings;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import svfetcher.app.sv.forum.Post;
import svfetcher.app.sv.forum.Story;
import svfetcher.app.sv.html.Cleaner;
import svfetcher.http.Client;
import svfetcher.http.RequestQuery;
import svfetcher.http.Response;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SV {

  public interface RequestConsumer<Type> {

    void accept(SVRequest request, Type doc);

  }

  @DependencyInjection()
  protected DocumentBuilder builder;

  @DependencyInjection()
  protected Client httpClient;

  SVRequest api = new SVRequest();

  public boolean isSVLink(String link) {
    if (link.matches("(?i)^https?://.*"))
      return link.contains(api.apiAddress.toString());

    return true;
  }

  public Document fetch(SVRequest request, Response response) throws IOException {
    String content = fetchContents(response.decodedStream());
    String cleanedHtml = Cleaner.cleanup(content);

    try {
      return builder.parse(new ByteArrayInputStream(cleanedHtml.getBytes()));
    } catch (SAXParseException ex) {
      Strings lines = Strings.explode(cleanedHtml, "\n");
      System.err.printf("ERR: {{%s}}\n", lines.get(ex.getLineNumber() - 1));
//      System.err.printf("ERR:\n%s\n\n", cleanedHtml);
      
      int i = 0;
      for (String line : Strings.explode(cleanedHtml, "\n"))
        System.err.printf("[%3d] %s\n", ++i, line);
      
      request.setFailure(ex);
    } catch (SAXException ex) {
      request.setFailure(ex);
    }
    return null;
  }

  public RequestQuery threadmarks(String threadmarksLink, RequestConsumer<Story> consumer) throws IOException {
    SVRequest request = api.threadmarks();
    return request.query(threadmarksLink, readDocument(request, (q, document) -> {
      if (document == null) {
        consumer.accept(q, null);
        return;
      }

      Story story = new Story().parser().fromPage(document.getDocumentElement());
      story.setBase(threadmarksLink);
      consumer.accept(q, story);
    }));
  }

  public RequestQuery chapter(Link link, RequestConsumer<Post> consumer) throws IOException {
    String page = postFragment(link.getHref());

    SVRequest request = api.page(page);
    return request.query(link.getBase(), readDocument(request, (q, document) -> {
      if (document == null) {
        consumer.accept(q, null);
        return;
      }

      Post post = new Post().parser().fromPage(
        document.getDocumentElement(),
        postByAnchor(page)
      );

      consumer.accept(q, post);
    }));
  }

  Consumer<Response> readDocument(SVRequest request, RequestConsumer<Document> consumer) {
    return (response) -> {
      if (response == null) {
        consumer.accept(request, null);
        return;
      }

      Document doc = null;
      try {
        doc = fetch(request, response);
      } catch (IOException ex) {
        request.setFailure(new RuntimeException(ex));
      }

      consumer.accept(request, doc);
    };
  }

  static String fetchContents(InputStream stream) throws IOException {
    try (Reader r = new InputStreamReader(stream)) {
      StringBuilder sb = new StringBuilder();
      CharBuffer buffer = CharBuffer.allocate(1024 * 32);
      int n;
      while ((n = r.read(buffer)) >= 0) {
        buffer.flip();
        sb.append(buffer);
      }

      return sb.toString();
    }
  }

  String postFragment(String pageUrl) {
    Pattern p = Pattern.compile("/([^/]+)$");
    Matcher m = p.matcher(pageUrl);
    return m.find() ? m.group(1) : "";
  }

  String postByAnchor(String pageUrl) {
    Pattern p1 = Pattern.compile("#(.*)$");
    Matcher m1 = p1.matcher(pageUrl);
    return m1.find() ? m1.group(1) : pageUrl;
  }

}
