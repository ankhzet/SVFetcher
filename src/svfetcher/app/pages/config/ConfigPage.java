package svfetcher.app.pages.config;

import ankh.config.Config;
import ankh.ioc.annotations.DependencyInjection;
import ankh.pages.AbstractPage;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class ConfigPage extends AbstractPage {

  @DependencyInjection()
  protected Config config;

  @Override
  public String pathFragment() {
    return null;
  }

  @Override
  protected Node buildNode() {
    return new VBox();
  }

  @Override
  protected void ready() {
    setTitle("Settings");
  }

}
