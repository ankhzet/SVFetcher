package svfetcher.factories;

import ankh.app.ConfigDatabaseSupplier;
import ankh.ioc.IoC;
import ankh.db.ConnectionBuilder;
import ankh.db.DB;
import ankh.db.ModelBuilder;
import ankh.db.ModelFields;
import ankh.db.databases.SQLiteConnectionBuilder;
import ankh.db.query.SQLGrammar;
import ankh.ioc.factory.ClassFactory;
import java.sql.Connection;
import svfetcher.app.utils.Redirect;
import svfetcher.app.utils.SectionMapping;
import svfetcher.app.utils.tables.RedirectsTable;
import svfetcher.app.utils.tables.SectionMappingTable;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class DBFactory extends ClassFactory<DB> {

  public DBFactory(IoC ioc) {
    super(ioc);

    register(DB.class);

    registerClass(ConfigDatabaseSupplier.class);
    registerClass(SQLiteConnectionBuilder.class);
    registerClass(Connection.class, ioc.get(ConnectionBuilder.class));
    registerClass(SQLGrammar.class);

    registerClass(ModelFields.class);
    registerClass(ModelBuilder.class);

    registerClass(RedirectsTable.class);
    registerClass(Redirect.class);

    registerClass(SectionMappingTable.class);
    registerClass(SectionMapping.class);
  }

}
