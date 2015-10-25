package svfetcher.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class RequestQuery extends ReentrantLock {

  Request request;
  Thread asynkTask;

  public RequestQuery(Request request) {
    this.request = request;
  }

  public Response synk() throws InterruptedException {
    lock();
    try {
      Response r;
      setResponse(r = request.execute());
      if (r.status == HttpURLConnection.HTTP_OK)
        return r;
    } catch (IOException ex) {
      request.setFailure(ex);
    } finally {
      unlock();
    }

    return null;
  }

  synchronized public RequestQuery asynk(Consumer<Response> task) {
    asynkTask = new Thread(() -> {
      Response result = null;
      try {
        result = synk();
      } catch (InterruptedException e) {
        request.setFailure(e);
      }
      try {
        task.accept(result);
      } catch (Exception e) {
        request.setFailure(e);
      }
    });

    asynkTask.start();

    return this;
  }

  synchronized public void cancel() {
    request.cancel();
  }
  
  public Request getRequest() {
    return request;
  }

  private SimpleObjectProperty<Response> response;

  public Response getResponse() {
    return (response == null) ? null : response.get();
  }

  void setResponse(Response r) {
    responseProperty();
    response.set(r);
  }

  public ObservableValue<Response> responseProperty() {
    if (response == null)
      response = new SimpleObjectProperty<>(this, "response", null);
    return response;
  }

}
