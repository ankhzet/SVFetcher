package svfetcher.app.pages.pick;

import ankh.http.loading.AbstractURLFetchTask;
import ankh.http.query.ResourceQuery;
import org.w3c.dom.Document;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.Story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FetchStoryTask extends AbstractURLFetchTask<SV, Document, Story> {

  String url;
  boolean ignoreThreadmarks;

  public FetchStoryTask(SV source, String url, boolean ignoreThreadmarks) {
    super(source);
    this.url = url;
    this.ignoreThreadmarks = ignoreThreadmarks;
  }

  @Override
  protected ResourceQuery<Document, Story> query(SV sv) {
    return sv.story(url, !ignoreThreadmarks);
  }

  @Override
  protected String identifier(SV source) {
    return url;
  }

}
