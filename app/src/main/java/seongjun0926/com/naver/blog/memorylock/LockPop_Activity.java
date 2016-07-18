package seongjun0926.com.naver.blog.memorylock;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Button;

public class LockPop_Activity extends FragmentActivity {

    Button Show_Lock_Btn,Create_Lock_Btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lockpop_activity);



    }
}
