package seongjun0926.com.naver.blog.memorylock.TimeCapsule;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import seongjun0926.com.naver.blog.memorylock.R;

public class TimeCapsulePop_Activity extends FragmentActivity{


    Button Create_TimeCapsule_Btn, Show_TimeCapsule_Btn;

    boolean isGPSEnabled, isNetworkEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timecapsulepop_activity);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // GPS 프로바이더 사용가능여부
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Create_TimeCapsule_Btn = (Button) findViewById(R.id.Create_TimeCapsule_Btn);
        Show_TimeCapsule_Btn = (Button) findViewById(R.id.Show_TimeCapsule_Btn);

        Create_TimeCapsule_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //isGPSEnaBled가 false면 위치정보가 안켜져있어서 생성안됌
                if (isGPSEnabled == false || isNetworkEnabled == false) {
                    Toast.makeText(getApplicationContext(), "위치정보를 켜주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent Create_Lock_Activity = new Intent(TimeCapsulePop_Activity.this, Create_Time_Activity.class);
                    startActivity(Create_Lock_Activity);
                    finish();
                }
        }
        });

        Show_TimeCapsule_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent Create_Lock_Activity = new Intent(TimeCapsulePop_Activity.this, Show_Time_Activity.class);
                    startActivity(Create_Lock_Activity);

                    finish();

            }
        });


    }



}
