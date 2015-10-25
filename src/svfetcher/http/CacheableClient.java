package svfetcher.http;

import ankh.annotations.DependenciesInjected;
import java.io.IOException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class CacheableClient extends Client {

  public static final String CACHE_DIR = "cache";
  public static final long CACHE_TTL = 3600000 * 24 * 7;

  ResponseCache cache;
  String dir;
  long ttl;

  @Override
  public Response execute(Request request) throws IOException {
    return execute(request, ttl);
  }

  public Response execute(Request request, long ttl) throws IOException {
    return cache.remember(request.fullUrl().toString(), () -> {
      return super.execute(request);
    }, ttl);
  }

  @DependenciesInjected()
  private void diInjected() {
    dir = config.resolveAppDir("api.cache.path", CACHE_DIR);
    ttl = config.get("api.cache.ttl", CACHE_TTL);
    cache = new ResponseCache(dir);
  }

}
