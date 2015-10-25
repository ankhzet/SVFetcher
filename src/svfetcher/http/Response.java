package svfetcher.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Response {

  public interface ProgressListener {

    void progress(long done, long max);

  }

  public int status = HttpURLConnection.HTTP_OK;
  private InputStream stream;

  HttpURLConnection connection;

  private ProgressListener listener;

  public Response(HttpURLConnection connection) {
    this.connection = connection;
    try {
      status = connection.getResponseCode();
      switch (status) {
      case HttpURLConnection.HTTP_OK:
        stream = connection.getInputStream();
        String encoding = connection.getContentEncoding();
        if (encoding != null && encoding.equalsIgnoreCase("gzip"))
          stream = new GZIPInputStream(stream);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public Response(InputStream stream) {
    this.stream = stream;
  }

  public Response setStream(InputStream stream) {
    this.stream = stream;
    return this;
  }

  public void setListener(ProgressListener listener) {
    this.listener = listener;
  }

  public InputStream decodedStream() throws IOException {
    return new ProgressedBufferedStream(stream);
  }

  void progress(long readed) {
    if (listener != null)
      listener.progress(readed, getTotalSize());
  }

  public long getTotalSize() {
    return (connection != null) ? connection.getContentLengthLong() : -1;
  }

  @Override
  public String toString() {
    return String.format("[%d] %s, %s", status, connection, stream);
  }

  class ProgressedBufferedStream extends BufferedInputStream {

    public ProgressedBufferedStream(InputStream in) {
      super(new ProgressedStream(in));
    }

  }

  class ProgressedStream extends BufferedInputStream {

    long readed = 0;

    public ProgressedStream(InputStream in) {
      super(in);
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
      int r = super.read(b, off, len);

      readed += r;

      if (r >= 0)
        progress(readed);

      return r;
    }

  }

}

class StreamCharsetConverter {

  public static ByteBuffer convert(InputStream stream, String charset) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset))) {

      StringBuilder sb = new StringBuilder(stream.available());
      String r;
      while ((r = reader.readLine()) != null)
        sb.append(r)
          .append("\n");

      return ByteBuffer.wrap(sb.toString().getBytes());
    }
  }

}
