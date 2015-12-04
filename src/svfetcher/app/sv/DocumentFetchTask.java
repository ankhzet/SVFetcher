package svfetcher.app.sv;

import ankh.ioc.IoC;
import ankh.ioc.exceptions.FactoryException;
import java.net.URL;
import org.w3c.dom.Document;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DocumentFetchTask extends URLDocumentFetchTask<Document> {

  public DocumentFetchTask(URL url) throws FactoryException {
    super(url, document -> document);
    IoC.resolve(this);
  }

}
