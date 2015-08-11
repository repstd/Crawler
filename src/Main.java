import Model.Category;
import Model.WebCrawler.GooglePlaySpider;
import Utils.Constants;
import Utils.IoUtil;

/**
 * Created by yulw on 8/11/2015.
 */
public class Main {
    public static void main(String[] args) {
        GooglePlaySpider spider=new GooglePlaySpider();
        spider.initConnection(Constants.GooglePlayURL);
        Category[] categories=spider.getCategory();
        IoUtil.clearResult();
        for(Category category:categories)
            spider.saveState(category,null);
        return;
    }

}
