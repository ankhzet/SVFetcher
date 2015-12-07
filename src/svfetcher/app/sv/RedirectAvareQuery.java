package svfetcher.app.sv;

import ankh.http.Request;
import ankh.http.Response;
import ankh.http.query.DocumentResourceQuery;
import java.io.IOException;
import java.net.URL;
import org.w3c.dom.Document;
import svfetcher.app.utils.Redirect;
import svfetcher.app.utils.Redirect.Redirects;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Resource>
 */
public class RedirectAvareQuery<Resource> extends DocumentResourceQuery<Resource> {

  public RedirectAvareQuery(Request request) {
    super(request);
  }

  public RedirectAvareQuery(Request request, ResourceSupplier<Document, Resource> resourceSuplier, SourceSupplier<Document> sourceSupplier) {
    super(request, resourceSuplier, sourceSupplier);
  }

  @Override
  protected Resource redirected(Response response) throws Exception {
    String source = getRequest().getUrl().toString();
    String location = response.getLocation();
    if (!source.equals(location))
      Redirects.save(new Redirect(source, location)); //      System.out.printf(" +  %s\n -> %s\n", source, location);

    return super.redirected(response);
  }

  @Override
  public Response execute() throws IOException {
    Request request = getRequest();

    String url = request.getUrl().toString();

    boolean redirected = false;
    for (;;) {
      Redirect r = Redirects.find(url);
      if (r == null)
        break;

      redirected = true;

//      System.out.printf("    %s\n -> %s\n", url, r.redirect);
      url = r.redirect;
    }

    if (redirected)
      request.setUrl(new URL(url));

    return super.execute();
  }

}
