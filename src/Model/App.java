package Model;

import java.io.Serializable;

/**
 * Created by yulw on 8/12/2015.
 */
public class App implements Serializable
{
    public String category;

    //like top selling, top free.
    public String cluster;

    //App name
    public String name;

    //package name
    public String packageName;

    public int rank;

    //normalize the score to [0,5.0]
    public float normalizedRating;

    //ratings.like 4.5 out of 5
    public float ratingValue;

    public int ratingCount;

    public String description;

    @Override
    public int hashCode() {
        return (int) ratingValue *packageName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this==obj)
            return true;
        if(obj==null)
            return false;
        if(this.getClass()!=obj.getClass())
            return false;
        App other=(App)obj;
        return this.name.equals(other.name)&&this.packageName.equals(other.packageName);
    }

    @Override
    public String toString() {
        if(cluster.length()!=0)
            return String.format("[Name:%s,PackageName:%s,Rank %d in %s,Rating:%f,Desc:%s]",name,packageName,rank,cluster, ratingValue,description);
        else
            return String.format("[Name:%s,PackageName:%s,Rank %d,Rating:%f,Desc:%s]",name,packageName,rank, ratingValue,description);
    }
}
