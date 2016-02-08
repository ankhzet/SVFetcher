package svfetcher.app.serializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Writer {

  public int write(Writable writable) throws IOException {
    String filename = writable.filename();
    Objects.requireNonNull(filename);

    try (FileOutputStream os = new FileOutputStream(filename)) {
      return write(writable, os);
    }
  }

  public int write(Writable writable, OutputStream os) throws IOException {
    int total = 0;
    try (InputStream is = writable.serialize()) {
      byte[] buffer = new byte[1024 * 256];
      int got;
      while ((got = is.read(buffer)) >= 0) {
        os.write(buffer, 0, got);
        total += got;
      }
    }
    
    return total;
  }

}
