package seongjun0926.com.naver.blog.memorylock.Lock;

import android.content.Intent;
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

public class LockPop_Activity extends FragmentActivity implements BeaconConsumer {

    Button Show_Lock_Btn, Create_Lock_Btn;

    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    Boolean Beacon_Check=false;
    String Beacon_Add="ML_BEACON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lockpop_activity);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
// 비콘 탐지를 시작한다. 실제로는 서비스를 시작하는것.
        beaconManager.bind(this);


        Create_Lock_Btn = (Button) findViewById(R.id.Create_Lock_Btn);
        Show_Lock_Btn = (Button) findViewById(R.id.Show_Lock_Btn);


        //생성 버튼 클릭했을 때
        Create_Lock_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Beacon_Check) {
                    Intent Create_Lock_Activity = new Intent(LockPop_Activity.this, Create_Lock_Activity.class);
                    startActivity(Create_Lock_Activity);
                    Beacon_Check=false;
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "감지된 비콘이 없습니다!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //자물쇠 목록 보기 버튼
        Show_Lock_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Show_Lock_Activity = new Intent(LockPop_Activity.this, Show_Lock_Activity.class);
                startActivity(Show_Lock_Activity);
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
                        Log.i("Test", "BlutoothName : "+beacon.getBluetoothName());
                        if(Beacon_Add.equals(beacon.getBluetoothName())){
                            beaconList.add(beacon);
                            Beacon_Check=true;
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
