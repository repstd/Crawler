import Model.App;
import Model.Category;
import Model.WebCrawler.GooglePlaySpider;
import Utils.Constants;
import Utils.IOUtil;

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
            if(cat.title.equalsIgnoreCase("productivity")) {
                App[] result=spider.parseCategory(cat);
                //IOUtil.writeCategory(cat,result);
                for(App app:result)
                    System.out.println(app+" "+Float.toString(app.normalizedRating));
            }
        }
        Category topCharts= new Category();
        topCharts.title = "top_chart";
        topCharts.href = "/store/apps/top";
        topCharts.parent = "";
        //App[] topChartApp=spider.parseCategory(topCharts);
        //IOUtil.writeCategory(topCharts,topChartApp);
        //test app list loading and writing
        App[] loaded=IOUtil.loadCategory(topCharts);
        String[] packageNames=IOUtil.getPackagesName(loaded);
        for(String packageName:packageNames)
            System.out.println(packageName);

    }

}
