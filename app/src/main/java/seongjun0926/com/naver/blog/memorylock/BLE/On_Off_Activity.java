package seongjun0926.com.naver.blog.memorylock.BLE;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ToggleButton;

import seongjun0926.com.naver.blog.memorylock.R;

public class On_Off_Activity extends FragmentActivity {

    ToggleButton on_off_btn;
    byte[] col = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.on_off_activity);

        on_off_btn=(ToggleButton)findViewById(R.id.on_off_btn);
        on_off_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(on_off_btn.isChecked()){
                    on_off_btn.setText("열기");
                    on_off_btn.setChecked(true);
                    on_off_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.lock_on_btn));
                    on_off_btn.setClickable(true);
                    col = new byte[]{  0x01, (byte)(0)};
                    // col = 0;
                    Log.i("test","on : "+col);}
                else{
                    on_off_btn.setText("잠그기");
                    on_off_btn.setChecked(false);
                    on_off_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.lock_btn));
                    on_off_btn.setClickable(true);
                    col = new byte[]{  0x01, (byte)(180)};
                    // col = 0;
                    Log.i("test","off : "+col);
                }
                if( FindDeviceActivity.findDeviceActivity != null){
                    FindDeviceActivity.findDeviceActivity.OnDataChangeListener(col);
                }
            }

        });


    }
}




