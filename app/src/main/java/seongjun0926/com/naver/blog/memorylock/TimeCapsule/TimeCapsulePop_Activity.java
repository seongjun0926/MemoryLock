package seongjun0926.com.naver.blog.memorylock.TimeCapsule;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import seongjun0926.com.naver.blog.memorylock.R;

public class TimeCapsulePop_Activity extends FragmentActivity {

    String E_Mail;
    SharedPreferences setting;
    SharedPreferences.Editor editor; //자동 로그인을 위한 프리퍼런스(값 저장)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timecapsulepop_activity);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        if (setting.getBoolean("Auto_Login_enabled", false)) {
            E_Mail=setting.getString("ID", "");
        }
    }
}
