package svfetcher.app;

import ankh.Launcher;
import ankh.Splash;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SVFetcherApp extends Launcher {

  @Override
  public ankh.AbstractAppLoadTask loadTask() {
    return new AppLoadTask();
  }

  @Override
  public Splash initSplash() {
    return new Splash();
  }

}
