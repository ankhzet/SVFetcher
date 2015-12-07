package svfetcher.app.serializer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Writer {

  public void write(Writable writable) throws IOException {
    String filename = writable.filename();
    Objects.requireNonNull(filename);

    try (FileWriter w = new FileWriter(filename)) {
      String contents = writable.serialize();

      w.write(contents);
    }
  }

}
