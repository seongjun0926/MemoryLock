package seongjun0926.com.naver.blog.memorylock.TimeCapsule;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import seongjun0926.com.naver.blog.memorylock.List.ListViewAdapter;
import seongjun0926.com.naver.blog.memorylock.R;
import seongjun0926.com.naver.blog.memorylock.Search.Item;
import seongjun0926.com.naver.blog.memorylock.Search.OnFinishSearchListener;
import seongjun0926.com.naver.blog.memorylock.Search.Searcher;

public class Show_Time_Activity extends AppCompatActivity{

    Delete_Contents DC_task;
    String E_mail;
    SharedPreferences setting;
    ListViewAdapter adapter;

    List<Item> itemList1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.show_time_activity);
        Log.i("test", "Show_Lock_Activity");

        final ListView listview;

        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.List_View);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                Item item=itemList1.get(i);
                final String Delete_Num=item.num;
                //String a=(String)adapterView.getItemAtPosition(i);//null

                //String a=(String)adapterView.getAdapter().getItem(i);//null

                //String a=adapter.getItem(i).toString();//null

                Log.i("test","a : "+Delete_Num);

                AlertDialog.Builder alert=new AlertDialog.Builder(view.getContext());
                alert.setTitle("확인");
                alert.setMessage("삭제 하시겠습니까?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        DC_task = new Delete_Contents();
                        DC_task.execute("http://cometocu.com/MemoryLock/Delete_Contents.jsp?M_C_Num="+Delete_Num);
                    }
                });
                alert.show();

                return false;
            }
        });

        setting = getSharedPreferences("setting", 0);
        E_mail = setting.getString("ID", "");

        Searcher searcher = new Searcher();
        searcher.Start_Searcher("http://seongjun0926.cafe24.com/MemoryLock/Search_Create_Detail.jsp?E_Mail=" + E_mail+"&M_C_Type=2", new OnFinishSearchListener() {

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
        itemList1=itemList;
        Log.i("test1", "showResult()");
        Log.i("test1", "itemList size: " + itemList.size());


        for (int i = 0; i < itemList.size(); i++) {
            final Item item = itemList.get(i);
            Log.i("test1", "itemList size: " + itemList.size());

            final String Image = item.Contents_image;
            final String Context = item.Contents_text;
            final String Time = item.time;
            final String Open_Time=item.Open_time;
            final String Current_Time=item.Current_Time;
            final String Header=item.Header;

            creaeteDrawableFromUrl cdf = new creaeteDrawableFromUrl();
            cdf.execute(Image,Context,Time,Open_Time,Current_Time,Header);

        }

    }


    class creaeteDrawableFromUrl extends AsyncTask<String, Void, Drawable> {
        String text;
        String time;
        String OpenTime;
        String CurrentTime;
        String url;
        String header;
        @Override
        protected Drawable doInBackground(String... strings) {
            OpenTime=strings[3];
            CurrentTime=strings[4];

            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-M-dd");

            try {
                Date OpenTime1=transFormat.parse(OpenTime);
                Date CurrentTime2=transFormat.parse(CurrentTime);



                if(OpenTime1.after(CurrentTime2)){
                    long term = OpenTime1.getTime()-CurrentTime2.getTime();

                    term=term/1000/60/60/24;

                    Log.i("test","term : "+term);

                    text="뭐라고 적었을까?";
                    header="";
                    time="언제 만들었더라?";
                    url = "http://seongjun0926.cafe24.com/MemoryLock/Upload/img/noimage.jpg";
                    try {
                        InputStream is = (InputStream) new URL(url).getContent();
                        Drawable d = Drawable.createFromStream(is, "src");

                        return d;
                    } catch (Exception e) {

                    }

                }else{
                    text = strings[1];
                    time = strings[2];
                    url = strings[0];
                    header=strings[5];
                    try {
                        InputStream is = (InputStream) new URL(url).getContent();
                        Drawable d = Drawable.createFromStream(is, "src");

                        return d;
                    } catch (Exception e) {

                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }




            return null;
        }


        @Override
        protected void onPostExecute(Drawable drawable) {
            adapter.addItem(drawable,header,text ,time);

            adapter.notifyDataSetChanged();
        }
    }


    private class Delete_Contents extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();

        }

        protected void onPostExecute(String str) {

            Toast.makeText(getApplicationContext(),"삭제되었습니다.",Toast.LENGTH_SHORT).show();
            Intent Show_Lock_Activity = new Intent(Show_Time_Activity.this, Show_Time_Activity.class);
            startActivity(Show_Lock_Activity);
            finish();


        }


    }

}




