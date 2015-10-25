package svfetcher.http;

import java.io.IOException;
import java.io.InputStream;
import svfetcher.cache.AbstractCache;
import svfetcher.cache.Cache;
import svfetcher.cache.FileCache;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ResponseCache extends AbstractCache<Response, Boolean> implements Cache<Response, Boolean> {

  FileCache fs;

  public ResponseCache(String directory) {
    fs = new FileCache(directory);
  }

  @Override
  public Boolean has(String key) {
    return fs.has(key) != null;
  }

  @Override
  public Response get(String key) throws IOException {
    InputStream cache = fs.get(key);
    if (cache == null)
      return null;

    return new Response(cache);
  }

  @Override
  public Response put(String key, Response response, long ttl) throws IOException {
    InputStream stream = fs.put(key, response.decodedStream(), ttl);
    if (stream != null)
      return response.setStream(stream);

    return null;
  }

  @Override
  public long cleanup() {
    return fs.cleanup();
  }

  @Override
  public void forget(String key) {
    fs.forget(key);
  }

}
