package svfetcher.app.sv.pages.fetch;

import ankh.http.query.ResourceQuery;
import ankh.zet.http.http.loading.AbstractURLDocumentFetchTask;
import svfetcher.app.sv.pages.fetch.stated.StatedSource;
import svfetcher.app.sv.pages.fetch.stated.SectionsStateList;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.w3c.dom.Document;
import svfetcher.app.story.Source;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.Post;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FetchTask extends AbstractURLDocumentFetchTask<SV, Post> {

  SectionsStateList sections;

  public FetchTask(SV sv, SectionsStateList sections) {
    super(sv);
    this.sections = sections;
  }

  @Override
  protected Post call() throws Exception {
    Source link = getLink();
    if (link == null)
      return null;

    Post post = super.call();

    updateMessage(String.format("Fetched: %s", link.getName()));

    StatedSource<Source> linkState = sections.stated(link);
    linkState.setFetched(true);

    return post;
  }

  @Override
  protected ResourceQuery<Document, Post> query(SV sv) {
    return sv.chapter(getLink());
  }

  @Override
  protected String identifier(SV sv) {
    Source link = getLink();
    return link == null ? null : link.getName();
  }

  @Override
  protected void running() {
    Optional.ofNullable(getLink())
      .ifPresent(link -> {
        updateMessage(String.format("Fetch: %s", link));

        StatedSource<Source> linkState = sections.stated(link);
        linkState.setFetching(true);
      });
  }

  @Override
  protected void done() {
    Optional.ofNullable(getLink())
      .ifPresent(link -> {
        StatedSource<Source> linkState = sections.stated(link);
        linkState.setFetching(false);
      });
  }

  private SimpleObjectProperty<Source> linkProperty;

  public Source getLink() {
    return linkProperty().get();
  }

  public ObjectProperty<Source> linkProperty() {
    if (linkProperty == null)
      linkProperty = new SimpleObjectProperty<>(this, "link", sections.firstUnfinished());

    return linkProperty;
  }

}
