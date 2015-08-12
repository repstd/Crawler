package Model;

import java.io.Serializable;

/**
 * Created by yulw on 8/12/2015.
 */
public class App implements Serializable
{
    //like top selling, top free.
    public String cluster;

    //App name
    public String name;

    //package name
    public String packageName;

    //TODO:how to rank?
    public String rank;

    //ratings.like 4.5 out of 5
    public String rating;

    public String description;

    @Override
    public String toString() {
        if(cluster.length()!=0)
            return String.format("[Name:%s,PackageName:%s,Rank %s in %s,Rating:%s,Desc:%s]",name,packageName,rank,cluster,rating,description);
        else
            return String.format("[Name:%s,PackageName:%s,Rank %s,Rating:%s,Desc:%s]",name,packageName,rank,rating,description);
    }
}
