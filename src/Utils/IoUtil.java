package Utils;

import Model.App;
import Model.Category;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by yulw on 8/12/2015.
 */
//some tools to read and write app information
public class IoUtil {
    //write a specific
    public static void clearResult() {
        try {
            File path = new File(Constants.ResultDir);
            if (path.exists() && path.isDirectory())
                path.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeCategory(Category category, App[] apps) {
        try {
            File path = new File(Constants.ResultDir);
            if (!path.exists())
                path.mkdir();
            File outFile = new File(path, category.parent + "_" + category.title);
            FileOutputStream fileOut = new FileOutputStream(outFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            if (apps != null) {
                for (App app : apps)
                    objectOut.writeObject(objectOut);
            }
            objectOut.close();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
