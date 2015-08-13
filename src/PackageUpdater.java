import Model.App;
import Model.AppListMerger;
import Model.Category;
import Model.WebCrawler.GooglePlaySpider;
import Utils.IOUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by yulw on 8/13/2015.
 */
public class PackageUpdater {
    ArrayList<Category> availableCategoriesList=new ArrayList<Category>();

    GooglePlaySpider spider;
    PackageUpdater() {
        spider = new GooglePlaySpider(new IntervalList());
        init();
    }
    PackageUpdater(AppListMerger merger) {
        spider = new GooglePlaySpider(merger);
        init();
    }
    void init() {
        availableCategoriesList.addAll(Arrays.asList(spider.getCategory()));
        Category topCharts = new Category();
        topCharts.title = "top_chart";
        topCharts.href = "/store/apps/top";
        topCharts.parent = "";
        availableCategoriesList.add(topCharts);
    }
    void updateList(String[] keywordList) {
        for (String keyword : keywordList) {
            String[] packageList = getPackageNameList(keyword);
            if (packageList != null) {
                for (String packageName : packageList)
                    System.out.println(keyword + " " + packageName);
            }
        }
    }

    String[] getPackageNameList(String keyword) {
        String[] packageLists = null;
        for (Category category : availableCategoriesList) {
            if (category.title.toLowerCase().contains(keyword)) {
                App[] apps = spider.parseCategory(category);
                //get package string[]
                packageLists = IOUtil.getPackagesName(apps);
            }
        }
        return packageLists;
    }
}
