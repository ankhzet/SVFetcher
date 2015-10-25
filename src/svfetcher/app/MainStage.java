package svfetcher.app;

import svfetcher.app.sv.SV;
import ankh.AbstractMainStage;
import ankh.pages.breadcrumps.Breadcrumbs;
import ankh.annotations.DependencyInjection;
import ankh.pages.Page;
import ankh.utils.Utils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import svfetcher.app.sv.pages.pick.LinkPage;
import svfetcher.factories.IoCFactoriesRegistrar;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class MainStage extends AbstractMainStage {

//  private static final ImageView image = new ImageView(new Image("/resources/icon16x16.png"));
//
//  private Collection<? extends Action> actions = Arrays.asList(
//          new ActionGroup("Group 1", image, new DummyAction("Action 1.1", image),
//                          new DummyAction("Action 1.2")),
//          new ActionGroup("Group 2", image, new DummyAction("Action 2.1"),
//                          ACTION_SEPARATOR,
//                          new ActionGroup("Action 2.2", new DummyAction("Action 2.2.1"),
//                                          new DummyAction("Action 2.2.2")),
//                          new DummyAction("Action 2.3")),
//          ACTION_SEPARATOR,
//          new DummyAction("Action 3", image),
//          new ActionGroup("Group 4", image, new DummyAction("Action 4.1", image),
//                          new DummyAction("Action 4.2"))
//  );
//
//  static class DummyAction extends Action {
//
//    public DummyAction(String name, Node image) {
//      super(name);
//      setGraphic(image);
//      setEventHandler(ae -> String.format("Action '%s' is executed", getText()));
//    }
//
//    public DummyAction(String name) {
//      super(name);
//    }
//
//    @Override
//    public String toString() {
//      return getText();
//    }
//
//  }
  @DependencyInjection()
  protected SV sv;

//  ToolBar toolbar;
  Breadcrumbs crumbs;

  @Override
  public Scene constructScene() {
    return Utils.pass(super.constructScene(), (scene) -> {
      crumbs = new Breadcrumbs();
      crumbs.setPadding(new Insets(8));
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
