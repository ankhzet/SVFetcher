package svfetcher.app.utils;

import ankh.db.Model;
import ankh.db.Table;
import ankh.ioc.IoC;
import ankh.ioc.annotations.DependencyInjection;
import ankh.ioc.exceptions.FactoryException;
import java.util.List;
import java.util.Objects;
import svfetcher.app.utils.tables.RedirectsTable;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Redirect extends Model<String> {

  @DependencyInjection()
  protected RedirectsTable table;

  public String url;
  public String redirect;
  public long timestamp;

  public Redirect() {
  }

  public Redirect(String url, String redirect) {
    this.url = url;
    this.redirect = redirect;
    this.timestamp = System.currentTimeMillis();
  }

  @Override
  public String toString() {
    return String.format("{%s -> %s}", url, redirect);
  }

  @Override
  public Table table() {
    return table;
  }

  @Override
  public String id() {
    return url;
  }

  @Override
  public String save() {
    if (timestamp == 0)
      timestamp = System.currentTimeMillis();

    return super.save();
  }

  public static class Redirects {

    public static Redirect save(Redirect r) {
      Objects.requireNonNull(r);
      try {
        IoC.resolve(r);
      } catch (FactoryException ex) {
        throw new RuntimeException(ex);
      }

      if (r.save() != null)
        return r;
      return null;
    }

    public static Redirect find(String id) {
      return Model.find(Redirect.class, id);
    }

    public static List<Redirect> all() {
      return Model.all(Redirect.class);
    }

    public static void all(int chunk, ChunkedConsumer<Redirect> consumer) {
      Model.all(Redirect.class, chunk, consumer);
    }

  }

}
