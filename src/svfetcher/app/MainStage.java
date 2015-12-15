package svfetcher.app;

import ankh.AbstractMainStage;
import ankh.pages.breadcrumps.Breadcrumbs;
import ankh.pages.Page;
import ankh.pages.breadcrumps.NavPathPoint;
import ankh.utils.Utils;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import svfetcher.app.pages.config.ConfigPage;
import svfetcher.app.pages.pick.LinkPage;
import svfetcher.factories.IoCFactoriesRegistrar;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MainStage extends AbstractMainStage {

  Breadcrumbs crumbs;

  PseudoClass active = PseudoClass.getPseudoClass("active");

  @Override
  public Pane actionPane() {
    Pane pane = super.actionPane();

    Button configButton = new Button();
    configButton.getStyleClass().add("config");
    configButton.setOnAction(h -> {
      boolean atConfig = getCurrent() instanceof ConfigPage;
      if (!atConfig)
        navigateTo(ConfigPage.class);
      else
        navigateBack();

      configButton.pseudoClassStateChanged(active, !atConfig);
    });
    pane.getChildren().add(0, configButton);

    return pane;
  }

  @Override
  public Scene constructScene() {
    return Utils.pass(super.constructScene(), (scene) -> {
      crumbs = new Breadcrumbs();
      crumbs.setPadding(new Insets(0, 0, 8, 0));
      clientArea.setTop(crumbs);
      clientArea.setPrefSize(400, 600);

      navigateTo(LinkPage.class);

      return scene;
    });
  }

  @Override
  public boolean navigateTo(Class<? extends Page> id, Object... args) {
    if (!super.navigateTo(id, args))
      return false;

    crumbs.push(getCurrent(), args);
    showCrumbs();
    return true;
  }

  void showCrumbs() {
    boolean show = true;
    Page currentPage = getCurrent();
    if (currentPage != null)
      if (currentPage.pathFragment() == null)
        show = false;

    if (show)
      crumbs.rebuild((p) -> {
        navigateTo(p.page().getClass(), p.arguments());
      });

    crumbs.setManaged(show);
    crumbs.setVisible(show);
  }

  void navigateBack() {
    if (crumbs.size() <= 1)
      return;

    NavPathPoint<Page, Object[]> p = crumbs.pop();
    if (p != null)
      navigateTo(p.page().getClass(), p.arguments());
  }

  @Override
  public void navigateOut() {
    IoCFactoriesRegistrar.drop();
    super.navigateOut();
  }

}
