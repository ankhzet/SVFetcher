package svfetcher.app.pages.compose;

import ankh.ioc.annotations.DependencyInjection;
import ankh.config.Config;
import ankh.pages.AbstractPage;
import ankh.tasks.RunTask;
import ankh.utils.D;
import ankh.utils.Utils;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.action.Action;
import svfetcher.app.serializer.Writer;
import svfetcher.app.story.serialization.fb2.FB2StorySerializer;
import svfetcher.app.sv.forum.Story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ComposePage extends AbstractPage {

  static final String dirConfigKey = "default-folder";
  static final String rdrConfigKey = "fb2-reader";

  @DependencyInjection()
  protected Config config;

  @Override
  protected javafx.scene.Node buildNode() {
    return new VBox(8, new Label(story().toString()));
  }

  @Override
  protected void ready() {
    setTitle("Composing...");
    notify("Ready to save fetched story", new Action("Save", (h) -> compose()));
  }

  @Override
  public String pathFragment() {
    return "Compose";
  }

  Story story() {
    return navDataAtIndex(0, () -> new Story());
  }

  boolean compose() {
    Story story = story();

    FB2StorySerializer serializer = new FB2StorySerializer(story);

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Fiction Book file", "*.fb2");
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(filter);
    chooser.setSelectedExtensionFilter(filter);
    chooser.setInitialFileName(serializer.filename());
    String dir = config.get(dirConfigKey, "");
    if (!dir.isEmpty() && new File(dir).isDirectory())
      chooser.setInitialDirectory(new File(dir));

    File to = chooser.showSaveDialog(null);

    if (to == null)
      return false;

    String pathString = to.getAbsolutePath();

    RunTask<Boolean> task = new RunTask<>(String.format("Saving to \"%s\"...", pathString), () -> {
      Writer writer = new Writer();
      writer.write(serializer);

      return true;
    }).setFailed("Failed to save to " + pathString);

    if (!perform(task))
      return false;

    boolean saved = false;
    try {
      if (saved = task.get()) {
        config.set(dirConfigKey, to.getParent());

        notify(
          "Successfuly saved to " + pathString,
          new Action("Open", (h) -> open(to))
        );
      }
    } catch (InterruptedException | ExecutionException ex) {
    }

    return saved;
  }

  void open(File file) {
    String reader = config.get(rdrConfigKey, "");
    if (reader.isEmpty()) {
      Optional<ButtonType> r = D.confirm(null, "FB2 reader not set, choose one?");
      if (!(r.isPresent() && r.get() == ButtonType.OK))
        return;

      FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Executable file", "*.exe");
      FileChooser chooser = new FileChooser();
      chooser.getExtensionFilters().add(filter);
      chooser.setSelectedExtensionFilter(filter);
      String dir = config.get(dirConfigKey, "");
      if (!dir.isEmpty() && new File(dir).isDirectory())
        chooser.setInitialDirectory(new File(dir));

      File choosed = chooser.showOpenDialog(null);

      if (choosed != null)
        config.set(rdrConfigKey, reader = choosed.getAbsolutePath());
    }

    if (reader.isEmpty()) {
      D.alert("No FB2 reader set", "In order to open FB2 file you need to configure compatible reader.");
      return;
    }

    try {
      Utils.open(String.format("\"%s\" \"%s\"", reader, file.getAbsolutePath()));
    } catch (Exception e) {
      error("Failed to open " + file.getAbsolutePath(), e);
    }
  }

}
