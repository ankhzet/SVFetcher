package svfetcher.app.story.serialization.zip;

import ankh.utils.Strings;
import java.io.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import svfetcher.app.serializer.Writable;
import svfetcher.app.serializer.Writer;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Zip implements Writable {

  public static final String EXTENSION = ".zip";

  List<Writable> wraped;
  String filename;

  private static String extractZipName(List<Writable> wraped) {
    String base = null;

    for (Writable writable : wraped) {
      String name = writable.filename();
      if (base != null) {
        int i = -1, l = Math.min(base.length(), name.length());
        while (++i < l)
          if (base.charAt(i) != name.charAt(i))
            break;

        base = Strings.trimr(base.substring(0, i), "/\\");
      } else
        base = name.replaceFirst("\\.[^\\.]+$", "");
    }

    if (base == null || base.isEmpty())
      return null;

    return base + EXTENSION;
  }

  public Zip(List<Writable> wraped) {
    this(wraped, extractZipName(wraped));
  }

  public Zip(List<Writable> wraped, String filename) {
    this.wraped = wraped;
    this.filename = filename;
  }

  @Override
  public String filename() {
    return filename;
  }

  @Override
  public InputStream serialize() {
    String base = (new File(filename)).getParent();

    Writer writer = new Writer();

    try (InputOutputStream os = new InputOutputStream(1024 * 256)) {
      try (ZipOutputStream zs = new ZipOutputStream(os)) {
        zs.setComment("SVFetcher by ankhzet");
        
        for (Writable writable : wraped) {
          String name = writable.filename();
          name = name
            .replaceFirst("(?i)^" + Pattern.quote(base), "")
            .replaceFirst("^[/\\\\]+", "");

          ZipEntry entry = new ZipEntry(name);
          zs.putNextEntry(entry);

          writer.write(writable, zs);
        }
        
        zs.finish();
      }

      return os.inputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private class InputOutputStream extends ByteArrayOutputStream {

    public InputOutputStream() {
    }

    public InputOutputStream(int size) {
      super(size);
    }

    public InputStream inputStream() {
      return new ByteArrayInputStream(toByteArray());
    }

  }

}
