package Utils;

import Model.App;
import Model.AppListMerger;
import Model.Category;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yulw on 8/12/2015.
 */
//some tools to read and write app information
public class IOUtil {
    static String getFileName(Category category, String cluster) {
        String name = category.title;
        if (cluster != null)
            name += "-" + cluster.toLowerCase();
        return name;
    }

    public static void writeCategory(Category category, App[] apps) {
        writeCategory(category, null, apps);
    }

    public static void writeCategory(Category category, String cluster, App[] apps) {
        try {
            File path = new File(Constants.ResultDir);
            if (!path.exists())
                path.mkdir();
            //File outFile = new File(path, getFileName(category, cluster));
            writeCategory(path + "/" + getFileName(category, cluster), apps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeCategory(String fileName, App[] apps) {
        try {
            File outFile = new File(fileName);
            FileOutputStream fileOut = new FileOutputStream(outFile);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            if (apps != null) {
                for (App app : apps) {
                    System.out.println("write::" + app);
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
        return loadCategory(category, null);
    }

    public static App[] loadCategory(Category category, String cluster) {
        File path = new File(Constants.ResultDir);
        if (!path.exists())
            return new App[0];
        return loadCategory(path + "/" + getFileName(category, cluster));
    }

    public static App[] loadCategory(String fileName) {
        List<App> applist = new ArrayList<App>();
        try {
            File inFile = new File(fileName);
            if (!inFile.exists())
                return new App[0];
            System.out.println(inFile.getAbsolutePath());
            FileInputStream fileIn = new FileInputStream(inFile);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object temp = objectIn.readObject();
            while (fileIn.available() > 0) {
                System.out.println("loading.." + temp);
                applist.add((App) temp);
                temp = objectIn.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        App[] result = new App[applist.size()];
        applist.toArray(result);
        return result;
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

    //if all of the
    public static String[] findAllPackagesInCategoryContains(String keywords, String resultPath, AppListMerger merger) {
        App[] result = new App[0];
        try {
            File path = new File(resultPath);
            if (!path.exists() || !path.isDirectory())
                return null;
            if (merger == null)
                return null;
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.getName().toLowerCase().contains(keywords)) {
                    App[] newList = loadCategory(file.getAbsolutePath());
                    result = merger.merge(result, newList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getPackagesName(result);
    }
}
