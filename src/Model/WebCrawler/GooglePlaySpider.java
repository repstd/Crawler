package Model.WebCrawler; /**
 * Created by yulw on 8/11/2015.
 */

import Model.App;
import Model.Category;
import Model.AppListMerger;
import Utils.Constants;
import Utils.IOUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class GooglePlaySpider extends BaseSpider {
    Document root = null;
    List<String> failedConnection = new ArrayList<String>();
    boolean isRatingInfoNeeded = false;
    private AppListMerger filterImpl;

    public GooglePlaySpider(AppListMerger filter) {
        filterImpl = filter;
    }

    @Override
    public void initConnection(String url, HashMap<String, String> paras) {
        m_url = url;
        try {
            if (paras == null)
                paras = new HashMap<String, String>();
            paras.put("hl", "en");
            //tried to update the page sources using HtmlUnitDriver, but doesn't work
            System.out.println(url);
            root = Jsoup.connect(url).data(paras).timeout(0).followRedirects(true).get();
        } catch (Exception e) {
            failedConnection.add(url);
            root = null;
            e.printStackTrace();
        }
    }

    @Override
    public Category[] getCategory() {
        initConnection(Constants.GooglePlayApps, null);
        List<Category> parsedResult = new ArrayList<Category>();
        Elements categoryGroup = root.getElementsByClass(Constants.GooglePlayCategoryOuterContainer);
        for (Element groupWrapper : categoryGroup) {
            Elements categoryList = groupWrapper.getElementsByClass(Constants.GooglePlayChildSubmenuWrapper);
            String groupTitle = groupWrapper.getElementsByClass(Constants.GooglePlayCategoryParentSubmenu).attr(Constants.AttrTitle);
            for (Element categoryWrapper : categoryList) {
                try {
                    Elements categoryElement = categoryWrapper.getElementsByClass(Constants.GooglePlayParentSubmenu);
                    if (categoryElement.size() == 0)
                        continue;
                    Element ele = categoryElement.first();
                    Category category = new Category();
                    category.parent = groupTitle;
                    category.title = ele.attr(Constants.AttrTitle);
                    category.href = ele.attr(Constants.AttrHref);
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
    public App[] parseCategory(Category category) {
        String fullCategoryUrl = Constants.GooglePlay + category.href;
        initConnection(fullCategoryUrl, null);
        if (root == null)
            return new App[0];
        HashMap<String, String> seemoreInfo = new HashMap<String, String>();
        //first of all,we get all of the clusters
        try {
            Document bodyDoc = Jsoup.parseBodyFragment(root.body().html());
            Elements clusters = bodyDoc.getElementsByClass(Constants.GooglePlayAppClusterClass);
            for (Element cluster : clusters) {
                Elements seemore = cluster.getElementsByTag(Constants.TagA);
                Elements clusterTitleEle = cluster.getElementsByTag(Constants.TagH2);
                if (seemore.size() == 0)
                    continue;
                String clusterTitle = clusterTitleEle.first().getElementsByTag(Constants.TagA).first().text();
                String href = seemore.first().attr(Constants.AttrClass, Constants.GooglePlayAppClusterSeemoreClass).attr(Constants.AttrHref);
                seemoreInfo.put(clusterTitle, href);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        App[] old = new App[0];
        for (Map.Entry<String, String> clusters : seemoreInfo.entrySet()) {
            App[] newAppList = parseAppList(category, clusters.getKey(), clusters.getValue());
            //save the information
            IOUtil.writeCategory(category, clusters.getKey(), newAppList);
            if (this.filterImpl != null) {
                old = this.filterImpl.merge(old, newAppList);
            }
        }
        return Arrays.copyOf(old, Math.min(old.length, Constants.MaxAppCount));
    }

    private App[] parseAppList(Category category, String cluster, String url) {
        String detailUrl = Constants.GooglePlay + url;
        int startIndex = 0;
        int numToGet = 100;
        boolean allLoaded = false;
        HashMap<String, App> appList = new LinkedHashMap<>();
        float minScore = 6;
        float maxScore = 0;
        try {
            while (!allLoaded && startIndex < Constants.MaxAppCount) {
                //update the app list
                numToGet = Math.min(numToGet, Constants.MaxAppCount - startIndex);
                updateAppList(startIndex, numToGet, detailUrl);
                Elements newAppLists = root.getElementsByAttributeValue(Constants.AttrClass, Constants.GooglePlayAppCardClass);
                int size = newAppLists.size();
                if (size == 0)
                    break;
                for (Element appElement : newAppLists) {
                    String packageName = appElement.attr(Constants.GooglePlayAppCardAttrId);
                    String name = appElement.getElementsByAttributeValue(Constants.AttrClass, Constants.GooglePlayAppCardTitleClass).first().attr(Constants.AttrTitle);

                    String description = appElement.getElementsByAttributeValue(Constants.AttrClass, Constants.GooglePlayAppCardContentDescriptionClass).text();
                    String clickUrl = appElement.getElementsByAttributeValue(Constants.AttrClass, Constants.GooglePlayAppCardContentClickTargetClass).attr(Constants.AttrHref);
                    String ratingValue = "-1";
                    String ratingCount = "-1";
                    if (isRatingInfoNeeded) {
                        initConnection(Constants.GooglePlay + clickUrl, null);
                        ratingValue = root.getElementsByAttributeValue(Constants.AttrItemprop, Constants.GooglePlayAppDetailsRatingValue).attr(Constants.AttrContent);
                        ratingCount = root.getElementsByAttributeValue(Constants.AttrItemprop, Constants.GooglePlayAppDetailsRatingCount).attr(Constants.AttrContent);
                    }
                    //System.out.println(String.format("[%s][%s]", title, packageName));
                    if (appList.get(packageName) != null) {
                        allLoaded = true;
                        break;
                    }
                    App app = new App();
                    app.category = category.title;
                    app.cluster = cluster;
                    app.name = name;
                    app.packageName = packageName;
                    app.rank = appList.size();
                    app.ratingValue = Float.parseFloat(ratingValue);
                    app.normalizedRating = app.ratingValue;
                    app.ratingCount = Integer.parseInt(ratingCount);
                    app.description = description;
                    minScore = Math.min(minScore, app.ratingValue);
                    maxScore = Math.max(maxScore, app.ratingValue);
                    //System.out.println(app);
                    appList.put(packageName, app);
                }
                startIndex += size;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(String.format("[%s]:[%s]:[%s].Total Found: %d", category.title, cluster, url, appList.size()));
        App[] result = new App[appList.size()];
        if (isRatingInfoNeeded) {
            int index = 0;
            //normalize the score
            for (App app : appList.values()) {
                app.normalizedRating = 5 * ((app.ratingValue - minScore) / (maxScore - minScore)) + 0;
                //System.out.println(app + "normalizedRating: " + Float.toString(app.normalizedRating)+Float.toString(minScore)+" "+Float.toString(maxScore));
                result[index++] = app;
            }
        }
        appList.values().toArray(result);
        return result;
    }

    private void updateAppList(int startIndex, int num, String url) {
        HashMap<String, String> paras = new HashMap<String, String>();
        paras.put("start", Integer.toString(startIndex));
        paras.put("num", Integer.toString(num));
        paras.put("numChildren", "0");
        initConnection(url, paras);
    }

    public void setGrabRatingInformation(boolean b) {
        this.isRatingInfoNeeded = b;
    }

    @Override
    public void saveState(Category category, App[] apps) {

    }

    @Override
    public void loadState(Category category) {

    }
}
