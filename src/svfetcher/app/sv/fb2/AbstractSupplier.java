package svfetcher.app.sv.fb2;

import svfetcher.app.sv.fb2.nodes.Node;
import ankh.utils.Strings;
import java.util.HashMap;
import svfetcher.app.sv.fb2.nodes.ContainerNode;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Type>
 */
public abstract class AbstractSupplier<Type> extends HashMap<String, InfoSupplier> implements InfoSupplier<Type> {

  @Override
  public Node has(Type data, Node node, String path) {
    if (path.isEmpty()) {
      Node child = formNode(data, node, path);

      if (child != null)
        for (String supplier : keySet())
          has(data, child, supplier + "." + path);

      return child;
    }

    for (String supplier : keySet())
      if (is(path, supplier))
        return get(supplier).has(data, node, next(path));

    throw new RuntimeException(
      String.format("Unknown supplier path [%s] for supplier %s",
                    path,
                    getClass().getSimpleName()
      )
    );
  }

  static boolean is(String a, String b) {
    return a.matches("^" + b + "(\\.|$)");
  }

  static String next(String path) {
    Strings parts = Strings.explode(path, ".");
    parts.shift();
    return parts.join(".");
  }

  static Node append(Node parent, Node child) {
    if (parent instanceof ContainerNode)
      ((ContainerNode) parent).add(child);

    return child;
  }

  protected abstract Node formNode(Type data, Node node, String path);

}
