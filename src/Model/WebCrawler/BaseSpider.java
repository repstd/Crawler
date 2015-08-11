package Model.WebCrawler;

import Model.App;
import Model.Category;

/**
 * Created by yulw on 8/11/2015.
 */
public abstract class BaseSpider {
    String m_url;
    public abstract void initConnection(String url);

    public abstract Category[] getCategory();

    public abstract void loadState(Category category);

    public abstract void saveState(Category category, App[] apps);

    public abstract void crawl(Category category);
}
