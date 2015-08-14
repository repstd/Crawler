import Model.App;
import Model.Category;
import Model.WebCrawler.GooglePlaySpider;
import Utils.Constants;
import Utils.IOUtil;

/**
 * Created by yulw on 8/11/2015.
 */
public class Main {

    public static void main(String[] args) {
        PackageUpdater pu = new PackageUpdater();
        pu.updateList(Constants.ResultDir + "/in.txt", Constants.ResultDir + "/out.txt");
    }
    static void test() {
        String[] productivityApps = {
                "com.estrongs.android.pop", "com.surpax.ledflashlight.panel", "cc.forestapp", "com.google.android.calendar",
                "com.microsoft.office.officehub", "com.astrill.astrillvpn", "com.microsoft.skydrive", "com.chrome.beta",
                "com.socialnmobile.dictapps.notepad.color.note", "com.microsoft.office.onenote", "com.vpnoneclick.android", "com.evernote.widget",
                "com.teamviewer.teamviewer.market.mobile", "com.google.android.inputmethod.japanese", "net.daum.android.solcalendar",
                "com.google.android.apps.unveil", "com.youdao.note", "com.qq.qcloud", "com.microsoft.msa.authenticator", "com.tigervpns.android", "la.droid.qr",
                "com.flyingottersoftware.mega", "com.pushbullet.android", "com.metago.astro", "com.anydo.cal", "com.nhn.android.ndrive", "com.ifttt.ifttt",
                "com.speedsoftware.explorer", "at.nk.tools.iTranslate", "uk.co.nickfines.RealCalc", "com.Kingdee.Express", "com.trello",
                "am.sunrise.android.calendar", "com.rockcatstudio", "org.dayup.gtask", "com.google.android.apps.giant", "com.nhn.android.calendar",
                "la.droid.qr.priva", "mobi.infolife.taskmanager", "imoblife.toolbox.full", "com.estmob.android.sendanywhere", "com.advancedprocessmanager",
                "com.github.mobile", "com.rechild.advancedtaskkiller", "com.acompli.acompli", "com.google.android.apps.enterprise.dmagent", "com.netease.mail",
                "com.ylmf.androidclient", "com.qihoo.msafe", "com.tsf.shell.widget.alarm"
        };

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
        String[] topFreeList = IOUtil.findAllPackagesInCategoryContains("top free", Constants.CacheDir, new IntervalList());
        if (topFreeList != null) {
            for (String topItem : topFreeList)
                System.out.println("top free " + topItem);
        }
        String[] topPaidList = IOUtil.findAllPackagesInCategoryContains("top paid", Constants.CacheDir, new IntervalList());
        if (topPaidList != null) {
            for (String topItem : topPaidList)
                System.out.println("top paid " + topItem);
        }

        String[] mostPopular = IOUtil.findAllPackagesInCategoryContains("popular apps", Constants.CacheDir, new IntervalList());
        if (mostPopular != null) {
            for (String topItem : mostPopular)
                System.out.println("Most popular " + topItem);
        }

        String[] productivityList = IOUtil.findAllPackagesInCategoryContains("productivity", Constants.CacheDir, new IntervalList());
        if (productivityList != null) {
            for (String topItem : productivityList)
                System.out.println("productivity " + topItem);
        }

        int common = pu.compare(productivityList, productivityApps, true);
        System.out.println(String.format("common %d ", common));

        int common_2 = pu.compare(productivityList, productivityApps, false);
        System.out.println(String.format("common %d ", common_2));
    }
}
