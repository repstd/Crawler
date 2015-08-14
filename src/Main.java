import Model.App;
import Model.Category;
import Model.WebCrawler.GooglePlaySpider;
import Utils.IOUtil;

/**
 * Created by yulw on 8/11/2015.
 */
public class Main {

    private static String[] productivityApps = {"com.estrongs.android.pop", "com.surpax.ledflashlight.panel", "cc.forestapp", "com.google.android.calendar",
            "com.microsoft.office.officehub", "com.astrill.astrillvpn", "com.microsoft.skydrive", "com.chrome.beta",
            "com.socialnmobile.dictapps.notepad.color.note", "com.microsoft.office.onenote", "com.vpnoneclick.android", "com.evernote.widget",
            "com.teamviewer.teamviewer.market.mobile", "com.google.android.inputmethod.japanese", "net.daum.android.solcalendar",
            "com.google.android.apps.unveil", "com.youdao.note", "com.qq.qcloud", "com.microsoft.msa.authenticator", "com.tigervpns.android", "la.droid.qr",
            "com.flyingottersoftware.mega", "com.pushbullet.android", "com.metago.astro", "com.anydo.cal", "com.nhn.android.ndrive", "com.ifttt.ifttt",
            "com.speedsoftware.explorer", "at.nk.tools.iTranslate", "uk.co.nickfines.RealCalc", "com.Kingdee.Express", "com.trello",
            "am.sunrise.android.calendar", "com.rockcatstudio", "org.dayup.gtask", "com.google.android.apps.giant", "com.nhn.android.calendar",
            "la.droid.qr.priva", "mobi.infolife.taskmanager", "imoblife.toolbox.full", "com.estmob.android.sendanywhere", "com.advancedprocessmanager",
            "com.github.mobile", "com.rechild.advancedtaskkiller", "com.acompli.acompli", "com.google.android.apps.enterprise.dmagent", "com.netease.mail",
            "com.ylmf.androidclient", "com.qihoo.msafe", "com.tsf.shell.widget.alarm"};

    public static void main(String[] args) {
        PackageUpdater pu = new PackageUpdater();

        //get packageName using keywords.A package name list would be returned if the keyword matches one of the GooglePlay category.
        String[] comicsPackages = pu.getPackageNameList("comics");
        if (comicsPackages != null) {
            for (String topItem : comicsPackages)
                System.out.println("comics: " + topItem);
        }

        //get app list for a keyword array. The detailed information is saved into files.
        String[] keywordList = {"communication", "productivity", "news", "music", "transportation", "travel", "top", "popular"};
        pu.updateList(keywordList);


        //reload the information from files
        String[] topFreeList = IOUtil.findAllPackagesInCategoryContains("top free", "./result", new IntervalList());
        if (topFreeList != null) {
            for (String topItem : topFreeList)
                System.out.println("top free " + topItem);
        }
        String[] topPaidList = IOUtil.findAllPackagesInCategoryContains("top paid", "./result", new IntervalList());
        if (topPaidList != null) {
            for (String topItem : topPaidList)
                System.out.println("top paid " + topItem);
        }

        String[] mostPopular = IOUtil.findAllPackagesInCategoryContains("popular apps", "./result", new IntervalList());
        if (mostPopular != null) {
            for (String topItem : mostPopular)
                System.out.println("Most popular " + topItem);
        }

        String[] productivityList = IOUtil.findAllPackagesInCategoryContains("productivity", "./result", new IntervalList());
        if (productivityList != null) {
            for (String topItem : productivityList)
                System.out.println("productivity " + topItem);
        }

        int common= pu.compare(productivityList, productivityApps, true);
        System.out.println(String.format("common %d ", common));

        int common_2 = pu.compare(productivityList, productivityApps, false);
        System.out.println(String.format("common %d ", common_2));
    }

    static void test() {
        //we have two choices for merging different clusters in the same category
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
