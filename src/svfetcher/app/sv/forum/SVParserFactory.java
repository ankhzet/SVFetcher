package svfetcher.app.sv.forum;

import ankh.ioc.IoC;
import ankh.ioc.annotations.DependencyInjection;
import ankh.ioc.exceptions.FactoryException;
import svfetcher.app.sv.forum.parser.PostParser;
import svfetcher.app.sv.forum.parser.StoryParser;
import svfetcher.app.sv.forum.parser.UserParser;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SVParserFactory {

  @DependencyInjection()
  protected UserParser userParser;

  @DependencyInjection()
  protected PostParser postParser;

  @DependencyInjection()
  protected StoryParser storyParser;

  private static SVParserFactory instance;

  public static synchronized SVParserFactory getInstance() {
    if (instance == null)
      try {
        instance = IoC.make(SVParserFactory.class);
      } catch (FactoryException ex) {
        ex.printStackTrace();
      }

    return instance;
  }

  public static UserParser userParser() {
    return instance.userParser;
  }

  public static PostParser postParser() {
    return instance.postParser;
  }

  public static StoryParser storyParser() {
    return instance.storyParser;
  }

}
