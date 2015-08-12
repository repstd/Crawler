package Model.WebCrawler; /**
 * Created by yulw on 8/11/2015.
 */

import Model.App;
import Model.Category;
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

    @Override
    public void initConnection(String url, HashMap<String, String> paras) {
        m_url = url;
        try {
            /*
            //try to update the page sources using HtmlUnitDriver, but doesn't work
            WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
            java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
            WebRequest request = new WebRequest(new URL(url));
            request.setHttpMethod(HttpMethod.GET);
            ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : paras.entrySet()) {
                parameters.add(new NameValuePair(entry.getKey(), entry.getValue()));
            }
            request.setRequestParameters(parameters);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getCurrentWindow().setInnerHeight(6000);

            HtmlPage html_src = webClient.getPage(request);
            ScriptResult sr = html_src.executeJavaScript("window.scrollBy(0,6000)");
            webClient.loadDownloadedResponses();
            webClient.waitForBackgroundJavaScript(10000);
            html_src = (HtmlPage) sr.getNewPage();
            root = Jsoup.parse(html_src.asXml());
            webClient.close();
            */
            if (paras != null)
                root = Jsoup.connect(url).data(paras).timeout(0).followRedirects(true).get();
            else
                root = Jsoup.connect(url).timeout(0).followRedirects(true).get();
            Thread.sleep(5000);
        } catch (Exception e) {
            failedConnection.add(url);
            root = null;
            e.printStackTrace();
        }
    }

    @Override
    public Category[] getCategory() {
        if (root == null)
            return new Category[0];
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
    public void parseCategory(Category category) {
        String fullCategoryUrl = Constants.GooglePlay + category.href;
        HashMap<String, String> paras = new HashMap<String, String>();
        initConnection(fullCategoryUrl, paras);
        if (root == null)
            return;
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
        ArrayList<App> apps = new ArrayList<App>();
        for (Map.Entry<String, String> clusters : seemoreInfo.entrySet()) {
            parseAppList(category, clusters.getKey(), clusters.getValue());
        }
    }

    private App[] parseAppList(Category category, String cluster, String url) {
        String detailUrl = Constants.GooglePlay + url;
        int startIndex = 0;
        int numToGet = 100;
        boolean allLoaded = false;
        HashMap<String, App> appList = new HashMap<String, App>();
        try {
            while (!allLoaded) {
                //update the app list
                updateAppList(startIndex, numToGet, detailUrl);
                Elements newAppLists = root.getElementsByAttributeValue(Constants.AttrClass, Constants.GooglePlayAppCardClass);
                int size = newAppLists.size();
                if (size == 0)
                    break;
                for (Element appElement : newAppLists) {
                    String packageName = appElement.attr(Constants.GooglePlayAppCardAttrId);
                    String name = appElement.getElementsByAttributeValue(Constants.AttrClass, Constants.GooglePlayAppCardTitleClass).first().attr(Constants.AttrTitle);
                    String rating="unavailable";
                    Elements ratingElement= appElement.getElementsByAttributeValue(Constants.AttrClass, Constants.GooglePlayAppCardContentStarRatingClass);
                    if(ratingElement.size()!=0)
                        rating=ratingElement.first().attr(Constants.AttrAriaLable);
                    String description=appElement.getElementsByAttributeValue(Constants.AttrClass,Constants.GooglePlayAppCardContentDescriptionClass).text();
                    //System.out.println(String.format("[%s][%s]", title, packageName));
                    if (appList.get(name) != null) {
                        allLoaded = true;
                        System.out.println(String.format("[%s]:[%s]:[%s]", category.title, cluster, url));
                        System.out.println("duplicate: ");
                        break;
                    }
                    App app = new App();
                    app.cluster = cluster;
                    app.name = name;
                    app.packageName = packageName;
                    app.rank = Integer.toString(appList.size());
                    app.rating = rating;
                    app.description =description;
                    //System.out.println(app);
                    appList.put(name, app);
                }
                startIndex += size;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Total found: " + Integer.toString(appList.size()));
        App[] result = new App[appList.size()];
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

    @Override
    public void saveState(Category category, App[] apps) {
        //TODO: consider the condition that the task is interrupted
        IOUtil.writeCategory(category, apps);
    }

    @Override
    public void loadState(Category category) {
        App[] apps=IOUtil.loadCategory(category);
    }
}
