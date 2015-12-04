package svfetcher.app.fb2;

import ankh.utils.Strings;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import svfetcher.app.story.Source;
import svfetcher.app.fb2.builder.nodes.ContainerContainerNode;
import svfetcher.app.fb2.builder.nodes.NamedNode;
import svfetcher.app.fb2.builder.nodes.Node;
import svfetcher.app.fb2.builder.nodes.body.Body;
import svfetcher.app.fb2.builder.nodes.body.Section;
import svfetcher.app.fb2.builder.nodes.common.Date;
import svfetcher.app.fb2.builder.nodes.common.Id;
import svfetcher.app.fb2.builder.nodes.common.Title;
import svfetcher.app.fb2.builder.nodes.description.*;
import svfetcher.app.sv.forum.Post;
import svfetcher.app.sv.forum.Story;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SVStoryFB2Builder extends FB2Builder<Story> {

  public static String fileName(Story story) {
    String link = story.getSource().getUrl();
    File f = new File(link);
    while (f.getName().isEmpty())
      f = f.getParentFile();

    link = f.getName();
    Pattern p = Pattern.compile("(.*)\\.\\d+");
    Matcher m = p.matcher(link);
    if (m.find())
      link = m.group(1);
    return link + ".fb2";
  }

  protected static String encodeEntities(String html) {
    return html.replace("&", "&amp;").replace("<", "&lt;");
  }

  public SVStoryFB2Builder() {
    put("body", new BodySupply());
    put("description", new DescriptionSupply());
  }

  public Node build(Story story) {
    return has(story, null, "");
  }

  @Override
  protected Node formNode(Story data, Node parent, String path) {
    return new FB2();
  }

  class BodySupply extends FB2Supplier {

    public BodySupply() {
      put("title", new BodyTitleSupply());
      put("sections", new SectionsSupply());
    }

    @Override
    protected Node formNode(Story data, Node parent, String path) {
      return append(parent, new Body());
    }

    class BodyTitleSupply extends FB2Supplier {

      @Override
      protected Node formNode(Story data, Node parent, String path) {
        return append(parent, new Title(encodeEntities(data.getTitle())));
      }

    }

    class SectionsSupply extends FB2Supplier {

      @Override
      protected Node formNode(Story data, Node parent, String path) {
        ContainerContainerNode holder = new ContainerContainerNode();
        for (Entry<Source, Post> post : data.entrySet())
          holder.add(new Section(encodeEntities(post.getKey().getName()), post.getValue().stringContents()));
        return append(parent, holder);
      }

    }

  }

  class DescriptionSupply extends FB2Supplier {

    public DescriptionSupply() {
      put("title-info", new TitleInfoSupply());
      put("document-info", new DocumentInfoSupply());
    }

    @Override
    protected Node formNode(Story data, Node parent, String path) {
      return append(parent, new Description());
    }

    class TitleInfoSupply extends FB2Supplier {

      public TitleInfoSupply() {
        put("genre", new GenreSupply());
        put("author", new AuthorSupply());
        put("book-title", new BookTitleSupply());
        put("lang", new LangSupply());
        put("annotation", new AnnotationSupply());
      }

      @Override
      protected Node formNode(Story data, Node parent, String path) {
        return append(parent, new TitleInfo());
      }

      class GenreSupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Genre("Science Fiction"));
        }

      }

      class AuthorSupply extends FB2Supplier {

        public AuthorSupply() {
          put("nickname", new NicknameSupply());
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Author());
        }

        class NicknameSupply extends FB2Supplier {

          @Override
          protected Node formNode(Story data, Node parent, String path) {
            return append(parent, new FirstName(encodeEntities(data.getAuthor().getName())));
          }

        }

      }

      class BookTitleSupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new BookTitle(encodeEntities(data.getTitle())));
        }

      }

      class LangSupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Lang("en"));
        }

      }

      class AnnotationSupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          String annotation = data.getAnnotation();
          return (annotation != null && !annotation.isEmpty())
                 ? append(parent, new Annotation(encodeEntities(annotation)))
                 : null;
        }

      }

    }

    class DocumentInfoSupply extends FB2Supplier {

      public DocumentInfoSupply() {
        put("author", new AuthorSupply());
        put("program-used", new ProgramUsedSupply());
        put("date", new DateSupply());
        put("src-url", new SrcUrlSupply());
        put("id", new IDSupply());
        put("version", new VersionSupply());
        put("history", new HistorySupply());
      }

      @Override
      protected Node formNode(Story data, Node parent, String path) {
        return append(parent, new DocumentInfo());
      }

      class AuthorSupply extends FB2Supplier {

        public AuthorSupply() {
          put("nickname", new NickNameSupply());
          put("homepage", new HomepageSupply());
          put("email", new EMailSupply());
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Author());
        }

        class NickNameSupply extends FB2Supplier {

          @Override
          protected Node formNode(Story data, Node parent, String path) {
            return append(parent, new NamedNode("nickname", "ankhzet"));
          }

        }

        class HomepageSupply extends FB2Supplier {

          @Override
          protected Node formNode(Story data, Node parent, String path) {
            return append(parent, new NamedNode("homepage", "https://github.com/ankhzet/"));
          }

        }

        class EMailSupply extends FB2Supplier {

          @Override
          protected Node formNode(Story data, Node parent, String path) {
            return append(parent, new NamedNode("email", "ankhzet@gmail.com"));
          }

        }

      }

      class ProgramUsedSupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new NamedNode("program-used", "SV-Fetcher"));
        }

      }

      class DateSupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Date());
        }

      }

      class SrcUrlSupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new NamedNode("src-url", data.getSource().getUrl()));
        }

      }

      class IDSupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Id(Strings.md5(data.getSource().getUrl())));
        }

      }

      class VersionSupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return null;
        }

      }

      class HistorySupply extends FB2Supplier {

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return null;
        }

      }

    }

  }

}
