package svfetcher.app.utils.tables;

import ankh.db.Table;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SectionMappingTable extends Table {

  public static String ID_COLUMN = "url";

  @Override
  protected String schema() {
    return ""
           + "  url text primary key not null"
           + ", title text null"
           + ", suppressed boolean not null default false"
      ;
  }

  public String idColumn() {
    return ID_COLUMN;
  }

}
