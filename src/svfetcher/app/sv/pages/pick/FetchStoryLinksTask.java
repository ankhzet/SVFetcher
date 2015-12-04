package svfetcher.app.sv.pages.pick;

import ankh.http.Response;
import ankh.http.loading.AbstractURLFetchTask;
import ankh.http.query.ResourceQuery;
import org.w3c.dom.Document;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.Story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FetchStoryLinksTask extends AbstractURLFetchTask<SV, Document, Story> {

  String url;
  ResourceQuery<Document, Story> query;

  public FetchStoryLinksTask(SV source, String url) {
    super(source);
    this.url = url;
  }

  @Override
  protected ResourceQuery<Document, Story> query(SV sv) {
    return query = sv.threadmarks(url);
  }

  @Override
  protected String identifier(SV source) {
    return url;
  }

  @Override
  public Story call() throws Exception {
    try {
      return super.call();
    } catch (Exception e) {
      Response response = (query != null) ? query.getResponse() : null;
      if (response != null && response.failed())
        throw new Exception("Provided link, probably, doesn't have threadmarks", e);

      throw e;
    }
  }

}
