package svfetcher.app.sv.pages.fetch;

import ankh.annotations.DependencyInjection;
import ankh.tasks.CustomTask;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import svfetcher.app.sv.forum.Link;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.Story;
import svfetcher.http.RequestQuery;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FetchTask extends CustomTask<Link> {

  @DependencyInjection()
  protected SV sv;

  Story story;

  public FetchTask(Story story) {
    this.story = story;
  }

  public Link firstUnfinished() {
    for (Link link : story.threadmarks())
      if (!link.isFetched())
        return link;

    return null;
  }

  public boolean hasUnfinished() {
    return firstUnfinished() != null;
  }

  @Override
  protected Link call() throws Exception {
    Link link = firstUnfinished();
    if (link != null)
      fetch(link);

    updateProgress(0, 0);
    done();

    return link;
  }

  void fetch(Link link) throws Exception {
    if (isCancelled())
      return;

    setFetchingLink(link);

    try {
      link.setFetching(true);

      updateMessage(String.format("Fetch: %s", link));

      updateProgress(0, 1);

      Semaphore s = new Semaphore(0);

      Thread.sleep(10);
      RequestQuery q = sv.chapter(link, (request, post) -> {
        try {
          updateProgress(1, 1);

          if (post != null)
            story.setPost(link, post);
          else
            throw new RuntimeException("Failed to fetch post " + link, request.getFailure());

          updateMessage(String.format("Fetched: %s", link));
        } finally {
          s.release();
        }
      });
      q.responseProperty().addListener((v, o, n) -> {
        if (n != null)
          n.setListener((d, t) -> {
            if (t < 0)
              t = (long) (d * 1.8);

            updateProgress(d, t);
          });
      });

      s.acquire();
      
      Exception thrown = q.getRequest().getFailure();
      if (thrown != null)
        throw thrown;
      
    } finally {
      link.setFetching(false);
    }
  }

  public void setFetchingLink(Link fetchingLink) {
    fetchingLinkProperty().set(fetchingLink);
  }

  public Link getFetchingLink() {
    return fetchingLinkProperty().get();
  }

  private ObjectProperty<Link> fetchingLinkProperty;

  ObjectProperty<Link> fetchingLinkProperty() {
    if (fetchingLinkProperty == null)
      fetchingLinkProperty = new SimpleObjectProperty<>(this, "fetchingLink", null);
    return fetchingLinkProperty;
  }

}
