import Model.App;
import Model.Category;
import Model.WebCrawler.GooglePlaySpider;
import Utils.IOUtil;

/**
 * Created by yulw on 8/11/2015.
 */
public class Main
{
    public static void main(String[] args) {
        System.setProperty("https.proxyHost", "127.0.0.1");
        System.setProperty("https.proxyPort", "8087");
        PackageUpdater pu=new PackageUpdater();
        String[] comicsPackages=pu.getPackageNameList("comics");

        //get packageName using keywords.A package name list would be returned if the keyword matches one of the GooglePlay category.
        if(comicsPackages!=null) {
            for(String topItem:comicsPackages)
                System.out.println("comics: "+topItem);
        }

        //get app list for a keyword array. The detailed information is saved into files.
        String[] keywordList = {"communication", "productivity", "news", "music", "transportation", "travel", "top"};
        pu.updateList(keywordList);


        //reload the information from files
        String[] topFreeList=IOUtil.findAllPackagesInCategoryContains("top free ","./result",new IntervalList());
        if(topFreeList!=null) {
            for(String topItem:topFreeList)
                System.out.println("top free "+topItem);
        }
        String[] topPaidList=IOUtil.findAllPackagesInCategoryContains("top paid ","./result",new IntervalList());
        if(topPaidList!=null) {
            for(String topItem:topPaidList)
                System.out.println("top paid "+topItem);
        }
        String[] productivityList=IOUtil.findAllPackagesInCategoryContains("productivity","./result",new IntervalList());
        if(productivityList!=null) {
            for(String topItem:productivityList)
                System.out.println("productivity "+topItem);
        }
    }

    void test() {
        //we have two choices for merging different clusters in the same categories
        //IntervalList: merge two app list by turns
        //RankList: merge two app lists according their ratingValue
        GooglePlaySpider spider = new GooglePlaySpider(new RankList());
        spider.setGrabRatingInformation(true);
        Category[] categories = spider.getCategory();
        for (Category cat : categories) {
            App[] apps = spider.parseCategory(cat);
            for (App app : apps)
                System.out.println(app + " " + Float.toString(app.normalizedRating));
            //write app list to file
            IOUtil.writeCategory(cat, apps);
        }
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
