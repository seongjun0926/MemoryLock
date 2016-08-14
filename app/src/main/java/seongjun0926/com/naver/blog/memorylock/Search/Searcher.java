package seongjun0926.com.naver.blog.memorylock.Search;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

    SearchTask searchTask;
    OnFinishSearchListener onFinishSearchListener;

    public void Start_Searcher(String url, OnFinishSearchListener onFinishSearchListener){
        this.onFinishSearchListener = onFinishSearchListener;

        searchTask = new SearchTask();
        searchTask.execute(url);
    }

    public class SearchTask extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... urls) {
            String url = urls[0];
            String json = fetchData(url);
            List<Item> itemList = parse(json);
            if (onFinishSearchListener != null) {
                if (itemList == null) {
                    onFinishSearchListener.onFail();
                } else {
                    onFinishSearchListener.onSuccess(itemList);
                }
            }
            return null;
        }
    }

    private String fetchData(String url_) {
        StringBuilder jsonHtml = new StringBuilder();
        try {
            // 연결 url 설정
            URL url = new URL(url_);
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
            return jsonHtml.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private List<Item> parse(String jsonString) {
        List<Item> itemList = new ArrayList<Item>();
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONArray objects = root.getJSONArray("results");

            for (int i = 0; i < objects.length(); i++) {
                JSONObject object = objects.getJSONObject(i);
                String Check = object.getString("Check");//Json에서 Check라는 키의 값을 가져옴

                if (Check.equals("succed")) {

                    Item item = new Item();
                    Log.i("test", "item : " + item);
                    item.num=object.getString("M_C_Num");
                    item.Contents_image = object.getString("M_C_Contents");
                    item.Contents_text = object.getString("M_C_Text");
                    item.lat = object.getString("M_C_lat");
                    item.lng = object.getString("M_C_lng");
                    item.time = object.getString("M_C_Time");
                    item.type = object.getString("M_C_Type");
                    item.Current_Time=object.getString("Current_Time");
                    item.Open_time=object.getString("M_C_OpenTime");
                    item.Header=object.getString("M_C_Header");
                    itemList.add(item);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return itemList;
    }


}