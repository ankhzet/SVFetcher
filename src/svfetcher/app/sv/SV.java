package svfetcher.app.sv;

import ankh.http.Request;
import ankh.http.ServerRequest;
import ankh.http.cached.CacheableClient;
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

  @DependencyInjection()
  protected CacheableClient cache;

  public boolean uncachedIndex = true;

  public String isSVLink(String link) {
    if (link == null || (link = link.trim()).isEmpty())
      return null;

    if (link.matches("(?i)^https?://.*")) {
      Matcher m = Pattern.compile("forums\\..*/threads/([^/#\\?]+)", Pattern.CASE_INSENSITIVE).matcher(link);
      if (m.find())
        return m.group(1);

      return null;
    }

    return link;
  }

  public DocumentResourceQuery<Story> story(String threadLink, boolean fromThreadmarks) {
    String appendage = fromThreadmarks ? "threadmarks" : null;
    ServerRequest request = threadRequest(threadLink, appendage);

    if (uncachedIndex)
      cache.forget(request.getFullUrl());

    return query(request, document -> {
      Story story = null;

      if (document != null) {
        story = storyParser.fromPost(document.getDocumentElement());
        story.setSource(new Source(threadLink));
      } else if (fromThreadmarks) {
        DocumentResourceQuery<Story> indexQuery = story(threadLink, false);
        try {
          story = indexQuery.executeQuery();
          if (story == null)
            indexQuery.rethrow();
        } catch (Exception e) {
          request.setFailure(e);
          return null;
        }
      }

      if (story != null)
        story.setHasThreadmarks(document != null);

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

  private ServerRequest threadRequest(String threadLink, String appendage) {
    String thread = Strings.trim(threadLink, "/");

    String threadSlug = isSVLink(thread);
    boolean fullUrl = threadSlug != null && !threadSlug.equalsIgnoreCase(thread);
    if (fullUrl)
      api.setApiAddress(apiServer(thread));

    if (appendage == null)
      appendage = "";
    else
      appendage = "/" + appendage;

    return api.resolve("threads/" + Strings.trim(threadSlug, "/") + appendage);
  }

}
