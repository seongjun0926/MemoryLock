package seongjun0926.com.naver.blog.memorylock.Search;

/**
 * Created by juny on 2016-07-26.
 */
import java.util.List;

public interface OnFinishSearchListener {
    public void onSuccess(List<Item> itemList);
    public void onFail();
}
