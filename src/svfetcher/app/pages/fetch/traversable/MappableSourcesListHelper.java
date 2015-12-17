package svfetcher.app.pages.fetch.traversable;

import ankh.ioc.IoC;
import ankh.ioc.exceptions.FactoryException;
import java.util.*;
import svfetcher.app.story.Source;
import svfetcher.app.utils.SectionMapping;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MappableSourcesListHelper extends SourcesListHelper {

  private HashMap<String, Source> urlSourceMap;
  private HashMap<Source, SectionMapping> sectionsMapping;

  public MappableSourcesListHelper(Collection<Source> allItems) {
    super(allItems);
  }

  public HashMap<String, Source> getUrlSourceMap() {
    if (urlSourceMap == null) {
      urlSourceMap = new HashMap<>();
      for (Source source : getAllItems()) {
        String url = source.getUrl();
        urlSourceMap.put(url, source);
      }
    }
    return urlSourceMap;
  }

  public Map<Source, SectionMapping> getMappings() {
    if (sectionsMapping == null) {
      HashMap<String, Source> usm = getUrlSourceMap();

      List<SectionMapping> mappings = SectionMapping.model()
        .whereIn("url", usm.keySet().toArray())
        .get();

      sectionsMapping = new HashMap<>();
      for (SectionMapping mapping : mappings)
        sectionsMapping.put(usm.get(mapping.url), mapping);
    }
    return sectionsMapping;
  }

  protected Collection<Source> move(Collection<Source> sources, boolean hide) {
    Collection<Source> update = super.move(sources, hide);

    Map<Source, SectionMapping> mappings = getMappings();

    for (Source item : update) {
      SectionMapping was = mappings.get(item);
      SectionMapping become = remapItem(item, was, hide);
      if (become != was)
        mappings.put(item, become);
    }

    return update;
  }

  public SectionMapping remapItem(Source source, SectionMapping mapping, boolean hide) {
    if (mapping == null)
      if (!hide)
        return mapping;
      else
        try {
          mapping = IoC.resolve(new SectionMapping(source.getUrl()));
        } catch (FactoryException ex) {
          throw new RuntimeException(ex);
        }

    mapping.setSuppressed(hide);
    mapping.save();

    return mapping;
  }

  public Collection<Source> getShownItems() {
    Collection<Source> shown = new ArrayList<>(super.getShownItems());

    HashMap<String, Source> usm = getUrlSourceMap();
    Map<Source, SectionMapping> mappings = getMappings();

    ArrayList<Source> hidden = new ArrayList<>();
    for (String url : usm.keySet()) {
      Source source = usm.get(url);
      SectionMapping mapping = mappings.get(source);

      if (mapping == null || !mapping.suppressed)
        continue;

      hidden.add(source);
    }

    shown.removeAll(hidden);
    return shown;
  }

}
