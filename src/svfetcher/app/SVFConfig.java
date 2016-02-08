package svfetcher.app;

import ankh.app.AppConfig;
import java.io.File;
import javafx.beans.property.StringProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SVFConfig extends AppConfig {

  public SVFConfig(String src, String def) {
    super(src, def);
  }

  public SVFConfig(String src) {
    super(src);
  }

  public String getReader() {
    return getString(C_READER);
  }

  public void setReader(String reader) {
    set(C_READER, reader);
  }

  public String getSaveFolder() {
    return getString(C_SAVEFOLDER);
  }

  public void setSaveFolder(String saveFolder) {
    set(C_SAVEFOLDER, saveFolder);
  }

  public boolean getDebug() {
    return debugProperty().asBoolean(false);
  }

  public void setDebug(boolean debug) {
    set(C_SAVEFOLDER, Boolean.toString(debug));
  }

  private static final String C_READER = "fb2-reader";
  private static final String C_SAVEFOLDER = "default-folder";
  private static final String C_DEBUG = "debug";

  public ConvertableProperty debugProperty() {
    return property(C_DEBUG);
  }

  public StringProperty readerProperty() {
    return property(C_READER);
  }

  public StringProperty saveFolderProperty() {
    return property(C_SAVEFOLDER);
  }

  public String pickReader() {
    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Executable file", "*.exe");
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(filter);
    chooser.setSelectedExtensionFilter(filter);

    return pickFile(readerProperty(), directory -> {
      if (directory != null)
        chooser.setInitialDirectory(directory);

      return chooser.showOpenDialog(null);
    });
  }

  public String pickSaveFolder() {
    DirectoryChooser chooser = new DirectoryChooser();

    return pickFile(saveFolderProperty(), directory -> {
      if (directory != null)
        chooser.setInitialDirectory(directory);

      return chooser.showDialog(null);
    });
  }

  private String pickFile(StringProperty property, Chooser chooser) {
    String dir = property.get();

    File directory = null;
    if (!dir.isEmpty()) {
      directory = new File(dir);
      if (!directory.isDirectory())
        directory = directory.getParentFile();
    }

    File choosed = chooser.choose(directory);

    if (choosed != null) {
      String choosedFile = choosed.getAbsolutePath();
      if (!dir.equals(choosedFile))
        property.set(dir = choosedFile);
    }

    return dir;
  }

  private interface Chooser {

    File choose(File init);

  }

}
