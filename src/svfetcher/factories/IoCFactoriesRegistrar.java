package svfetcher.factories;

import ankh.app.AppCacheableHttpClient;
import ankh.app.AppConfig;
import ankh.app.AppProxy;
import ankh.http.ServerRequest;
import ankh.ioc.IoC;
import ankh.ioc.factory.ClassFactory;
import ankh.utils.Utils;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import svfetcher.App;
import svfetcher.app.MainStage;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.SVParserFactory;
import svfetcher.app.sv.forum.parser.PostParser;
import svfetcher.app.sv.forum.parser.StoryParser;
import svfetcher.app.sv.forum.parser.UserParser;
import svfetcher.app.sv.pages.compose.ComposePage;
import svfetcher.app.sv.pages.convert.DocumentPage;
import svfetcher.app.sv.pages.fetch.FetchPage;
import svfetcher.app.sv.pages.pick.LinkPage;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoCFactoriesRegistrar extends ClassFactory {

  static IoCFactoriesRegistrar registrar;

  public IoCFactoriesRegistrar(IoC ioc) {
    super(ioc);

    registerClass(AppConfig.class, (c, args) -> {
      String src = Utils.isAny(args, () -> IoC.get(App.class).appName());

      return new AppConfig(src);
    });

    registerSVCore();
    registerPages();

    registerClass(DocumentBuilder.class, (c, args) -> {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);

      DocumentBuilder b = factory.newDocumentBuilder();
      b.setEntityResolver(null);
      return b;
    });

    registerClass(App.class);
  }

  final void registerSVCore() {
    registerClass(AppProxy.class);
    registerClass(AppCacheableHttpClient.class);
    registerClass(ServerRequest.class);

    registerClass(StoryParser.class);
    registerClass(PostParser.class);
    registerClass(UserParser.class);
    registerClass(SVParserFactory.class);
    registerClass(SV.class);
  }

  final void registerPages() {
    registerClass(MainStage.class);
    registerClass(LinkPage.class);
    registerClass(FetchPage.class);
    registerClass(ComposePage.class);
    registerClass(DocumentPage.class);

  }

  public static void register() {
    registrar = new IoCFactoriesRegistrar(IoC.instance());
  }

  public static void drop() {
    registrar = null;
    IoC.drop();
  }

}
