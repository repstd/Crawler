package Model;

import java.util.HashMap;

/**
 * Created by v-yuliwa on 8/13/2015.
 */
public interface AppListMerger {
    App[] merge(App[] src1,App[] src2);

    boolean isEnabled();
}
