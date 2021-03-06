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
      Matcher m = Pattern.compile("forums\\..*/(threads|posts)/([^/#\\?]+)", Pattern.CASE_INSENSITIVE).matcher(link);
      if (m.find())
        return m.group(2);

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
        String post = postByAnchor(threadLink);
        if (post.equals(threadLink))
          post = null;
        
        story = storyParser.fromPost(document.getDocumentElement(), post);
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
    String postUrl = source.getUrl().replaceAll("(?i)^http://", "https://");
    source.setUrl(postUrl);

    String anchor = postByAnchor(postFragment(postUrl));

    ServerRequest request = api.resolve(postUrl);
    return query(request, document -> {
      if (document == null)
        return null;

      String url = request.getFullUrl().toString();

      String postAnchor = anchor;
      if (url.contains("#")) {
        String page = postFragment(url);
        postAnchor = postByAnchor(page);
      } else if (anchor != null && !anchor.isEmpty()) {
          url = Strings.trimr(url, "/") + "#" + anchor;
      }

      source.setUrl(url);
      
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

  Pattern FRAGMENT = Pattern.compile("/([^/]+)$");
  Pattern ANCHOR = Pattern.compile("#(.*)$");
  Pattern PAGE = Pattern.compile("/(page-\\d+)/?[^/]*$");

  String postFragment(String pageUrl) {
    Matcher m = FRAGMENT.matcher(pageUrl);
    return m.find() ? m.group(1) : "";
  }

  String postByAnchor(String pageUrl) {
    Matcher m = ANCHOR.matcher(pageUrl);
    return m.find() ? m.group(1) : pageUrl;
  }
  
  String postPage(String pageUrl) {
    Matcher m = PAGE.matcher(pageUrl);
    return m.find() ? "/" + m.group(1) : "";
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
      appendage = postPage(thread);
    else
      appendage = "/" + appendage;

    return api.resolve("threads/" + Strings.trim(threadSlug, "/") + appendage);
  }

}
