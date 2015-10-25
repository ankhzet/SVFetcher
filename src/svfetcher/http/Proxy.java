package svfetcher.http;

import ankh.annotations.DependencyInjection;
import ankh.config.Config;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Proxy extends java.net.Proxy {

  @DependencyInjection()
  protected Config config;

  String proxy;
  InetSocketAddress addr;

  public Proxy() {
    super(Type.HTTP, new InetSocketAddress(0));
  }

  @Override
  public SocketAddress address() {
    String cfg = cfg();
    if (cfg.isEmpty())
      return null;

    return addr;
  }

  @Override
  public Type type() {
    return cfg().isEmpty() 
    ? Type.DIRECT 
    : Type.HTTP;
  }

  String cfg() {
    if (proxy == null) {
      proxy = config.get("api.proxy", "");

      if (proxy.isEmpty()) {
        String proxyHost = System.getProperty("http.proxyHost", "");
        if (!proxyHost.isEmpty()) {
          int proxyPort = Integer.valueOf(System.getProperty("http.proxyPort", "80"));

          proxy = String.format("%s:%d", proxyHost, proxyPort);
        }
      }

      if (!proxy.isEmpty()) {
        int port = 80;
        Matcher matcher = Pattern.compile("^(.+)(:(\\d+)(.*))$").matcher(proxy);
        if (matcher.find()) {
          port = Integer.valueOf(matcher.group(3));

          proxy = matcher.group(1) + matcher.group(4);
        }

        System.out.printf("Proxy: %s (port: %d)\n", proxy, port);
        addr = new InetSocketAddress(proxy, port);
      }
    }

    return proxy;
  }

}
