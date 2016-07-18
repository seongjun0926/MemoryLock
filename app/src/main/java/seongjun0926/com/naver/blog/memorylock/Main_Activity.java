package seongjun0926.com.naver.blog.memorylock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import net.daum.mf.map.api.MapView;

public class Main_Activity extends FragmentActivity {

    Button Lock_Btn, TimeCapsule_Btn, Shared_Btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        Lock_Btn = (Button) findViewById(R.id.Lock_Btn);
        TimeCapsule_Btn = (Button) findViewById(R.id.TimeCapSule_Btn);
        Shared_Btn = (Button) findViewById(R.id.Shared_Btn);


        Lock_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LockPop_Activity = new Intent(Main_Activity.this, LockPop_Activity.class);
                startActivity(LockPop_Activity);

            }
        });

        TimeCapsule_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent TimeCapsulePop_Activity = new Intent(Main_Activity.this, TimeCapsulePop_Activity.class);
                startActivity(TimeCapsulePop_Activity);
            }
        });

        //=========================================================================================================//
        MapView mapView = new MapView(this);
        mapView.setDaumMapApiKey("14f0409b73f10d9a817b7968ca7c9a19");

//xml에 선언된 map_view 레이아웃을 찾아온 후, 생성한 MapView객체 추가
        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);

        container.addView(mapView);

    }
}
