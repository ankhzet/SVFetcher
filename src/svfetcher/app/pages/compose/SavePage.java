package svfetcher.app.pages.compose;

import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import ankh.tasks.RunTask;
import ankh.utils.D;
import ankh.utils.Utils;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import org.controlsfx.control.action.Action;
import svfetcher.app.SVFConfig;
import svfetcher.app.serializer.Writable;
import svfetcher.app.serializer.Writer;
import svfetcher.app.story.serialization.fb2.FB2StorySerializer;
import svfetcher.app.story.serialization.zip.Zip;
import svfetcher.app.sv.forum.Story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SavePage extends AbstractPage {

  @DependencyInjection()
  protected SVFConfig config;

  private boolean asZip = true;

  private Node row(String labelCaption, Node node) {
    Label label = new Label(labelCaption);
    label.setLabelFor(node);
    label.setTextAlignment(TextAlignment.RIGHT);
    label.setPrefWidth(100);
    HBox.setHgrow(node, Priority.ALWAYS);
    return new HBox(8, label, node);
  }

  @Override
  protected javafx.scene.Node buildNode() {
    Story story = story();
    TextField titleField = new TextField(story.getTitle());
    titleField.textProperty().addListener((l, o, text) -> {
      story.setTitle(text);
    });
    Hyperlink link = new Hyperlink(story.getAuthor().getName());
    link.setOnAction(h -> {
      Utils.safely(() -> {
        String url = story.getAuthor().getSource().getUrl();
        Utils.open("%s", url);
      });
    });

    CheckBox saveAsZip = new CheckBox("Put in ZIP archive");
    saveAsZip.setSelected(asZip);
    saveAsZip.setOnAction(h -> {
      asZip = saveAsZip.isSelected();
    });

    return new VBox(8,
                    row("Author:", link),
                    row("Title:", titleField),
                    row("Chapters:", new Label(String.valueOf(story.size()))),
                    row("Size:", new Label(Utils.humanReadableByteCount(story.contentsLength()))),
                    row("", saveAsZip)
    );
  }

  @Override
  protected void ready() {
    Story story = story();
    setTitle("Saving \"" + story.getTitle() + "\"");
    notify("Ready to save fetched story", new Action("Save", (h) -> compose()));
  }

  @Override
  public String pathFragment() {
    return "Save";
  }

  Story story() {
    return navDataAtIndex(0, () -> new Story());
  }

  private Writable buildWritable(Writable base) {
    CustomWritabe writable = new CustomWritabe(base);

    String extension = (asZip ? Zip.EXTENSION : FB2StorySerializer.EXTENSION);
    String filename = CustomWritabe.flipExtension(writable.filename(), extension);

    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Fiction Book file", "*" + extension);
    FileChooser chooser = new FileChooser();
    chooser.getExtensionFilters().add(filter);
    chooser.setSelectedExtensionFilter(filter);

    chooser.setInitialFileName(filename);

    String saveDir = config.getSaveFolder();
    if (!saveDir.isEmpty()) {
      File dirFile = new File(saveDir);
      if (!dirFile.isDirectory())
        dirFile = dirFile.getParentFile();
      chooser.setInitialDirectory(dirFile);
    }

    File to = chooser.showSaveDialog(null);

    if (to == null)
      return null;

    filename = to.getAbsolutePath();
    if (asZip) {
      writable.setFilename(CustomWritabe.flipExtension(filename, FB2StorySerializer.EXTENSION));
      writable = new CustomWritabe(new Zip(Collections.singletonList(writable)));
    }

    writable.setFilename(filename);

    return writable;
  }

  boolean compose() {
    Writable writable = buildWritable(new FB2StorySerializer(story()));
    if (writable == null)
      return false;

    String filename = writable.filename();

    File to = new File(filename);
    long oldSize = to.exists() ? to.length() : 0;

    return followup((TaskedResultSupplier<File>) supplier -> {
      return supplier.get(() -> {
        return new RunTask<>(String.format("Saving to \"%s\"...", filename), () -> {
          Writer writer = new Writer();
          writer.write(writable);
          return to;
        });
      })
        .setError("Failed to save to " + filename)
        .schedule(savedTo -> {
          if (savedTo == null)
            return;

          if (config.getSaveFolder().isEmpty())
            config.setSaveFolder(savedTo.getParent());

          long newSize = to.length();
          long delta = newSize - oldSize;
          long abs = Math.abs(delta);
          String diff = Utils.humanReadableByteCount(newSize);
          if ((oldSize > 0) && (abs > 0))
            diff += String.format(" (%s%s)", abs < 0 ? "-" : "+", Utils.humanReadableByteCount(abs));

          diff = "[" + diff + "]";

          notify(
            String.format("%s Successfuly saved to %s", diff, savedTo.getAbsolutePath()),
            new Action("Open", (h) -> open(savedTo))
          );
        });
    });
  }

  void open(File file) {
    String reader = config.getReader();
    if (reader.isEmpty()) {
      Optional<ButtonType> r = D.confirm(null, "FB2 reader not set, choose one?");
      if (!(r.isPresent() && r.get() == ButtonType.OK))
        return;

      reader = config.pickReader();
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

  private static class CustomWritabe implements Writable {

    private final Writable writable;

    public CustomWritabe(Writable writable) {
      this.writable = writable;
    }

    private StringProperty filename;

    public StringProperty filenameProperty() {
      if (filename == null)
        filename = new SimpleStringProperty(this, "filename", writable.filename());

      return filename;
    }

    @Override
    public String filename() {
      return filenameProperty().get();
    }

    public void setFilename(String name) {
      filenameProperty().set(name);
    }

    @Override
    public InputStream serialize() {
      return writable.serialize();
    }

    public static String flipExtension(String filename, String extension) {
      return filename.replaceFirst("\\.[^\\.]+$", extension);
    }

  }

}
