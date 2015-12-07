package svfetcher.app.utils.tables;

import ankh.db.Table;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class RedirectsTable extends Table {

  public static String ID_COLUMN = "url";

  @Override
  protected String schema() {
    return ""
           + "  url text primary key not null"
           + ", redirect text not null"
           + ", timestamp integer not null";
  }

  public String idColumn() {
    return ID_COLUMN;
  }

}
