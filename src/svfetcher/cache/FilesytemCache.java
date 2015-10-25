package svfetcher.cache;

import ankh.utils.Strings;
import java.io.File;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class FilesytemCache extends AbstractCache<File, File> {
  
  public String directory;

  public FilesytemCache(String directory) {
    this.directory = directory;

    File cache = new File(directory);
    if (!cache.isDirectory())
      cache.mkdir();
  }

  public String key(String key) {
    return Strings.trimr(directory, "/\\") + "/" + Strings.md5(key);
  }

  @Override
  public File has(String filename) {
    File file = new File(key(filename));
    if (file.exists() && !outdated(file))
      return file;

    return null;
  }

  @Override
  public File get(String key) {
    return has(key);
  }

  @Override
  public File put(String key, File object, long ttl) {
    throw new UnsupportedOperationException("Not supported");
  }

  @Override
  public void forget(String filename) {
    File file = new File(key(filename));
    if (file.exists())
      file.delete();
  }

  @Override
  public long cleanup() {
    File d = new File(directory);

    long i = 0;
    for (File file : d.listFiles((File f) -> f.isFile()))
      if (outdated(file)) {
        file.delete();
        i++;
      }

    return i;
  }

  boolean outdated(File file) {
    long now = System.currentTimeMillis();
    long expire = expire(file);

    return now > expire;
  }
  
  long ttl(String key) {
    return 0;
  }
  
  long expire(File file) {
    long cached = file.lastModified();
    long ttl = ttl(file.getName());
    
    return (ttl > 0) ? cached + ttl : Long.MAX_VALUE;
  }

}
