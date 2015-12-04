package svfetcher.app.story.serialization.fb2.builder;

import ankh.utils.Strings;
import svfetcher.app.story.Source;
import svfetcher.app.story.Story;
import svfetcher.app.story.serialization.fb2.builder.nodes.FB2;
import svfetcher.app.story.serialization.fb2.builder.nodes.description.Genre;
import svfetcher.app.story.serialization.fb2.builder.nodes.description.Lang;
import svfetcher.app.story.serialization.fb2.builder.nodes.description.BookTitle;
import svfetcher.app.story.serialization.fb2.builder.nodes.description.DocumentInfo;
import svfetcher.app.story.serialization.fb2.builder.nodes.description.FirstName;
import svfetcher.app.story.serialization.fb2.builder.nodes.description.TitleInfo;
import svfetcher.app.story.serialization.fb2.builder.nodes.description.Annotation;
import svfetcher.app.story.serialization.fb2.builder.nodes.description.Description;
import svfetcher.app.story.serialization.fb2.builder.nodes.description.Author;
import svfetcher.app.story.serialization.fb2.builder.nodes.ContainerContainerNode;
import svfetcher.app.story.serialization.fb2.builder.nodes.NamedNode;
import svfetcher.app.story.serialization.fb2.builder.nodes.Node;
import svfetcher.app.story.serialization.fb2.builder.nodes.body.Body;
import svfetcher.app.story.serialization.fb2.builder.nodes.body.Section;
import svfetcher.app.story.serialization.fb2.builder.nodes.common.Date;
import svfetcher.app.story.serialization.fb2.builder.nodes.common.Id;
import svfetcher.app.story.serialization.fb2.builder.nodes.common.Title;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 * @param <T>
 */
public class SVStoryFB2Builder<T extends svfetcher.app.story.Section<?>> extends FB2Builder<Story<T>> {

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
      protected Node formNode(Story<T> data, Node parent, String path) {
        ContainerContainerNode holder = new ContainerContainerNode();
        for (Entry<Source, T> post : data.entrySet())
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
