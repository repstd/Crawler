import Model.App;
import Model.AppListMerger;
import Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by v-yuliwa on 8/13/2015.
 */
class IntervalList implements AppListMerger {
    @Override
    public App[] merge(App[] oldList, App[] newList) {
        HashMap<String, Integer> found = new HashMap<String, Integer>();
        ArrayList<App> result = new ArrayList<App>();
        int m = oldList.length;
        int n = newList.length;
        int p = 0;
        int q = 0;
        System.out.println(String.format("%d %d",m,n));
        Boolean flag = true;
        while (p < m && q < n && result.size()< Constants.MaxAppCount) {
            if (flag) {
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
            flag = !flag;
        }
        while (p < m && result.size()< Constants.MaxAppCount) {
            if (found.get(oldList[p].packageName)== null) {
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
    public boolean isEnabled() {
        return true;
    }
}
