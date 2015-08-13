package Utils;

import Model.App;
import Model.Category;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yulw on 8/12/2015.
 */
//some tools to read and write app information
public class IOUtil {
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
                for (App app : apps) {
                    //System.out.println(app);
                    objectOut.writeObject(app);
                }
            }
            objectOut.close();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static App[] loadCategory(Category category) {
        try {
            File path = new File(Constants.ResultDir);
            if (!path.exists())
                return new App[0];
            File inFile = new File(path, category.parent + "_" + category.title);
            if (!inFile.exists())
                return new App[0];
            System.out.println(inFile.getAbsolutePath());
            FileInputStream fileIn = new FileInputStream(inFile);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            List<App> applist = new ArrayList<App>();
            Object temp=objectIn.readObject();
            while (temp!=null) {
                System.out.println(temp);
                applist.add((App)temp);
                if(objectIn.available()!=0)
                    temp=objectIn.readObject();
                else
                    temp=null;
            }
            App[] result = new App[applist.size()];
            applist.toArray(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new App[0];
        }
    }

    public static String[] getPackagesName(App[] applist) {
        try {
            String[] result = new String[applist.length];
            for (int i = 0; i < applist.length; i++)
                result[i] = applist[i].packageName;
            return result;
        } catch (Exception e) {
            return new String[0];
        }
    }
}
