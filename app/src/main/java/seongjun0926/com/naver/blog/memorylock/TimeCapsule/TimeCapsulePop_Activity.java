package seongjun0926.com.naver.blog.memorylock.TimeCapsule;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import seongjun0926.com.naver.blog.memorylock.R;

public class TimeCapsulePop_Activity extends FragmentActivity  implements BeaconConsumer {


    Button Create_TimeCapsule_Btn, Show_TimeCapsule_Btn;

    boolean isGPSEnabled, isNetworkEnabled;
    Boolean Beacon_Check = false;
    String Beacon_Add = "ML_BEACON";
    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timecapsulepop_activity);

        Intent intent = getIntent();
        Beacon_Check = intent.getExtras().getBoolean("Beacon_Check");

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        // 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
        beaconManager.bind(this);

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
                    if(Beacon_Check) {
                        Intent Create_Lock_Activity = new Intent(TimeCapsulePop_Activity.this, Create_Time_Activity.class);
                        startActivity(Create_Lock_Activity);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "감지된 비콘이 없습니다!", Toast.LENGTH_SHORT).show();
                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                       /* Log.i("test","? : "+Beacon_Add.equals(beacon.getBluetoothName()));
                        Log.i("test","?2 : "+beacon.getBluetoothAddress());
                        Log.i("test","?3 : "+beacon.getBluetoothName());*/
                        Log.i("Test", "BlutoothName : " + beacon.getBluetoothName());
                        if (Beacon_Add.equals(beacon.getBluetoothName())) {
                            beaconList.add(beacon);
                            Beacon_Check = true;
                        }
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

}
