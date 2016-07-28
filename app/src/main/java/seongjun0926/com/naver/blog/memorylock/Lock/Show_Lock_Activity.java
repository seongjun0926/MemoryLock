package seongjun0926.com.naver.blog.memorylock.Lock;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import seongjun0926.com.naver.blog.memorylock.ListViewAdapter;
import seongjun0926.com.naver.blog.memorylock.R;
import seongjun0926.com.naver.blog.memorylock.Search.Item;
import seongjun0926.com.naver.blog.memorylock.Search.OnFinishSearchListener;
import seongjun0926.com.naver.blog.memorylock.Search.Searcher;

public class Show_Lock_Activity extends AppCompatActivity {


    String E_mail;
    SharedPreferences setting;

    ListViewAdapter adapter;

    ArrayList<HashMap<String, String>> personList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_lock_activity);
        Log.i("test", "Show_Lock_Activity");

        ListView listview;

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.List_View);
        listview.setAdapter(adapter);


        setting = getSharedPreferences("setting", 0);
        E_mail = setting.getString("ID", "");

        Searcher searcher = new Searcher();
        searcher.Start_Searcher("http://seongjun0926.cafe24.com/MemoryLock/Search_Create.jsp?E_Mail=" + E_mail, new OnFinishSearchListener() {

            @Override
            public void onSuccess(List<Item> itemList) {
                showResult(itemList); // 검색 결과 보여줌

            }

            @Override
            public void onFail() {

            }
        });


    }

    /*여기있어야함*/
    private void showResult(List<Item> itemList) {
        Log.i("test1", "showResult()");
        Log.i("test1", "itemList size: " + itemList.size());


        for (int i = 0; i < itemList.size(); i++) {
            final Item item = itemList.get(i);
            Log.i("test1", "itemList size: " + itemList.size());

            final String Image = item.Contents_image;
            final String Context = item.Contents_text;
            final String Time = item.time;

            creaeteDrawableFromUrl cdf = new creaeteDrawableFromUrl();
            cdf.execute(Image,Context,Time);

        }

    }

    class creaeteDrawableFromUrl extends AsyncTask<String, Void, Drawable> {
        String text;
        String time;
        @Override
        protected Drawable doInBackground(String... strings) {
            String url = strings[0];
            text = strings[1];
            time = strings[2];
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                Drawable d = Drawable.createFromStream(is, "src");

                return d;
            } catch (Exception e) {

            }

            return null;
        }


        @Override
        protected void onPostExecute(Drawable drawable) {
            adapter.addItem(drawable,text ,time);
            adapter.notifyDataSetChanged();
        }
    }


    private Drawable createDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");

            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object fetch(String address) throws MalformedURLException, IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }


}




