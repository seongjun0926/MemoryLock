package seongjun0926.com.naver.blog.memorylock;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class Main_Activity extends FragmentActivity implements MapView.POIItemEventListener {

    Button Lock_Btn, TimeCapsule_Btn, Shared_Btn;
    CountDownTimer CountDown=null;
    MapView mapView=null;
    MapPOIItem mCustomMarker;


    private static final MapPoint CUSTOM_MARKER_POINT = MapPoint.mapPointWithGeoCoord(35.871922,128.623690);//커스텀 마크 설정하기.



    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageResource(R.drawable.custom_marker_red);//마커 이미지
            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());//마커 제목
            ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText("Custom CalloutBalloon");//마커 네용
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
        TimeCapsule_Btn = (Button) findViewById(R.id.TimeCapSule_Btn);
        Shared_Btn = (Button) findViewById(R.id.Shared_Btn);

        //--------------------------------------------------------------------------------------------------------------------------//
        //공유 버튼 눌렀을 때
        Shared_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

//--------------------------------------------------------------------------------------------------------------------------//
        //자물쇠 버튼 눌렀을 때
        Lock_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LockPop_Activity = new Intent(Main_Activity.this, LockPop_Activity.class);
                startActivity(LockPop_Activity);

            }
        });
//--------------------------------------------------------------------------------------------------------------------------//
        //타임캡슐 버튼 눌렀을 때
        TimeCapsule_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent TimeCapsulePop_Activity = new Intent(Main_Activity.this, TimeCapsulePop_Activity.class);
                startActivity(TimeCapsulePop_Activity);
            }
        });

        //=========================================================================================================//

        //맵뷰
        mapView = new MapView(this);
        mapView.setDaumMapApiKey("14f0409b73f10d9a817b7968ca7c9a19");
        mapView.setMapType(MapView.MapType.Standard); // 맵 타입을 표준 타입으로 변경
        mapView.setShowCurrentLocationMarker(true);//현재 위치 보여주는 마커 사용

        mapView.setPOIItemEventListener(this);//마커 사용하기 위한 리스너들



        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        // 구현한 CalloutBalloonAdapter 등록
        createCustomMarker(mapView);
        //커스텀 마커 생성

        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);

        container.addView(mapView);
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

    private void createCustomMarker(MapView mapView) {
        mCustomMarker = new MapPOIItem();
        String name = "Custom Marker";//마커 이름 지정
        mCustomMarker.setItemName(name);
        mCustomMarker.setTag(1);//Item 객체에 임의의 정수값(tag)을 지정할 수 있다. 식별자 Id
        mCustomMarker.setMapPoint(CUSTOM_MARKER_POINT);// Item의 지도상 좌표를 설정한다.

        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);//Item 아이콘(마커) 타입을 설정한다.

        mCustomMarker.setCustomImageResourceId(R.drawable.custom_marker_red);//사용자 지정 마커 ex)자물쇠, 타임캡슐
        mCustomMarker.setCustomImageAutoscale(false);
        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);

        mapView.addPOIItem(mCustomMarker);//지도화면에 POI Item 아이콘(마커)를 추가한다.
        mapView.selectPOIItem(mCustomMarker, true);//특정 POI Item 을 선택한다.
        mapView.setMapCenterPoint(CUSTOM_MARKER_POINT, false);//지도 화면의 중심점을 설정한다.

    }


    //---------------------------------------------------------------------------------------------------------------------------------------//
    //이상한 함수들...


    /////////////////////////////////////////////////////////////////////////////////////////////////
    // net.daum.mf.map.api.MapView.MapViewEventListener

    public void onMapViewInitialized(MapView mapView) {
        //맵 처음 로딩했을 때
        Log.i("test", "MapView had loaded. Now, MapView APIs could be called safely");
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }



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





}
