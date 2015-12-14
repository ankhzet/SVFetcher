package svfetcher.app.sv;

import ankh.http.Request;
import ankh.http.ServerRequest;
import ankh.http.loading.HTMLLoader;
import ankh.http.query.DocumentResourceQuery;
import ankh.ioc.annotations.DependencyInjection;
import ankh.utils.Strings;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import svfetcher.app.story.Source;
import svfetcher.app.sv.forum.Post;
import svfetcher.app.sv.forum.Story;
import svfetcher.app.sv.forum.parser.PostParser;
import svfetcher.app.sv.forum.parser.StoryParser;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SV extends HTMLLoader {

  @DependencyInjection()
  protected StoryParser storyParser;

  @DependencyInjection()
  protected PostParser postParser;

  @DependencyInjection()
  protected ServerRequest api;

  public String isSVLink(String link) {
    if (link == null || (link = link.trim()).isEmpty())
      return null;

    if (link.matches("(?i)^https?://.*")) {
      Matcher m = Pattern.compile("forums\\..*/threads/([^/#\\?]+)", Pattern.CASE_INSENSITIVE).matcher(link);
      if (m.find())
        return m.group(1);
    }

    return link;
  }

  public URL threadmarksLink(String from) {
    return threadLink(Strings.trim(from, "/") + "/threadmarks");
  }

  public URL threadLink(String from) {
    String thread = Strings.trim(from, "/");

    String threadSlug = isSVLink(thread);
    boolean fullUrl = threadSlug != null && !threadSlug.equalsIgnoreCase(thread);
    if (fullUrl)
      api.setApiAddress(apiServer(thread));

    ServerRequest request = api.resolve("threads/" + threadSlug);
    return request.getUrl();
  }

  public DocumentResourceQuery<Story> threadmarks(String threadLink) {
    String thread = Strings.trim(threadLink, "/");

    String threadSlug = isSVLink(thread);
    boolean fullUrl = threadSlug != null && !threadSlug.equalsIgnoreCase(thread);
    if (fullUrl)
      api.setApiAddress(apiServer(thread));

    ServerRequest request = api.resolve("threads/" + threadSlug + "/threadmarks");

    return query(request, document -> {
      Story story;
      if (document == null) {
        if (!fullUrl)
          return null;

        DocumentResourceQuery<Story> post = query(api.resolve("threads/" + threadSlug), doc2 -> {
          if (doc2 == null)
            return null;

          return storyParser.fromPost(doc2.getDocumentElement());
        });

        try {
          story = post.executeQuery();
          if (story == null) {
            post.rethrow();
            return null;
          }
        } catch (Exception ex) {
          request.setFailure(ex);
          return null;
        }
      } else
        story = storyParser.fromPage(document.getDocumentElement());

      story.setSource(new Source(threadLink));

      return story;
    });
  }

  public DocumentResourceQuery<Post> chapter(Source source) {
    String postUrl = source.getUrl();
    if (postUrl.toLowerCase().startsWith("http://"))
      postUrl = postUrl.replaceAll("^http://", "https://");

    String anchor = postByAnchor(postFragment(postUrl));

    ServerRequest request = api.resolve(postUrl);
    return query(request, document -> {
      if (document == null)
        return null;

      String url = request.getFullUrl().toString();
      source.setUrl(url);

      String postAnchor = anchor;
      if (url.contains("#")) {
        String page = postFragment(url);
        postAnchor = postByAnchor(page);
      }

      Post post = postParser.fromPage(
        document.getDocumentElement(),
        postAnchor,
        source
      );

      return post;
    });
  }

  @Override
  public <Resource> DocumentResourceQuery<Resource> query(Request request) {
    return new RedirectAvareQuery<>(request);
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

  URL apiServer(String urlString) {
    try {
      URL url = new URL(urlString);
      return new URL(url.getProtocol(), url.getHost(), "/");
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }

}
