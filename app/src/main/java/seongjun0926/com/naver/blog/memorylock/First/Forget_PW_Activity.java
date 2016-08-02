package seongjun0926.com.naver.blog.memorylock.First;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import seongjun0926.com.naver.blog.memorylock.R;

public class Forget_PW_Activity extends AppCompatActivity {
    EditText Input_Email_ET, Input_Answer_ET, Input_Change_PW_ET;
    TextView Show_Info1,Show_Info2;
    Button Input_Email_Btn, Input_Answer_Btn, Input_Change_PW_Btn;

    LinearLayout DynamicLayout1,DynamicLayout2,DynamicLayout3,DynamicLayout4;


    Forget_to_EMail_do_Search FES_task;
    Check_PW_Answer CPA_task;
    Change_PW CP_task;


    //ListItem객체는 JSON형식으로 데이터를 셋팅하기 위한 클래스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forget_pw_activity);

        Input_Email_ET = (EditText) findViewById(R.id.Input_Email);
        //Input_Answer_ET=(EditText)findViewById(R.id.Input_Answer);


        Input_Email_Btn = (Button) findViewById(R.id.Input_Email_Btn);
        //Input_Answer_Btn=(Button)findViewById(R.id.Input_Answer_Btn);

        //에딧텍스트, 텍스트뷰, 버튼 생성

        Input_Email_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Input_Email = Input_Email_ET.getText().toString();//이메일 에딧텍스트 값 가져오기
                Log.i("test", "Input_Email : " + Input_Email);

                if (Input_Email.length() != 0) {
                    //Email이 입력이 되었다면
                    if (CheckEmailForm(Input_Email) == true) {
                        //Email 형식이 올바르다면
                        FES_task = new Forget_to_EMail_do_Search();
                        FES_task.execute("http://seongjun0926.cafe24.com/MemoryLock/Forget_Email.jsp?E_Mail=" + Input_Email);

                    } else {
                        //Email 형식이 올바르지 않으면
                        Toast.makeText(getApplicationContext(), "E-Mail 형식을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //Email을 입력하지 않고 버튼을 누르면
                    Toast.makeText(getApplicationContext(), "E-Mail을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public boolean CheckEmailForm(String src) {
        //이메일 형식 검사 함수
        String EmailRegex = "^[_a-z0-9-]+(.[_a-z0-9-]+)@(?:\\w+\\.)+\\w+$";
        return Pattern.matches(EmailRegex, src);
    }


    private class Forget_to_EMail_do_Search extends AsyncTask<String, Integer, String> {


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
            String Question;
            JSONObject root;

            try {

                root = new JSONObject(str);
                Log.i("test", "root=" + root.toString());

                JSONArray ja = root.getJSONArray("results");
                Log.i("test", "ja=" + ja.toString());

                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    Log.i("test", "jo=" + jo);

                    Check = jo.getString("Check");//Json에서 Check라는 키의 값을 가져옴
                    if (Check.equals("succed")) {
                        Question = jo.getString("Question");//Json에서 Question이라는 키의 값을 가져옴

                        Input_Email_Btn.setClickable(false);//Email확인 했음으로 버튼 비활성화

                        DynamicLayout1 = (LinearLayout) findViewById(R.id.DynamicLayout1);

                        Show_Info1 = new TextView(Forget_PW_Activity.this);//동적으로 텍스트 뷰 생성
                        Show_Info1.setTextAppearance(Forget_PW_Activity.this, R.style.Font);//values의 style.xml에 지정된 Font라는 이름의 꾸밈을 가져옴
                        Show_Info1.setText("암호 찾기 질문");//뭐라고 적을지 설정

                        LinearLayout.LayoutParams Lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        Lp1.gravity = Gravity.CENTER_HORIZONTAL;//gravity 설정

                        Show_Info1.setLayoutParams(Lp1);//Lp1에 집어 넣음


                        DynamicLayout1.addView(Show_Info1);//레이아웃에 집어넣음 이 순서대로 들어감 1번
//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------//
                        DynamicLayout2 = (LinearLayout) findViewById(R.id.DynamicLayout2);
                        Input_Answer_ET = new EditText(Forget_PW_Activity.this);
                        Input_Answer_ET.setHint("입력하신 질문은 "+Question+" 입니다. 여기에 답을 입력해주세요.");
                        Input_Answer_ET.setHintTextColor(Color.parseColor("#FF5DA9FA"));
                        LinearLayout.LayoutParams Lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        Lp2.weight = 1;
                        Input_Answer_Btn = new Button(Forget_PW_Activity.this);
                        Input_Answer_Btn.setText("답 확인");


                        Input_Answer_ET.setLayoutParams(Lp2);
                        Input_Answer_Btn.setLayoutParams(Lp2);
                        DynamicLayout2.addView(Input_Answer_ET);
                        DynamicLayout2.addView(Input_Answer_Btn);

                        Input_Answer_Btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String Input_Answer=Input_Answer_ET.getText().toString();
                                Log.i("test","Input_Answer= "+Input_Answer);

                                if (Input_Answer_ET.length() != 0) {
                                    CPA_task = new Check_PW_Answer();
                                    CPA_task.execute("http://seongjun0926.cafe24.com/MemoryLock/Check_PW_Answer.jsp?PW_Answer=" + Input_Answer);
                                }else{
                                    Toast.makeText(getApplicationContext(),"답이 입력되지 않았습니다.",Toast.LENGTH_SHORT).show();
                                }


                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "일치하는 E-Mail이 없습니다.", Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }


    private class Check_PW_Answer extends AsyncTask<String, Integer, String> {


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

                    Check = jo.getString("Check");//Json에서 Check라는 키의 값을 가져옴
                    if (Check.equals("succed")) {
                        Input_Answer_Btn.setClickable(false);

                        DynamicLayout4 = (LinearLayout) findViewById(R.id.DynamicLayout4);

                        Show_Info2 = new TextView(Forget_PW_Activity.this);//동적으로 텍스트 뷰 생성
                        Show_Info2.setTextAppearance(Forget_PW_Activity.this, R.style.Font);//values의 style.xml에 지정된 Font라는 이름의 꾸밈을 가져옴
                        Show_Info2.setText("암호 변경");//뭐라고 적을지 설정

                        LinearLayout.LayoutParams Lp4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        Lp4.gravity = Gravity.CENTER_HORIZONTAL;//gravity 설정

                        Show_Info2.setLayoutParams(Lp4);//Lp1에 집어 넣음


                        DynamicLayout4.addView(Show_Info2);//레이아웃에 집어넣음 이 순서대로 들어감 1번

                        //---------------------------------------------------------------------------------------------------------------------------------------------------//


                        DynamicLayout3 = (LinearLayout) findViewById(R.id.DynamicLayout3);

                        Input_Change_PW_ET = new EditText(Forget_PW_Activity.this);//동적으로 텍스트 뷰 생성
                        Input_Change_PW_ET.setHint("변경할 암호를 적어주세요.");
                        Input_Change_PW_ET.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        Input_Change_PW_ET.setEms(10);

                        LinearLayout.LayoutParams Lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        Lp3.weight = 1;
                        Input_Change_PW_Btn = new Button(Forget_PW_Activity.this);
                        Input_Change_PW_Btn.setText("암호 변경");


                        Input_Change_PW_ET.setLayoutParams(Lp3);
                        Input_Change_PW_ET.setLayoutParams(Lp3);
                        DynamicLayout3.addView(Input_Change_PW_ET);
                        DynamicLayout3.addView(Input_Change_PW_Btn);

                        Input_Change_PW_Btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String Input_Change_PW=Input_Change_PW_ET.getText().toString();

                                if(Input_Change_PW.length()!=0){
                                    CP_task = new Change_PW();
                                    CP_task.execute();

                                    finish();
                                    Toast.makeText(getApplicationContext(),"암호 변경이 완료되었습니다.",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"변경할 암호가 입력되지 않았습니다.",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "답이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }
    class Change_PW extends AsyncTask<Void, String, Void> {
        String Input_Email=Input_Email_ET.getText().toString();
        String Input_Change_PW=Input_Change_PW_ET.getText().toString();

        @Override
        protected Void doInBackground(Void... voids) {


            try {

                Log.i("test","Email= "+Input_Email);
                Log.i("test","PW= "+Input_Change_PW);

                HttpClient client = new DefaultHttpClient();

                String RegisterURL = "http://seongjun0926.cafe24.com/MemoryLock/Change_PW.jsp";

                HttpPost post = new HttpPost(RegisterURL);
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();


                params.add(new BasicNameValuePair("Email", Input_Email));
                params.add(new BasicNameValuePair("Change_PW", Input_Change_PW));


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
