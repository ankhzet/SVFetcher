package svfetcher.app.pages.config;

import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import svfetcher.app.SVFConfig;
import svfetcher.app.pages.config.ui.AccessoriedConfigNode;
import svfetcher.app.pages.config.ui.ConfigBox;
import svfetcher.app.pages.config.ui.ConfigNode;
import svfetcher.app.pages.config.ui.ConfigUI;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConfigPage extends AbstractPage {

  @DependencyInjection()
  protected SVFConfig config;

  @Override
  public String pathFragment() {
    return null;
  }

  ConfigUI ui;

  @Override
  protected Node buildNode() {
    if (ui == null)
      ui = new ConfigUI(
        box("Common",
            access("FB2 reader",
                   config.readerProperty(),
                   e -> config.pickReader()),
            access("Save folder",
                   config.saveFolderProperty(),
                   e -> config.pickSaveFolder())
        ),
        box("Requests",
            simple("Proxy", config.apiProxyProperty()),
            simple("Default server", config.apiServerProperty()),
            simple("Cache storage", config.apiCacheDirProperty()),
            simple("Cache for (minutes)", config.apiCacheTtlProperty())
        )
      );

    return ui.getNode();
  }

  @Override
  protected void ready() {
    setTitle("Settings");
  }

  ConfigNode box(String caption, ConfigNode... nodes) {
    ConfigBox box = new ConfigBox(caption);
    box.add(nodes);
    return box;
  }

  ConfigNode simple(String caption, StringProperty property) {
    return new ConfigNode(caption + ":", property);
  }

  ConfigNode access(String caption, StringProperty property, EventHandler<ActionEvent> h) {
    return new AccessoriedConfigNode(caption + ":", property, h);
  }

}
