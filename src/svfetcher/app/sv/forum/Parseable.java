package svfetcher.app.sv.forum;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <Type>
 */
public interface Parseable<Type> {

  SVParser<Type> parser();

}
