package seongjun0926.com.naver.blog.memorylock;

/**
 * Created by juny on 2016-07-15.
 */

public class ListItem {

    private String[] mData;

    public ListItem(String[] data ){


        mData = data;
    }

    public ListItem(String Check,String Question){

        mData = new String[2];
        mData[0] = Check;
        mData[1] = Question;


    }

    public String[] getData(){
        return mData;
    }

    public String getData(int index){
        return mData[index];
    }

    public void setData(String[] data){
        mData = data;
    }



}
