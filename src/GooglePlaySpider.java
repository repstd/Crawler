/**
 * Created by yulw on 8/11/2015.
 */

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.net.URL;
import java.rmi.server.ExportException;
import java.util.*;

public class GooglePlaySpider extends BaseSpider {
    Document doc=null;
    @Override
    void initConnection(String url) {
        m_url=url;
        try {
            System.setProperty("https.proxyHost", "127.0.0.1");
            System.setProperty("https.proxyPort", "8087");
            doc=Jsoup.connect(url).timeout(0).get();
            System.out.println("connected to " + url);
        } catch (Exception e) {
            doc=null;
            e.printStackTrace();
            System.out.print(e.getMessage());
        }
    }

    @Override
    String[] getCategory() {
        List<String> categories=new ArrayList<String>();
        if(doc==null)
            return new String[0];
        return new String[0];
    }

    @Override
    void crawl(String category) {
        if(doc==null)
            return;
    }

    @Override
    void saveState() {

    }

    @Override
    void loadState() {

    }
}
