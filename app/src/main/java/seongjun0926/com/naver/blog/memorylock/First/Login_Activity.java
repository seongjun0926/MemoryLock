package seongjun0926.com.naver.blog.memorylock.First;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import seongjun0926.com.naver.blog.memorylock.R;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    EditText Input_Email, Input_PW;
    Button Register_Btn, Forget_PW, Login_Btn;
    Compare_Info D_task;//디비 값 비교 하기위한 코드
    String Text_Email;
    String Text_PW;
    CheckBox Auto_Login;//자동 로그인을 위한 체크박스

    SharedPreferences setting;
    SharedPreferences.Editor editor; //자동 로그인을 위한 프리퍼런스(값 저장)


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(Login_Activity.this, "권한 허용", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(Login_Activity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_activity);

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("해당 권한을 허용하지 않으면 앱 사용을 하지 못합니다.\n\n해당 권한을 허용해주세요. [설정] > [권한]")
                .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        Log.i("test", "로그인 실행");

        Input_Email = (EditText) findViewById(R.id.Input_Email);
        Input_PW = (EditText) findViewById(R.id.Input_PW);
        //에딧텍스트 초기화

        Register_Btn = (Button) findViewById(R.id.Register_Btn);
        Forget_PW = (Button) findViewById(R.id.Forget_PW);
        Login_Btn = (Button) findViewById(R.id.Login_Btn);
        Auto_Login = (CheckBox) findViewById(R.id.Auto_Login);
        Auto_Login.setOnCheckedChangeListener(this);
        Forget_PW.setOnClickListener(this);
        Login_Btn.setOnClickListener(this);
        Register_Btn.setOnClickListener(this);
        //체크박스 초기화

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        if (setting.getBoolean("Auto_Login_enabled", false)) {
            Input_Email.setText(setting.getString("ID", ""));
            Input_PW.setText(setting.getString("PW", ""));
            Auto_Login.setChecked(true);

          /*  껐다켜도 값 유지
            첫번째 줄의 getBoolean은 Boolean타입의 데이터를 가져오겠다 라는 표시이고요,
            처음에 실행을 하게 되면 설정된 값이 없습니다
            그러므로 기본값인 false가 반환되어 처음 실행시에는 저 if문이 작동되지 않게 됩니다

            두번째와 세번째 줄을 보면 getString이라고 되어 있는대 이렇게 ID값과 PW값을 불러와서 EditText에 setText로 적용하는 모습입니다

            마지막으로 자동로그인이 활성화 되었으므로 CheckBox도 활성화 표시를 해줍니다*/
        }

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Forget_PW:
                Intent Forget_PW_Activity = new Intent(Login_Activity.this, Forget_PW_Activity.class);
                startActivity(Forget_PW_Activity);
                break;
            case R.id.Login_Btn:
                 Text_Email = Input_Email.getText().toString();
                 Text_PW = Input_PW.getText().toString();
                //이메일과 암호를 가져오기 위한 스트링 객체 선언

                if(Auto_Login.isChecked()==false)
                    Toast.makeText(getApplicationContext(),"자동 로그인 체크를 해주시면 원활한 사용이 가능합니다.",Toast.LENGTH_SHORT).show();



                    //이메일 형식이 맞을때
                    if (Text_Email.length() != 0 || Text_PW.length() != 0) {
                        D_task = new Compare_Info();
                        D_task.execute("http://seongjun0926.cafe24.com/MemoryLock/Compare_Info.jsp?E_Mail=" + Text_Email + "&PW=" + Text_PW); //주소에 get 방식으로 전송
                    } else {
                        Toast.makeText(getApplicationContext(), "이메일 또는 암호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }

                break;
            case R.id.Register_Btn:
                Intent Register_Activity = new Intent(Login_Activity.this, Register_Activity.class);
                startActivity(Register_Activity);
                break;
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            String ID = Input_Email.getText().toString();
            String PW = Input_PW.getText().toString();
            editor.putString("ID", ID);
            editor.putString("PW", PW);
            editor.putBoolean("Auto_Login_enabled", true);
            editor.commit();
        } else {
//         editor.remove("ID");
//         editor.remove("PW");
//         editor.remove("Auto_Login_enabled");
            editor.clear();
            editor.commit();
/*
                    처음 세줄은 주석처리 되어 있습니다 그 이유는 모든 설정을 파괴할 것이니 일일히 써넣지 말고 한번에 지워버리는것이 코드 절약에도 좋습니다

                    이 소스에서 가장 중요한 부분은 editor.commit();입니다
                    위에서도 강조하였는대 저 코드가 없다면 실제로 변경된 내용이 반영되지 않는 경우가 생깁니다

                    (왜 설정이 변하지 않지? 하다가 저 commit()을 안해줘서 반영이 안된 사례도 많습니다)

*/

        }
    }

    private class Compare_Info extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return jsonHtml.toString();

        }

        protected void onPostExecute(String str) {

            String Check;
            JSONObject root;

            try {

                root = new JSONObject(str);
                Log.i("test", "root=" + root.toString());

                JSONArray ja = root.getJSONArray("results");
                Log.i("test", "ja=" + ja.toString());

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    Log.i("test", "jo=" + jo);

                    Check = jo.getString("Check");
                    Log.i("test", "Check=" + Check);

                    if (Check.equals("succed")) {
                        //ID와 PW가 일치하면 MainActivity 실행

                        editor.putString("ID", Text_Email);
                        editor.putString("PW", Text_PW);
                        editor.commit();
                        //프리퍼런스에 Email값과 PW 값을 넣어 준 후 commit을 시켜야 값이 저장되고 사용가능

                        Intent Main_Activity = new Intent(Login_Activity.this, seongjun0926.com.naver.blog.memorylock.Main_Activity.class);
                        startActivity(Main_Activity);
                        finish();
                    } else {
                        //일치하지 않으면 Toast 띄워줌
                        Toast.makeText(getApplicationContext(),"E-mail 또는 암호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

}