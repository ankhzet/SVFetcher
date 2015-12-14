package svfetcher.app;

import ankh.AbstractMainStage;
import ankh.pages.breadcrumps.Breadcrumbs;
import ankh.pages.Page;
import ankh.utils.Utils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import svfetcher.app.pages.pick.LinkPage;
import svfetcher.factories.IoCFactoriesRegistrar;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MainStage extends AbstractMainStage {

  Breadcrumbs crumbs;

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
    return Utils.pass(super.navigateTo(id, args), (Utils.PassThrough<Boolean>) (nav) -> {
      if (nav) {
        crumbs.push(getCurrent(), args);
        showCrumbs();
      }
      return nav;
    });
  }

  void showCrumbs() {
    boolean show = true;
    Page currentPage = getCurrent();
    if (currentPage != null)
      if (currentPage.pathFragment() == null)
        show = false;

    crumbs.setVisible(show);

    crumbs.rebuild((p) -> {
      navigateTo(p.page().getClass(), p.arguments());
    });
  }

  @Override
  public void navigateOut() {
    IoCFactoriesRegistrar.drop();
    super.navigateOut();
  }

}
