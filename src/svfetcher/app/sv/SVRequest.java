package svfetcher.app.sv;

import ankh.IoC;
import ankh.exceptions.FactoryException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;
import svfetcher.http.RequestQuery;
import svfetcher.http.Response;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SVRequest extends ServerRequest {

  private static final String P = "{{thread}}";

  public SVRequest() {
    super(Method.GET);
  }

  public SVRequest(URL apiAddress) {
    super(Method.GET);
    this.apiAddress = apiAddress;
  }

  public SVRequest(Method method, URL apiAddress) {
    super(method, apiAddress);
  }

  protected SVRequest instantiate(Method method) {
    try {
      return IoC.resolve(getClass(), Method.GET, apiAddress);
    } catch (FactoryException ex) {
      throw new RuntimeException(ex);
    }
  }

  public SVRequest threadmarks() throws MalformedURLException {
    SVRequest request = instantiate(Method.GET);
    request.url(request.apiURL(P + "/threadmarks"));
    return request;
  }

  public SVRequest page(String link) throws MalformedURLException {
    SVRequest request = instantiate(Method.GET);
    request.url(request.apiURL(P + "/" + link));
    return request;
  }

  public RequestQuery query(String link, Consumer<Response> consumer) throws MalformedURLException {
    url = new URL(url.toString().replace(P, link));
    RequestQuery query = new RequestQuery(this);
    return query.asynk(consumer);
  }

}
