package svfetcher.app;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class Splash extends ankh.Splash {

  @Override
  public void init() {
    super.init();

    loadProgress.setPrefSize(200, 20);
//    loadProgress.setTranslateX(120);
//    
//    progressText.setTranslateX(-84);
//    progressText.setTranslateY(-78);
    progressText.setPrefSize(200, 16);
//    progressText.setTextAlignment(TextAlignment.RIGHT);
  }

}
