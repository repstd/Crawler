import Model.App;
import Model.AppListMerger;
import Model.Category;
import Model.WebCrawler.GooglePlaySpider;
import Utils.Constants;
import Utils.IOUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by yulw on 8/13/2015.
 */
public class PackageUpdater {
    ArrayList<Category> availableCategoriesList = new ArrayList<Category>();

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

        Category mostPopular = new Category();
        mostPopular.title = "popular_apps";
        mostPopular.href = "/store";
        mostPopular.parent = "";
        availableCategoriesList.add(mostPopular);
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

    void updateList(String inFilePath, String outFilePath) {
        try {
            //update top separately
            getPackageNameList("top");
            InputStream fis = new FileInputStream(inFilePath);
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            OutputStream fout = new FileOutputStream(outFilePath);
            OutputStreamWriter osr = new OutputStreamWriter(fout, Charset.forName("UTF-8"));
            BufferedWriter bw = new BufferedWriter(osr);
            HashMap<String, String> remainingItems = new HashMap<String, String>();
            String line;
            String[] packageList;
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(" ");
                System.out.println("readLine; " + line);
                if (temp.length != 2)
                    break;
                packageList = getPackageNameList(temp[0]);
                if (packageList != null) {
                    writeToStringArray(bw, packageList, temp[1]);
                } else {
                    //read from local file then
                    remainingItems.put(temp[0], temp[1]);
                }
            }
            for (Map.Entry<String, String> entry : remainingItems.entrySet()) {
                packageList = IOUtil.findAllPackagesInCategoryContains(entry.getKey(), Constants.CacheDir, new IntervalList());
                if (packageList != null) {
                    writeToStringArray(bw, packageList, entry.getValue());
                }
            }
            bw.close();
            osr.close();
            fout.close();
            br.close();
            isr.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToStringArray(BufferedWriter bw, String[] packageList, String arrayName) {
        try {
            bw.write("private String[] " + arrayName + " = {");
            bw.write("\n\t");
            for (int i = 0; i < packageList.length; i++) {
                if (i != packageList.length - 1)
                    bw.write("\"" + packageList[i] + "\",");
                else
                    bw.write("\"" + packageList[i] + "\"};");
                if (i % 5 == 0 && i != 0) {
                    bw.write("\n\t");
                }
            }
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String[] getPackageNameList(String keyword) {
        String[] packageLists = null;
        for (Category category : availableCategoriesList) {
            if (category.title.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.print(category.title);
                App[] apps = spider.parseCategory(category);
                //get package string[]
                packageLists = IOUtil.getPackagesName(apps);
                break;
            }
        }
        return packageLists;
    }

    int compare(String[] la, String[] lb, boolean isNeedConsiderOrder) {
        int common = 0;
        if (!isNeedConsiderOrder) {
            HashSet<String> lbSet = new HashSet<String>();
            lbSet.addAll(Arrays.asList(lb));
            for (String item : la) {
                if (lbSet.contains(item))
                    common += 1;
            }
        } else {
            int m = Math.min(la.length, lb.length);
            for (int i = 0; i < m; i++) {
                if (la[i].equalsIgnoreCase(lb[i]))
                    common++;
            }
        }
        return common;
    }
}
