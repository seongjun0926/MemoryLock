package seongjun0926.com.naver.blog.memorylock.List;

import android.graphics.drawable.Drawable;

/**
 * Created by juny on 2016-07-27.
 */
public class ListViewItem {
    private Drawable Image;
    private String Context_str;
    private String Time_str;


    public void setImage(Drawable image){
        Image=image;
    }

    public void setContext_str(String context_str) {
        Context_str = context_str;
    }
    public void setTime_str(String time_str){
        Time_str=time_str;
    }
    public Drawable getImage(){
        return this.Image;
    }
    public String getContext_str(){
        return this.Context_str;
    }
    public String getTime_str(){
        return this.Time_str;
    }


}
