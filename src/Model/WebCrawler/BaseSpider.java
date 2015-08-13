package Model.WebCrawler;

import Model.App;
import Model.Category;

import java.util.HashMap;

/**
 * Created by yulw on 8/11/2015.
 */
public abstract class BaseSpider {
    String m_url;
    public abstract void initConnection(String url,HashMap<String,String> paras);

    public abstract Category[] getCategory();

    public abstract void loadState(Category category);

    public abstract void saveState(Category category, App[] apps);

    public abstract App[] parseCategory(Category category);
}
