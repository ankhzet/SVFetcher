package svfetcher.app.sv;

import ankh.annotations.DependenciesInjected;
import ankh.annotations.DependencyInjection;
import ankh.config.Config;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import svfetcher.http.Client;
import svfetcher.http.Request;
import svfetcher.http.Response;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class ServerRequest extends Request {

  @DependencyInjection()
  protected Config config;

  @DependencyInjection()
  protected Client httpClient;

  URL apiAddress;

  public ServerRequest() {
    super(Method.GET);
  }

  public ServerRequest(Method method) {
    super(method);
  }

  public ServerRequest(Method method, URL apiAddress) {
    super(method);
    this.apiAddress = apiAddress;
  }

  public ServerRequest(URL apiAddress) {
    super(Method.GET);
    this.apiAddress = apiAddress;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    httpClient.done(this);
  }

  protected URL apiURL(String append) throws MalformedURLException {
    return new URL(apiAddress, append);
  }

  @Override
  public Response execute() throws IOException {
    return httpClient.execute(this);
  }

  @Override
  public void cancel() {
    httpClient.cancel(this);
  }

  @DependenciesInjected()
  private void diInjected() throws MalformedURLException {
    if (apiAddress == null) {
      String apiUrl = config.get("api.server.url", "");
      if (!apiUrl.isEmpty())
        apiAddress = new URL(apiUrl);
    }
  }

}
