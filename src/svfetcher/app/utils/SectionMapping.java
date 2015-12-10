package svfetcher.app.utils;

import ankh.db.Model;
import ankh.db.ModelBuilder;
import ankh.db.Table;
import ankh.ioc.IoC;
import ankh.ioc.annotations.DependencyInjection;
import ankh.ioc.exceptions.FactoryException;
import java.util.*;
import svfetcher.app.utils.tables.SectionMappingTable;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SectionMapping extends Model<String> {

  @DependencyInjection()
  protected SectionMappingTable table;

  public String url;
  public String title;
  public boolean suppressed;

  public SectionMapping() {
  }

  public SectionMapping(String url) {
    this.url = url;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setSuppressed(boolean suppressed) {
    this.suppressed = suppressed;
  }

  public boolean isSuppressed() {
    return suppressed;
  }

  public static Map<String, SectionMapping> filterMappings(List<SectionMapping> mappings, List<String> urls) {
    Map<String, SectionMapping> filtered = new LinkedHashMap<>(urls.size());

    for (String url : urls) {
      SectionMapping found = null;
      for (SectionMapping mapping : mappings)
        if (url.equals(mapping.url)) {
          found = mapping;
          break;
        }

      if (found != null && found.isSuppressed())
        continue;

      filtered.put(url, found);
    }

    return filtered;
  }

  @Override
  public String toString() {
    return String.format("{%s%s -> %s}", suppressed ? "@" : "", url, title);
  }

  @Override
  public Table table() {
    return table;
  }

  @Override
  public String id() {
    return url;
  }

  public static ModelBuilder<SectionMapping, String> model() {
    return Model.modelBuilder(SectionMapping.class);
  }

  public static SectionMapping saveModel(SectionMapping r) {
    Objects.requireNonNull(r);
    try {
      IoC.resolve(r);
    } catch (FactoryException ex) {
      throw new RuntimeException(ex);
    }

    return (r.save() != null) ? r : null;
  }

}
