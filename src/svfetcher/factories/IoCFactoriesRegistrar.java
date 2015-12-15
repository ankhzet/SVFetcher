package svfetcher.factories;

import ankh.app.AppCacheableHttpClient;
import ankh.app.AppConfig;
import ankh.app.AppProxy;
import ankh.fs.cache.FileCache;
import ankh.fs.cache.FileStreamCache;
import ankh.http.ServerRequest;
import ankh.http.cached.ResponseCache;
import ankh.ioc.IoC;
import ankh.ioc.factory.ClassFactory;
import ankh.ioc.registrar.ClassFactoryRegistrar;
import ankh.ioc.registrar.FactoryRegistrar;
import ankh.utils.Utils;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import svfetcher.App;
import svfetcher.app.MainStage;
import svfetcher.app.SVFConfig;
import svfetcher.app.sv.SV;
import svfetcher.app.sv.forum.SVParserFactory;
import svfetcher.app.sv.forum.parser.PostParser;
import svfetcher.app.sv.forum.parser.StoryParser;
import svfetcher.app.sv.forum.parser.UserParser;
import svfetcher.app.pages.compose.ComposePage;
import svfetcher.app.pages.config.ConfigPage;
import svfetcher.app.pages.convert.DocumentPage;
import svfetcher.app.pages.fetch.FetchPage;
import svfetcher.app.pages.pick.LinkPage;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class IoCFactoriesRegistrar extends ClassFactory {

  static IoCFactoriesRegistrar registrar;

  static FactoryRegistrar<?> db;

  public IoCFactoriesRegistrar(IoC ioc) {
    super(ioc);

    registerClass(SVFConfig.class, (c, args) -> {
      String src = Utils.isAny(args, () -> IoC.get(App.class).appName());

      return new SVFConfig(src);
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

    db = new ClassFactoryRegistrar<>(new DBFactory(ioc));
  }

  final void registerSVCore() {
    registerClass(ResponseCache.class, (c, args) -> {
      SVFConfig config = IoC.get(SVFConfig.class);
      String dir = Utils.isAny(args, () -> config.getApiCacheDir());
      FileCache fileCache = new FileCache(dir);
      FileStreamCache fsCache = new FileStreamCache(fileCache);

      return new ResponseCache(fsCache);
    });
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

    registerClass(ConfigPage.class);
  }

  public static void register() {
    registrar = new IoCFactoriesRegistrar(IoC.instance());
  }

  public static void drop() {
    registrar = null;
    IoC.drop();
  }

}
