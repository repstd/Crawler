package Model;

import java.io.Serializable;

/**
 * Created by yulw on 8/12/2015.
 */
public class Category implements Serializable
{
    public String parent;

    public String title;

    public String href;

    @Override
    public String toString() {
        return String.format("[%s]:[%s]",title,href);
    }
}
