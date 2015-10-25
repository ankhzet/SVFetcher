package svfetcher.http;

import ankh.utils.Strings;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public abstract class Request {

  public enum Method {

    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS,

  }

  protected URL url;
  protected Method method = Method.GET;
  protected RequestParameters parameters;

  protected Exception failure;

  public Request(Method method) {
    this.method = method;
    this.parameters = new RequestParameters();
  }

  public Request(Method method, URL url, RequestParameters parameters) {
    this.url = url;
    this.method = method;
    this.parameters = parameters;
  }

  public Request url(URL url) {
    this.url = url;
    return this;
  }

  public Request parameters(RequestParameters parameters) {
    this.parameters = parameters;
    return this;
  }

  String query() {
    try {
      return parameters.format(null);
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public URL fullUrl() throws MalformedURLException {
    String query = this.url.getQuery();
    String params = query();
    query = (new Strings(query, params)).join("&");

    String spec = this.url.getPath();
    if (!query.isEmpty()) {
      spec += "?" + query;

      return new URL(this.url, spec);
    }

    return this.url;
  }

  abstract public Response execute() throws IOException;

  abstract public void cancel();

  public void setFailure(Exception failure) {
    this.failure = failure;
  }

  public Exception getFailure() {
    return failure;
  }

  @Override
  public String toString() {
    URL fullUrl = null;
    try {
      fullUrl = fullUrl();
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    }
    return String.format("%s %s", method.toString(), fullUrl);
  }

}
