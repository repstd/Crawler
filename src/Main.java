import Model.Category;
import Model.WebCrawler.GooglePlaySpider;
import Utils.Constants;

import java.util.HashMap;

/**
 * Created by yulw on 8/11/2015.
 */
public class Main {
    public static void main(String[] args) {
        GooglePlaySpider spider = new GooglePlaySpider();
        HashMap<String, String> paras = new HashMap<String, String>();
        spider.initConnection(Constants.GooglePlayApps, paras);
        Category[] categories = spider.getCategory();
        for (Category cat : categories) {
            //spider.parseCategory(cat);
        }
        Category topCharts= new Category();
        topCharts.title = "top_chart";
        topCharts.href = "/store/apps/top";
        topCharts.parent = "";
        spider.parseCategory(topCharts);
    }

}
