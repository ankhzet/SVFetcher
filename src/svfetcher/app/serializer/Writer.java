package svfetcher.app.serializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Writer {

  public void write(Writable writable) throws IOException {
    String filename = writable.filename();
    Objects.requireNonNull(filename);

    File to = new File(filename);

    String contents = writable.serialize();

    Files.write(
      to.toPath(),
      contents.getBytes(),
      StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE
    );
  }

}
