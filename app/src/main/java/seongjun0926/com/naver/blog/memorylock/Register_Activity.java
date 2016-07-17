package seongjun0926.com.naver.blog.memorylock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Register_Activity extends AppCompatActivity {

    TextView Email_Check_TV; //이메일 중복 체크를 위한 TextView
    EditText Name_ET, Email_ET, PW_ET, PW_Check_ET,PW_Question_ET,PW_Answer_ET; // 별명, 이메일, 암호, 암호체크 입력 받는 EditText
    Button Register_Btn, Email_Check_Btn; // 로그인 버튼, 회원 가입버튼, Email 중복 체크 버튼
    CheckBox Agree_Check; //정보 제공 동의 체크 버튼

    int Email_Check_Num = 0; //Email 중복 여부를 확인 하기위한 변수
    phpDown D_task;
    WriteJSP task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);


        Log.i("test", "회원가입 실행");

        Name_ET = (EditText) findViewById(R.id.Name_ET);
        Email_ET = (EditText) findViewById(R.id.Email_ET);
        PW_ET = (EditText) findViewById(R.id.PW_ET);
        PW_Check_ET = (EditText) findViewById(R.id.PW_Check_ET);
        PW_Question_ET=(EditText)findViewById(R.id.PW_Question);
        PW_Answer_ET=(EditText)findViewById(R.id.PW_Answer);

        Email_Check_Btn = (Button) findViewById(R.id.Email_Check_Btn);
        Register_Btn = (Button) findViewById(R.id.Register_Btn);

        Agree_Check = (CheckBox) findViewById(R.id.Agree_Check);





        Email_Check_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("test", "중복체크 눌림");
                //이메일 중복 체크 클릭 리스너
                if (Email_ET.getText().length()<=0){//Email을 입력하지 않았을 때.
                    Toast.makeText(getApplicationContext(), "E_Mail을 입력해주세요", Toast.LENGTH_SHORT).show();

                }else{
                    //email 입력하고
                    String Send_Email = Email_ET.getText().toString();
                    int i=1;
                    if(CheckEmailForm(Send_Email)==true){

                        //이메일 형식이 맞을때
                        D_task = new phpDown();
                        D_task.execute("http://seongjun0926.cafe24.com/MemoryLock/Search_Email.jsp?E_Mail=" + Send_Email); //주소에 get 방식으로 전송
                    }else{
                        //이메일 형식이 맞지 않을 때
                        Toast.makeText(getApplicationContext(), "E_Mail 형식을 확인해주세요", Toast.LENGTH_SHORT).show();
                    }


                }

               }
        });



        Register_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 버튼 누름
                int Name_ET_Length = Name_ET.getText().length();
                int Email_ET_Length = Email_ET.getText().length();
                int PW_Question_Length=PW_Question_ET.getText().length();
                int PW_Answer_Length=PW_Answer_ET.getText().length();

                if (Email_Check_Num == 0) {//이메일 중복인지 아닌지
                    Toast.makeText(getApplicationContext(), "E_Mail을 확인해주세요", Toast.LENGTH_SHORT).show();

                } else {
                    if (Name_ET_Length == 0 || Email_ET_Length == 0 || PW_Question_Length==0 || PW_Answer_Length==0) {//다른 칸들을 다 채웠는지 안채웠는지
                        Toast.makeText(getApplicationContext(), "빈칸을 채워주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Agree_Check.isChecked() == false) {
                            //체크박스 체크 안돼어있으면
                            Toast.makeText(getApplicationContext(), "정보 제공 동의를 해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            //체크 되어있으면 실행
                            if (PW_ET.getText().toString().equals(PW_Check_ET.getText().toString())) {
                                //암호가 일치 한다면 DB연동
                                task = new WriteJSP();
                                task.execute();

                                Toast.makeText(getApplicationContext(),"회원 가입이 완료되었습니다. 로그인을 해주세요.",Toast.LENGTH_SHORT).show();
                                finish();

                                //회원가입이 완료됨을 알려주고 현재 액티비티 종료

                            } else {
                                //일치하지 않으면 Toast 띄어줌
                                Toast.makeText(getApplicationContext(), "암호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }


            }
        });


    }
    public boolean CheckEmailForm(String src) {
        //이메일 형식 검사 함수
        String EmailRegex = "^[_a-z0-9-]+(.[_a-z0-9-]+)@(?:\\w+\\.)+\\w+$";
        return Pattern.matches(EmailRegex, src);
    }


    private class phpDown extends AsyncTask<String, Integer, String> {


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
                            Log.i("test", line);
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
                        Toast.makeText(getApplicationContext(),"사용할 수 있는 E-mail 입니다",Toast.LENGTH_SHORT).show();
                        Email_Check_Num = 1;
                    } else {
                        Toast.makeText(getApplicationContext(),"사용할 수 없는 E-mail 입니다",Toast.LENGTH_SHORT).show();
                        Email_Check_Num = 0;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }


    class WriteJSP extends AsyncTask<Void, String, Void> {
        String Name = Name_ET.getText().toString();
        String Email = Email_ET.getText().toString();
        String PW = PW_ET.getText().toString();
        String PW_Answer=PW_Answer_ET.getText().toString();
        String PW_Question=PW_Question_ET.getText().toString();

        @Override
        protected Void doInBackground(Void... voids) {


            try {
                Log.i("test","Name= "+Name);
                Log.i("test","Email= "+Email);
                Log.i("test","PW= "+PW);
                Log.i("test","PW_Question= "+PW_Question);
                Log.i("test","PW_Answer= "+PW_Answer);

                HttpClient client = new DefaultHttpClient();

                String RegisterURL = "http://seongjun0926.cafe24.com/MemoryLock/Register.jsp";

                HttpPost post = new HttpPost(RegisterURL);
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("Name", Name));
                params.add(new BasicNameValuePair("Email", Email));
                params.add(new BasicNameValuePair("PW", PW));
                params.add(new BasicNameValuePair("PW_Question", PW_Question));
                params.add(new BasicNameValuePair("PW_Answer", PW_Answer));

                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);

                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();

                if (resEntity != null) {
                    Log.i("RESPONSE", EntityUtils.toString(resEntity));
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
