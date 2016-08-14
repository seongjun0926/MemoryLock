package seongjun0926.com.naver.blog.memorylock.TimeCapsule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import seongjun0926.com.naver.blog.memorylock.R;

public class TimeCapsulePop_Activity extends FragmentActivity {

    Button Create_TimeCapsule_Btn,Show_TimeCapsule_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timecapsulepop_activity);


        Create_TimeCapsule_Btn = (Button)findViewById(R.id.Create_TimeCapsule_Btn);
        Show_TimeCapsule_Btn = (Button)findViewById(R.id.Show_TimeCapsule_Btn);

        Create_TimeCapsule_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Create_Lock_Activity = new Intent(TimeCapsulePop_Activity.this, Create_Time_Activity.class);
                startActivity(Create_Lock_Activity);
                finish();
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
