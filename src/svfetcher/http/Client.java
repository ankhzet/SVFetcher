package svfetcher.http;

import ankh.annotations.DependenciesInjected;
import ankh.annotations.DependencyInjection;
import ankh.config.Config;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import svfetcher.http.cookies.HttpCookies;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Client extends HttpCookies {

  @DependencyInjection()
  protected Config config;

  @DependencyInjection()
  protected Proxy proxy;

  public String charset = "UTF-8";
  public String encoding = "gzip";
  protected String userAgent = "Ankh client";

  HashMap<Request, HttpURLConnection> requestConnection = new HashMap<>();

  public Response execute(Request request) throws IOException {
    HttpURLConnection connection = prepare(request);

    synchronized (this) {
      requestConnection.put(request, connection);
    }

    switch (request.method) {
    case POST:
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
      connection.setDoOutput(true);
      try (OutputStream output = connection.getOutputStream()) {
        output.write(request.fullUrl().getQuery().getBytes(charset));
      }
      break;
    }

    LOG.log(Level.FINE, String.format("Connection  : %s", request.fullUrl()));
    LOG.log(Level.FINE, String.format("  usingProxy: %b", connection.usingProxy()));
    LOG.log(Level.FINE, String.format("  status    : %d", connection.getResponseCode()));

    grabCookies(connection);

    return new Response(connection);
  }

  HttpURLConnection prepare(Request request) throws IOException {
    URL url = request.fullUrl();
    switch (request.method) {
    case POST:
      url = new URL(url, "?");
      break;
    default:
    }

    HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
    connection.setUseCaches(false);
    connection.setRequestMethod(request.method.toString());

    putCookies(connection);

//    connection.setRequestProperty("Connection", "Close");
    connection.setRequestProperty("Accept-Charset", charset);
    if (encoding != null)
      connection.setRequestProperty("Accept-Encoding", encoding);

    connection.setRequestProperty("User-Agent", userAgent);
    connection.setConnectTimeout(1000 * 30);
    connection.setReadTimeout(1000 * 60 * 5);

    connection.setInstanceFollowRedirects(false);
    connection.setDoInput(true);

    return connection;
  }

  synchronized public void cancel(Request request) {
    HttpURLConnection connection = requestConnection.get(request);
    if (connection != null) {
      connection.disconnect();
      requestConnection.remove(request);
    }
  }

  synchronized public void done(Request request) {
    requestConnection.remove(request);
  }

  @DependenciesInjected()
  private void diInjected() {
    charset = config.get("api.server.charset", charset);
    encoding = config.get("api.server.encoding", encoding);
    userAgent = config.get("api.user-agent", userAgent);
  }

  private static final Logger LOG = Logger.getLogger(Client.class.getName());

}
