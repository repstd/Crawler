import Model.App;
import Model.Category;
import Model.WebCrawler.GooglePlaySpider;
import Utils.IOUtil;

import java.util.HashMap;

/**
 * Created by yulw on 8/11/2015.
 */
public class Main {
    public static void main(String[] args) {
        //we have two choices for merging different clusters in the same categories
        //IntervalList: merge two app list by turns
        //RankList: merge two app lists according their rating score
        GooglePlaySpider spider = new GooglePlaySpider(new IntervalList());
        /*
        Category[] categories = spider.getCategory();
        for (Category cat : categories) {
            App[] apps = spider.parseCategory(cat);
            //get package name
            String[] packageNames = IOUtil.getPackagesName(apps);
            for (App app : apps)
                System.out.println(app + " " + Float.toString(app.normalizedRating));
            //write app list to file
            IOUtil.writeCategory(cat, apps);
        }
        */
        //we can specify a category manually
        Category topCharts = new Category();
        topCharts.title = "top_chart";
        topCharts.href = "/store/apps/top";
        topCharts.parent = "";
        App[] topChartApp = spider.parseCategory(topCharts);
        for (App app : topChartApp)
            System.out.println(app + " " + Float.toString(app.normalizedRating));
        IOUtil.writeCategory(topCharts, topChartApp);
        //test app list loading and writing
        App[] loaded = IOUtil.loadCategory(topCharts);
        String[] packageNames = IOUtil.getPackagesName(loaded);
        for (String packageName : packageNames)
            System.out.println(packageName);

    }

}
