package seongjun0926.com.naver.blog.memorylock.TimeCapsule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import seongjun0926.com.naver.blog.memorylock.R;

public class Create_Time_Activity extends FragmentActivity {
    private static final int RESULT_LOAD_IMAGE = 1;


    boolean isGPSEnabled, isNetworkEnabled;
    double lng;
    double lat;
    int M_C_Type = 2;
    String Location, M_C_Creator, Search_Email, M_S_Persons, M_C_Text, M_C_Header;//위치 저장할 변수
    SharedPreferences setting;
    String urlString;

    EditText Search_Email_ET, M_C_Text_ET, M_C_Header_ET;
    Button Search_Email_Btn, Register_Btn;

    Finde_Share_Email FSE_task;
    String absolutePath;
    ImageView imageToUpload;

    DatePicker DP;
    String Input_Date;

    int Share_Email, Open_Check=0;
    DoFileUpload DFU;

    RadioButton.OnClickListener optionOnClickListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_time_activity);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());

        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        imageToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });

        RadioGroup RG=(RadioGroup)findViewById(R.id.RadioGroup);
        RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.Open_Radio1){
                    Open_Check = 1;
                    Toast.makeText(getApplicationContext(),"공개를 선택했습니다.",Toast.LENGTH_SHORT).show();
                }else if(checkedId==R.id.Open_Radio2){
                    Open_Check = 2;
                    Toast.makeText(getApplicationContext(),"비공개를 선택했습니다.",Toast.LENGTH_SHORT).show();
                }else{
                    Open_Check = 0;
                }
            }
        });


        setting = getSharedPreferences("setting", 0);
        M_C_Creator = setting.getString("ID", "");
//------------------------------------------------------------------------------------------------------------------------------//
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // GPS 프로바이더 사용가능여부
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.i("test", "isGPSEnabled=" + isGPSEnabled);
        Log.i("test", "isNetworkEnabled=" + isNetworkEnabled);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();

                Log.i("test", "1_latitude: " + lat + ", 1_longitude: " + lng);
                Location = String.format("%s---%s", lat, lng);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("test", "onStatusChanged");
            }

            public void onProviderEnabled(String provider) {
                Log.i("test", "onProviderEnabled");
            }

            public void onProviderDisabled(String provider) {
                Log.i("test", "onProviderDisabled");
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {

            //main 의 onMapViewInitialized에  mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading); 가 선언되어있어야 가능함

            lng = lastKnownLocation.getLongitude();
            lat = lastKnownLocation.getLatitude();

        }
//------------------------------------------------------------------------------------------------------------------------------//
        Search_Email_ET = (EditText) findViewById(R.id.Search_Email);
        Search_Email_Btn = (Button) findViewById(R.id.Share_Email_Btn);

        M_C_Text_ET = (EditText) findViewById(R.id.M_C_Text_ET);
        Register_Btn = (Button) findViewById(R.id.Register_Btn);

        M_C_Header_ET = (EditText) findViewById(R.id.M_C_Header_ET);

        DP = (DatePicker) findViewById(R.id.datePicker);

        DP.init(DP.getYear(), DP.getMonth(), DP.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                Input_Date = String.valueOf(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

            }

        });

        Search_Email_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search_Email = Search_Email_ET.getText().toString();

                if (Search_Email.length() != 0) {
                    if (CheckEmailForm(Search_Email)) {

                        FSE_task = new Finde_Share_Email();
                        FSE_task.execute("http://seongjun0926.cafe24.com/MemoryLock/Forget_Email.jsp?E_Mail=" + Search_Email);

                    } else {
                        Toast.makeText(getApplicationContext(), "이메일 양식을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "추억을 공유할 사용자를 입력해주세요,", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Register_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Open_Check != 0) {

                    if (Search_Email_ET.getText().toString().length() >= 1 && Share_Email == 0) {
                        Toast.makeText(getApplicationContext(), "공유 사용자란을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else {

                        M_C_Text = M_C_Text_ET.getText().toString();
                        M_C_Header = M_C_Header_ET.getText().toString();
                        String M_C_Text_enco = null;
                        String M_C_Header_enco = null;

                        if (M_C_Header.length() != 0) {
                            if (M_C_Text_ET.length() != 0) {
                                if (Input_Date != null) {
                                    Toast.makeText(getApplicationContext(), "사진 업로드 중입니다. 기다려주세요.", Toast.LENGTH_SHORT).show();

                                    try {
                                        M_C_Text_enco = java.net.URLEncoder.encode(new String(M_C_Text.getBytes("UTF-8")));
                                        M_C_Header_enco = java.net.URLEncoder.encode(new String(M_C_Header.getBytes("UTF-8")));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    urlString = "http://seongjun0926.cafe24.com/MemoryLock/Register_Create.jsp?lat=" + lat + "&lng=" + lng + "&M_C_Creator=" + M_C_Creator + "&M_C_Header=" + M_C_Header_enco + "&M_C_Text=" + M_C_Text_enco + "&M_C_Type=" + M_C_Type + "&M_S_Persons=" + M_S_Persons + "&M_C_OpenTime=" + Input_Date+"&M_C_Open="+Open_Check;
                                    Log.i("test", urlString);
                                    DFU = new DoFileUpload();
                                    DFU.execute(urlString, absolutePath);

                                } else {
                                    Toast.makeText(getApplicationContext(), "타임캡슐 개봉 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "공개 / 비공개를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }


    public boolean CheckEmailForm(String src) {
        //이메일 형식 검사 함수
        String EmailRegex = "^[_a-z0-9-]+(.[_a-z0-9-]+)@(?:\\w+\\.)+\\w+$";
        return Pattern.matches(EmailRegex, src);
    }


    private class Finde_Share_Email extends AsyncTask<String, Integer, String> {


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
                        M_S_Persons = Search_Email;
                        Toast.makeText(getApplicationContext(), "공유 사용자가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        Share_Email = 1;
                        Search_Email_Btn.setClickable(false);

                    } else {
                        M_S_Persons = null;
                        Toast.makeText(getApplicationContext(), "일치하는 E-Mail이 없습니다.", Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            Uri selPhotoUri = data.getData();
            imageToUpload.setImageURI(selPhotoUri);
            Log.i("test", "selPhotoUri : " + selPhotoUri);

            //imageToUpload.setImageURI(selectedImage);
            try {
                Bitmap selPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), selPhotoUri);


                Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null, null, null, null);
                c.moveToNext();
                absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                Log.i("test", "absolutePath : " + absolutePath);


//파일 업로드 시작!
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            Toast.makeText(getApplicationContext(), "사진을 선택해주세요!!", Toast.LENGTH_SHORT).show();
        }
    }


    public class DoFileUpload extends AsyncTask<String, Void, Void> {

        private ProgressDialog mDlg = new ProgressDialog(Create_Time_Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //스타일 설정
            mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //프로그래스 다이얼로그 나올 때 메시지 설정.
            mDlg.setMessage("내용을 저장하고있습니다.");
            //세팅된 다이얼로그를 보여줌.
            mDlg.show();

        }

        @Override
        protected Void doInBackground(String... strings) {
            String apiUrl = strings[0];
            String absolutePath = strings[1];

            try {
                for (int i = 0; i < 5; i++) {
                    mDlg.setProgress(i * 30);
                    Thread.sleep(500);
                }
                HttpFileUpload(apiUrl, "", absolutePath);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDlg.dismiss();
            Toast.makeText(getApplicationContext(), "등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            Intent Main_Activity = new Intent(Create_Time_Activity.this, seongjun0926.com.naver.blog.memorylock.Main_Activity.class);
            startActivity(Main_Activity);
            finish();
        }
    }

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    public void HttpFileUpload(String urlString, String params, String fileName) {

        try {

            FileInputStream mFileInputStream = new FileInputStream(fileName);
            URL connectUrl = new URL(urlString);
            Log.i("test", "connectUrl  is " + connectUrl);


            // open connection
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());


            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);


            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            mFileInputStream.close();
            dos.flush(); // finish upload...

            // get response
            int ch;
            InputStream is = conn.getInputStream();
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String s = b.toString();
            dos.close();


        } catch (Exception e) {


            Toast.makeText(getApplicationContext(), "사진을 선택해주세요!!", Toast.LENGTH_SHORT).show();
            Log.i("test", "exception " + e.getMessage());
            // TODO: handle exception
        }
    }
}
