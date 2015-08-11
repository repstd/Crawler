/**
 * Created by yulw on 8/11/2015.
 */
public abstract class BaseSpider {
    String m_url;
    abstract void initConnection(String url);

    abstract String[] getCategory();

    abstract void loadState();

    abstract void saveState();

    abstract void crawl(String category);
}
