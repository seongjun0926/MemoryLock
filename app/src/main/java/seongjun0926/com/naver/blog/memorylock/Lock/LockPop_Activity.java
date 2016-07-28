package seongjun0926.com.naver.blog.memorylock.Lock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import seongjun0926.com.naver.blog.memorylock.R;

public class LockPop_Activity extends FragmentActivity {

    Button Show_Lock_Btn, Create_Lock_Btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lockpop_activity);


        Create_Lock_Btn = (Button) findViewById(R.id.Create_Lock_Btn);
        Show_Lock_Btn = (Button) findViewById(R.id.Show_Lock_Btn);


        //생성 버튼 클릭했을 때
        Create_Lock_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Create_Lock_Activity = new Intent(LockPop_Activity.this, Create_Lock_Activity.class);
                startActivity(Create_Lock_Activity);
                finish();

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
}