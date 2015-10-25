package svfetcher.factories;

import ankh.IoC;
import ankh.factory.ClassFactory;
import ankh.config.Config;
import ankh.utils.Utils;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import svfetcher.App;
import svfetcher.app.MainStage;
import svfetcher.app.sv.pages.fetch.FetchPage;
import svfetcher.app.sv.pages.pick.LinkPage;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.SVRequest;
import svfetcher.app.sv.pages.compose.ComposePage;
import svfetcher.http.CacheableClient;
import svfetcher.http.Proxy;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoCFactoriesRegistrar extends ClassFactory {

  static IoCFactoriesRegistrar registrar;

  public IoCFactoriesRegistrar(IoC ioc) {
    super(ioc);

    registerClass(Config.class, (c, args) -> {
      String src = Utils.isAny(args, () -> ioc.get(App.class).appName());

      return new Config(src, "config");
    });

    registerClass(Proxy.class);
    registerClass(CacheableClient.class);

    registerClass(DocumentBuilder.class, (c, args) -> {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);

      DocumentBuilder b = factory.newDocumentBuilder();
      b.setEntityResolver(null);
      return b;
    });

    registerClass(SVRequest.class);
    registerClass(SV.class);

    registerClass(MainStage.class);
    registerClass(LinkPage.class);
    registerClass(FetchPage.class);
    registerClass(ComposePage.class);

    registerClass(App.class);
  }

  public static void register() {
    registrar = new IoCFactoriesRegistrar(IoC.instance());
  }

  public static void drop() {
    registrar = null;
    IoC.drop();
  }

}
