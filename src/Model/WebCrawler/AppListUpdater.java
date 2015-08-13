package Model.WebCrawler;

import Model.App;
import Model.Category;
import Model.Filter;
import Utils.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;

/**
 * Created by v-yuliwa on 8/13/2015.
 */
public class AppListUpdater {

    public void getAppList(String targetCagorit) {
        GooglePlaySpider spider = new GooglePlaySpider(new AppListFilter());
        Category[] available = spider.getCategory();
        for (Category c : available) {
            //if(c.title.toLowerCase().contains()
        }
    }
}

class AppListFilter implements Filter {
    int m_curAppNum = 0;
    int m_maxAppNum = 100;

    @Override
    public App[] merge(App[] oldList, App[] newList) {
        if (m_curAppNum >= m_maxAppNum)
            return oldList;
        HashMap<String, Integer> found = new HashMap<String, Integer>();
        ArrayList<App> result = new ArrayList<App>();
        int m = oldList.length;
        int n = newList.length;
        int p = 0;
        int q = 0;
        Boolean flag = true;
        while (p < m && q < n && m_curAppNum <= m_maxAppNum) {
            if (flag) {
                if (found.get(oldList[p].packageName) != 1) {
                    found.put(oldList[p].packageName, 1);
                    result.add(oldList[p]);
                    m_curAppNum += 1;
                }
                p++;
            } else {
                if (found.get(newList[q].packageName) != 1) {
                    found.put(newList[q].packageName, 1);
                    result.add(newList[q]);
                    m_curAppNum += 1;
                }
                q++;
            }
            flag = !flag;
        }
        while (p < m && m_curAppNum <= m_maxAppNum) {
            if (found.get(oldList[p].packageName) != 1) {
                found.put(oldList[p].packageName, 1);
                result.add(oldList[p]);
                m_curAppNum += 1;
            }
            p++;
        }
        while ( q< m && m_curAppNum <= m_maxAppNum) {
            if (found.get(newList[p].packageName) != 1) {
                found.put(newList[p].packageName, 1);
                result.add(newList[p]);
                m_curAppNum += 1;
            }
            p++;
        }
        App[] appList=new App[result.size()];
        result.toArray(appList);
        return appList;
    }
}
