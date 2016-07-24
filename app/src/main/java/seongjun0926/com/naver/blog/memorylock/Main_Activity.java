package seongjun0926.com.naver.blog.memorylock;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main_Activity extends FragmentActivity implements MapView.POIItemEventListener,View.OnClickListener {

    Button Lock_Btn, TimeCapSule_Btn, Shared_Btn;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();

    MapView mapView=null;

    Searcher task;

    private static final MapPoint[] CUSTOM_MARKER_POINT ={ MapPoint.mapPointWithGeoCoord(35.871922,128.623690),MapPoint.mapPointWithGeoCoord(35.871022,128.624690),MapPoint.mapPointWithGeoCoord(35.871122,128.625690),MapPoint.mapPointWithGeoCoord(35.871222,128.626690)};//커스텀 마크 설정하기.




    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            if (poiItem == null) return null;
            Item item = mTagItemMap.get(poiItem.getTag());
            if (item == null) return null;
            ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
            TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
            textViewTitle.setText(item.Contents_text);
            TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
            textViewDesc.setText(item.time);
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);



        Lock_Btn = (Button) findViewById(R.id.Lock_Btn);
        Lock_Btn.setOnClickListener(this);
        TimeCapSule_Btn = (Button) findViewById(R.id.TimeCapSule_Btn);
        TimeCapSule_Btn.setOnClickListener(this);
        Shared_Btn = (Button) findViewById(R.id.Shared_Btn);
        Shared_Btn.setOnClickListener(this);


        //=========================================================================================================//

        //맵뷰
        mapView = new MapView(this);
        mapView.setDaumMapApiKey(MapAPI.MAP_API_KEY_Value);
        mapView.setShowCurrentLocationMarker(true);//현재 위치 보여주는 마커 사용

        mapView.setPOIItemEventListener(this);//마커 사용하기 위한 리스너들



        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        // 구현한 CalloutBalloonAdapter 등록


        //커스텀 마커 생성

        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);

        container.addView(mapView);

        task=new Searcher();
        task.execute("http://seongjun0926.cafe24.com/MemoryLock/Search_Create.jsp");
        //xml에 선언된 map_view 레이아웃을 찾아온 후, 생성한 MapView객체 추가
       /* CountDownTimer CountDown=new CountDownTimer(10000,6000) {
            @Override
            public void onTick(long l) {

                Log.i("test","CountDown");

            }

            @Override
            public void onFinish() {
                start();
            }
        }.start();*/
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.Shared_Btn:
                task=new Searcher();
                task.execute("http://seongjun0926.cafe24.com/MemoryLock/Search_Create.jsp");
            break;
            case R.id.Lock_Btn:
                Intent LockPop_Activity = new Intent(Main_Activity.this, LockPop_Activity.class);
                startActivity(LockPop_Activity);
            break;
            case R.id.TimeCapSule_Btn:
                Intent TimeCapsulePop_Activity = new Intent(Main_Activity.this, TimeCapsulePop_Activity.class);
                startActivity(TimeCapsulePop_Activity);
            break;
        }
    }

    //---------------------------------------------------------------------------------------------------------------------------------------//
    //이상한 함수들...


    /////////////////////////////////////////////////////////////////////////////////////////////////
    // net.daum.mf.map.api.MapView.MapViewEventListener

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }


    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        //클릭했을 때, 만든 날짜 알려줄거임
        Toast.makeText(this, "Clicked " + mapPOIItem.getItemName() + " Callout Balloon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    public class Searcher extends AsyncTask<String, Integer, String> {


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
            List<Item> itemList = new ArrayList<Item>();

            try {
                String Check;
                JSONObject root;

                root = new JSONObject(str);
                Log.i("test", "root=" + root.toString());

                JSONArray ja = root.getJSONArray("results");
                Log.i("test", "ja=" + ja.toString());

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    Check = jo.getString("Check");//Json에서 Check라는 키의 값을 가져옴
                    if (Check.equals("succed")) {
                        Item item=new Item();
                        Log.i("test","item : "+item);
                        item.Contents_image=jo.getString("M_C_Contents");
                        item.Contents_text=jo.getString("M_C_Text");
                        item.lat=jo.getString("M_C_lat");
                        item.lng=jo.getString("M_C_lng");
                        item.time=jo.getString("M_C_Time");
                        item.type=jo.getString("M_C_Type");
                        itemList.add(item);

                        Log.i("test","item.Contents_image : "+item.Contents_image);
                        Log.i("test","item.Contents_text : "+item.Contents_text);
                        Log.i("test","item.lat : "+item.lat);
                        Log.i("test","item.lng : "+item.lng);
                        Log.i("test","item.time : "+item.time);
                        Log.i("test","item.type : "+item.type);
                        Log.i("test","itemList : "+itemList);

                    } else {
                    }
                    ShowResult(itemList);

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }


        }


    }
    private void ShowResult(List<Item> itemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            Log.i("test","itemList size: "+ itemList.size());
            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.Contents_text);
            poiItem.setTag(i);

            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.valueOf(item.lat).doubleValue(), Double.valueOf(item.lng).doubleValue());
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.custom_marker_red);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.custom_marker_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
        }

        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            mapView.selectPOIItem(poiItems[0], false);
        }
    }


}
