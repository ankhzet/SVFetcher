package svfetcher.cache;

import java.io.*;
import java.nio.ByteBuffer;
import svfetcher.http.StreamBufferer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FileCache extends AbstractCache<InputStream, Boolean> implements Cache<InputStream, Boolean> {

  FilesytemCache fs;

  public FileCache(String directory) {
    fs = new FilesytemCache(directory);
  }

  @Override
  public Boolean has(String key) {
    return fs.has(key) != null;
  }

  @Override
  public InputStream get(String key) throws IOException {
    File file = fs.get(key);
    if (file == null)
      return null;

    return new BufferedInputStream(new FileInputStream(file));
  }

  @Override
  public InputStream put(String key, InputStream object, long ttl) throws IOException {
    try (FileOutputStream stream = new FileOutputStream(fs.key(key), false)) {
      ByteBuffer buffer = StreamBufferer.buffer(object);
      stream.write(buffer.array(), 0, buffer.limit());
      return new FileInputStream(fs.key(key));
    }
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
