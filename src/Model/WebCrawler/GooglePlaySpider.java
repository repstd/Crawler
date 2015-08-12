package Model.WebCrawler; /**
 * Created by yulw on 8/11/2015.
 */

import Model.App;
import Model.Category;
import Utils.Constants;
import Utils.IoUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GooglePlaySpider extends BaseSpider {
    Document doc = null;

    @Override
    public void initConnection(String url) {
        m_url = url;
        try {
            HashMap<String, String> paras = new HashMap<String, String>();
            paras.put("hl", "en");
            paras.put("tab", "w8");
            doc = Jsoup.connect(url).data(paras).timeout(0).get();
            System.out.println("connected to " + url + " successfully");
        } catch (Exception e) {
            doc = null;
            e.printStackTrace();
            System.out.print(e.getMessage());
        }
    }

    @Override
    public Category[] getCategory() {
        if (doc == null)
            return new Category[0];
        List<Category> parsedResult = new ArrayList<Category>();
        Elements categoryGroup = doc.getElementsByClass(Constants.GooglePlayCategoryGroupWrapperClass);
        for (Element groupWrapper : categoryGroup) {
            String groupTitle= groupWrapper.getElementsByClass(Constants.GooglePlayCategoryGroupClass).attr(Constants.GooglePlayAttrTitle);
            Elements categoryList = groupWrapper.children();
            for (Element categoryWrapper : categoryList) {
                try {
                    Elements categoryElement = categoryWrapper.getElementsByClass(Constants.GooglePlayCategoryClass);
                    if (categoryElement.size() == 0)
                        continue;
                    Element ele = categoryElement.first();
                    Category category = new Category();
                    category.parent = groupTitle;
                    category.title = ele.attr(Constants.GooglePlayAttrTitle);
                    category.href = ele.attr(Constants.GooglePlayAttrHref);
                    parsedResult.add(category);
                    System.out.println(category.parent + " " + category);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Category[] categories = new Category[parsedResult.size()];
        parsedResult.toArray(categories);
        return categories;
    }

    @Override
    public void crawl(Category category) {
        if (doc == null)
            return;
    }

    @Override
    public void saveState(Category category, App[] apps) {
        //TODO: consider the condition that the task is interrupted
        IoUtil.writeCategory(category,apps);
    }

    @Override
    public void loadState(Category category) {

    }
}
