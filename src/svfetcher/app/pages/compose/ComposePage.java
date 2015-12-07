package svfetcher.app.pages.compose;

import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import ankh.tasks.RunTask;
import ankh.utils.D;
import ankh.utils.Utils;
import ankh.config.Config;
import java.io.File;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.action.Action;
import svfetcher.app.serializer.Writable;
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
    Story story = story();

    TextField authorField = new TextField(story.getAuthor().toString());
    authorField.textProperty().addListener((l, o, text) -> {
      story.getAuthor().setName(text);
    });
    Label author = new Label("Author:", authorField);
    author.setContentDisplay(ContentDisplay.RIGHT);

    TextField titleField = new TextField(story.getTitle());
    titleField.textProperty().addListener((l, o, text) -> {
      story.setTitle(text);
    });
    Label title = new Label("Title:", titleField);
    title.setContentDisplay(ContentDisplay.RIGHT);

    Label info = new Label(String.format("Chapters: %d\nSize: %d", story.size(), story.contentsLength()));
    
    HBox.setHgrow(author, Priority.ALWAYS);
    HBox.setHgrow(title, Priority.ALWAYS);
    HBox.setHgrow(info, Priority.ALWAYS);
    
    return new VBox(8, author, title, info);
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

    return followup((TaskedResultSupplier<File>) supplier -> {
      return supplier.get(() -> {
        return new RunTask<>(String.format("Saving to \"%s\"...", pathString), () -> {
          Writer writer = new Writer();
          writer.write(new Writable() {

            @Override
            public String filename() {
              return pathString;
            }

            @Override
            public String serialize() {
              return serializer.serialize();
            }
          });
          return to;
        });
      })
        .setError("Failed to save to " + pathString)
        .schedule(savedTo -> {
          if (savedTo == null)
            return;

          config.set(dirConfigKey, savedTo.getParent());

          notify(
            "Successfuly saved to " + savedTo.getAbsolutePath(),
            new Action("Open", (h) -> open(savedTo))
          );
        });
    });
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
