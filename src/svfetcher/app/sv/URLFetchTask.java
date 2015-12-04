package svfetcher.app.sv;

import ankh.http.Request;
import ankh.http.loading.HTMLLoader;
import ankh.http.query.ResourceQuery;
import ankh.ioc.IoC;
import ankh.ioc.annotations.DependencyInjection;
import ankh.ioc.exceptions.FactoryException;
import ankh.zet.http.http.loading.AbstractURLDocumentFetchTask;
import java.net.URL;
import org.w3c.dom.Document;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Result>
 */
public class URLFetchTask<Result> extends AbstractURLDocumentFetchTask<Request, Result> {

  @DependencyInjection()
  protected HTMLLoader loader;
  
  private final ResourceSupplier<Document, Result> supplier;

  public URLFetchTask(Request request, ResourceSupplier<Document, Result> supplier) throws FactoryException {
    super(request);
    this.supplier = supplier;
  }

  public URLFetchTask(URL url, ResourceSupplier<Document, Result> supplier) throws FactoryException {
    this(IoC.resolve(Request.class, url), supplier);
  }

  @Override
  protected ResourceQuery<Document, Result> query(Request request) {
    return loader.query(request, supplier);
  }

  public interface ResourceSupplier<Document, Result> extends ResourceQuery.ResourceSupplier<Document, Result> {
    
  }

}
