import javax.xml.bind.Element;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
/**
 * Created by yulw on 8/11/2015.
 */
public class Main {
    private final static String GooglePlay="https://play.google.com/store";
    public static void main(String[] args) {
        GooglePlaySpider spider=new GooglePlaySpider();
        spider.initConnection(GooglePlay);
        spider.crawl("Games");

        return;
    }

}
