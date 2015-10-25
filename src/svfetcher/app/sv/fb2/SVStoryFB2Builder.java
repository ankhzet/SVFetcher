package svfetcher.app.sv.fb2;

import ankh.utils.Strings;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import svfetcher.app.sv.fb2.nodes.ContainerContainerNode;
import svfetcher.app.sv.fb2.nodes.Node;
import svfetcher.app.sv.fb2.nodes.body.Body;
import svfetcher.app.sv.fb2.nodes.body.Section;
import svfetcher.app.sv.fb2.nodes.common.Date;
import svfetcher.app.sv.fb2.nodes.common.Id;
import svfetcher.app.sv.fb2.nodes.common.Title;
import svfetcher.app.sv.fb2.nodes.description.*;
import svfetcher.app.sv.forum.Link;
import svfetcher.app.sv.forum.Post;
import svfetcher.app.sv.forum.Story;
import svfetcher.app.sv.html.Cleaner;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public class SVStoryFB2Builder extends FB2Builder<Story> {

  public static String fileName(Story story) {
    String link = story.getBase();
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

  public SVStoryFB2Builder() {
    put("body", new BodySupply());
    put("description", new DescriptionSupply());
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

      public BodyTitleSupply() {
      }

      @Override
      protected Node formNode(Story data, Node parent, String path) {
        return append(parent, new Title(data.getTitle()));
      }

    }

    class SectionsSupply extends FB2Supplier {

      public SectionsSupply() {
      }

      @Override
      protected Node formNode(Story data, Node parent, String path) {
        ContainerContainerNode holder = new ContainerContainerNode();
        for (Entry<Link, Post> post : data.entrySet())
          holder.add(new Section(post.getKey().getName(), post.getValue().getContents()));
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

        public GenreSupply() {
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Genre("Science Fiction"));
        }

      }

      class AuthorSupply extends FB2Supplier {

        public AuthorSupply() {
          put("first-name", new FirstNameSupply());
          put("last-name", new LastNameSupply());
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Author());
        }

        class FirstNameSupply extends FB2Supplier {

          public FirstNameSupply() {
          }

          @Override
          protected Node formNode(Story data, Node parent, String path) {
            return append(parent, new FirstName(data.creator().getNickname()));
          }

        }

        class LastNameSupply extends FB2Supplier {

          public LastNameSupply() {
          }

          @Override
          protected Node formNode(Story data, Node parent, String path) {
            return append(parent, new LastName(data.creator().getTitle()));
          }

        }

      }

      class BookTitleSupply extends FB2Supplier {

        public BookTitleSupply() {
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new BookTitle(data.getTitle()));
        }

      }

      class LangSupply extends FB2Supplier {

        public LangSupply() {
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Lang("en"));
        }

      }

      class AnnotationSupply extends FB2Supplier {

        public AnnotationSupply() {
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Annotation(data.firstPost().getContents()));
        }

      }

    }

    class DocumentInfoSupply extends FB2Supplier {

      public DocumentInfoSupply() {
        put("author", new AuthorSupply());
        put("date", new DateSupply());
        put("id", new IDSupply());
      }

      @Override
      protected Node formNode(Story data, Node parent, String path) {
        return append(parent, new TitleInfo());
      }

      class AuthorSupply extends FB2Supplier {

        public AuthorSupply() {
          put("first-name", new FirstNameSupply());
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Author());
        }

        class FirstNameSupply extends FB2Supplier {

          public FirstNameSupply() {
          }

          @Override
          protected Node formNode(Story data, Node parent, String path) {
            return append(parent, new FirstName("https://github.com/ankhzet/sv-fetcher"));
          }

        }

      }

      class DateSupply extends FB2Supplier {

        public DateSupply() {
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Date());
        }

      }

      class IDSupply extends FB2Supplier {

        public IDSupply() {
        }

        @Override
        protected Node formNode(Story data, Node parent, String path) {
          return append(parent, new Id(Strings.md5(data.getBase())));
        }

      }

    }

  }

}
