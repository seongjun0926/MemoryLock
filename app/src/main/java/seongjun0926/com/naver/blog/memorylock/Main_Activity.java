package seongjun0926.com.naver.blog.memorylock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import seongjun0926.com.naver.blog.memorylock.BLE.FindDeviceActivity;
import seongjun0926.com.naver.blog.memorylock.Search.Item;
import seongjun0926.com.naver.blog.memorylock.Search.OnFinishSearchListener;
import seongjun0926.com.naver.blog.memorylock.Search.Searcher;

public class Main_Activity extends FragmentActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener, View.OnClickListener {

    String E_mail;
    Button Lock_Btn, TimeCapSule_Btn, Bluetooth_Btn;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();
    SharedPreferences setting;
    MapView mapView = null;

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 없애기
        setting = getSharedPreferences("setting", 0);
        E_mail = setting.getString("ID", ""); //프리퍼런스에 저장된 값을 가져오기 위함

        Lock_Btn = (Button) findViewById(R.id.Lock_Btn);
        Lock_Btn.setOnClickListener(this);
        TimeCapSule_Btn = (Button) findViewById(R.id.TimeCapSule_Btn);
        TimeCapSule_Btn.setOnClickListener(this);

        Bluetooth_Btn=(Button)findViewById(R.id.Bluetooth_Btn);
        Bluetooth_Btn.setOnClickListener(this);



        //=========================================================================================================//

        //맵뷰
        mapView = new MapView(this);
        mapView.setDaumMapApiKey(MapAPI.MAP_API_KEY_Value);
        mapView.setShowCurrentLocationMarker(true);//현재 위치 보여주는 마커 사용
        mapView.setPOIItemEventListener(this);//마커 사용하기 위한 리스너들
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        // 구현한 CalloutBalloonAdapter 등록
        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);
        container.addView(mapView);


        Searcher searcher = new Searcher();
        searcher.Start_Searcher("http://seongjun0926.cafe24.com/MemoryLock/Search_Create.jsp?E_Mail=" + E_mail, new OnFinishSearchListener() {
            //db에 저장되어있는 값들을 가져옴 겟방식으로 연결해서 지도에 커스텀 마커 띄워주기위함.
            @Override
            public void onSuccess(List<Item> itemList) {
                showResult(itemList); // 검색 결과 보여줌

            }

            @Override
            public void onFail() {
                Toast.makeText(getApplicationContext(), "통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Lock_Btn:
                Intent LockPop_Activity = new Intent(Main_Activity.this, seongjun0926.com.naver.blog.memorylock.Lock.LockPop_Activity.class);

                startActivity(LockPop_Activity);
                break;
            case R.id.TimeCapSule_Btn:
                Intent TimeCapsulePop_Activity = new Intent(Main_Activity.this, seongjun0926.com.naver.blog.memorylock.TimeCapsule.TimeCapsulePop_Activity.class);
                startActivity(TimeCapsulePop_Activity);
                break;
            case R.id.Bluetooth_Btn:
                Intent _i = null;
                _i = new Intent(Main_Activity.this, FindDeviceActivity.class );
                startActivity(_i);
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

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        //커스텀 마커 터치하면 만들어진 시간 알려줌
        Item item = mTagItemMap.get(mapPOIItem.getTag());
        StringBuilder sb = new StringBuilder();
        sb.append(item.time);

        String Type = item.type;


        if (Type.equals("2")) {
            String OpenTime = item.Open_time;
            String CurrentTime = item.Current_Time;

            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-M-dd");
            try {
                Date OpenTime1=transFormat.parse(OpenTime);
                Date CurrentTime2=transFormat.parse(CurrentTime);

                if(OpenTime1.after(CurrentTime2)){
                    long term = OpenTime1.getTime()-CurrentTime2.getTime();

                    term=term/1000/60/60/24;

                    String sentence=" "+String.valueOf(term)+" 일 남았습니다.";

                    Toast.makeText(this, sentence , Toast.LENGTH_SHORT).show();

                }else{

                    Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {

            Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    public void onMapViewInitialized(MapView mapView) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);//현재 위치 가지고 오기 위한 메솓,ㅊ

//메인과 같이 선언되어있어야 시작이 되는듯
        Searcher searcher = new Searcher();
        searcher.Start_Searcher("http://seongjun0926.cafe24.com/MemoryLock/Search_Create.jsp", new OnFinishSearchListener() {
            @Override
            public void onSuccess(final List<Item> itemList) {
                showResult(itemList);
            }

            @Override
            public void onFail() {


            }
        });
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }


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

            String Type = item.type;

            String Contents_header="";
            String Contents_text="";
            String Contents_url="";
            if (Type.equals("2")) {
                String OpenTime = item.Open_time;
                String CurrentTime = item.Current_Time;

                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-M-dd");
                try {
                    Date Date1=transFormat.parse(OpenTime);
                    Date Date2=transFormat.parse(CurrentTime);

                    if(Date1.after(Date2)){
                        Contents_header="과연";
                        Contents_text="뭐라고 적었을까?";
                        Contents_url="http://seongjun0926.cafe24.com/MemoryLock/Upload/img/noimage.jpg";

                    }else{
                        Contents_header=item.Header;
                        Contents_text=item.Contents_text;
                        Contents_url=item.Contents_image;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Contents_text=item.Contents_text;
                Contents_header=item.Header;
                Contents_url=item.Contents_image;
            }



            ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
            TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
            textViewTitle.setText(Contents_header);
            TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
            textViewDesc.setText(Contents_text);

            imageViewBadge.setImageDrawable(createDrawableFromUrl(Contents_url));
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    /*여기있어야함*/
    private void showResult(List<Item> itemList) {
        Log.i("test", "showResult()");
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            int Marker;
            String Type = item.type;
            if (Type.equals("2")) {
                Marker = R.drawable.time;

            } else {
                Marker = R.drawable.lock;

            }



            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.Contents_text);
            poiItem.setTag(i);

            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.valueOf(item.lat).doubleValue(), Double.valueOf(item.lng).doubleValue());
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(Marker);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(Marker);
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
