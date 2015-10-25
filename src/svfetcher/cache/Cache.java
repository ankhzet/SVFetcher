package svfetcher.cache;

import java.io.IOException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Type>
 * @param <Has>
 */
public interface Cache<Type, Has> {

  public Has has(String key);

  public Type get(String key) throws IOException;

  public Type put(String key, Type object, long ttl) throws IOException;

  public Type add(String key, Type object, long ttl) throws IOException;

  public Type remember(String key, Remember<Type> supplier, long ttl) throws IOException;

  public void forget(String key);

  public long cleanup();

  interface Remember<Type> {
    Type get() throws IOException;
  }

}
