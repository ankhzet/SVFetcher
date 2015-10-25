package svfetcher.app;

import svfetcher.factories.IoCFactoriesRegistrar;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class AppLoadTask extends ankh.AbstractAppLoadTask {

  @Override
  public Runnable prepare() {
    return () -> {
      IoCFactoriesRegistrar.register();
    };
  }

}
