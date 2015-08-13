import Model.App;
import Model.AppListMerger;
import Utils.Constants;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by v-yuliwa on 8/13/2015.
 */
public class RankList implements AppListMerger {
    @Override
    public App[] merge(App[] oldList, App[] newList) {
        Arrays.sort(newList,new AppComparator());
        HashMap<String, Integer> found = new HashMap<String, Integer>();
        ArrayList<App> result = new ArrayList<App>();
        int m = oldList.length;
        int n = newList.length;
        int p = 0;
        int q = 0;
        Boolean flag = true;
        while (p < m && q < n && result.size()< Constants.MaxAppCount) {
            if (oldList[p].rating < newList[q].rating) {
                if (found.get(oldList[p].packageName) == null) {
                    found.put(oldList[p].packageName, 1);
                    result.add(oldList[p]);
                }
                p++;

            } else {
                if (found.get(newList[q].packageName) == null) {
                    found.put(newList[q].packageName, 1);
                    result.add(newList[q]);
                }
                q++;
            }
        }
        while (p < m && result.size()< Constants.MaxAppCount) {
            if (found.get(oldList[p].packageName) == null) {
                found.put(oldList[p].packageName, 1);
                result.add(oldList[p]);
            }
            p++;
        }
        while (q < n && result.size()< Constants.MaxAppCount) {
            if (found.get(newList[q].packageName) == null) {
                found.put(newList[q].packageName, 1);
                result.add(newList[q]);
            }
            q++;
        }
        App[] appList = new App[result.size()];
        result.toArray(appList);
        return appList;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

class AppComparator implements Comparator<App> {
    @Override
    public int compare(App o1, App o2) {
        if (o1.normalizedRating*o1.ratingCount<o2.normalizedRating*o2.ratingCount)
            return 1;
        else if (Math.abs(o1.normalizedRating*o1.ratingCount - o2.normalizedRating*o2.ratingCount) < 1e-5)
            return 0;
        else
            return -1;
    }
}

