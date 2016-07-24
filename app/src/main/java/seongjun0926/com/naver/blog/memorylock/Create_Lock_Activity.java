package seongjun0926.com.naver.blog.memorylock;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.ImageView;
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
public class Create_Lock_Activity extends FragmentActivity {
    private static final int RESULT_LOAD_IMAGE = 1;


    boolean isGPSEnabled, isNetworkEnabled;
    double lng;
    double lat;
    int M_C_Type=1;
    String Location, M_C_Creator, Search_Email, M_S_Pesrsons, M_C_Text;//위치 저장할 변수
    SharedPreferences setting;
    String urlString;

    EditText Search_Email_ET, M_C_Text_ET;
    Button Search_Email_Btn, Register_Btn;

    Finde_Share_Email FSE_task;
    String absolutePath;
    ImageView imageToUpload;
    Bitmap image;
    Uri selectedImage;

    // WriteJSP task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_lock_activity);

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
                double lat = location.getLatitude();
                double lng = location.getLongitude();

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
            lng = lastKnownLocation.getLatitude();
            lat = lastKnownLocation.getLatitude();
            Log.i("test", "2_longtitude=" + lng + ", 2_latitude=" + lat);

            Log.i("test","Location : "+Location);
        }
//------------------------------------------------------------------------------------------------------------------------------//
        Search_Email_ET = (EditText) findViewById(R.id.Search_Email);
        Search_Email_Btn = (Button) findViewById(R.id.Share_Email_Btn);

        M_C_Text_ET = (EditText) findViewById(R.id.M_C_Text_ET);
        Register_Btn = (Button) findViewById(R.id.Register_Btn);

        Search_Email_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search_Email = Search_Email_ET.getText().toString();

                if (Search_Email.length() != 0) {
                    if (CheckEmailForm(Search_Email)) {

                        FSE_task = new Finde_Share_Email();
                        FSE_task.execute("http://seongjun0926.cafe24.com/MemoryLock/Forget_Email.jsp?E_Mail=" + Search_Email);

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "추억을 공유할 사용자를 입력해주세요,", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Register_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                M_C_Text = M_C_Text_ET.getText().toString();
                String M_C_Text_enco = null;
                if (M_C_Text_ET.length() != 0) {
                    try {
                         M_C_Text_enco=java.net.URLEncoder.encode(new String(M_C_Text.getBytes("UTF-8")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    urlString = "http://seongjun0926.cafe24.com/MemoryLock/Register_Create.jsp?lat="+lat+"&lng="+lng+"&M_C_Creator="+M_C_Creator+"&M_C_Text="+M_C_Text_enco+"&M_C_Type="+M_C_Type+"&M_S_Pesrsons="+M_S_Pesrsons;
                    Log.i("test", "lat ="+lat+", lng="+lng+", M_C_Creator : "+M_C_Creator+", M_C_Text : "+M_C_Text+", M_C_Type : "+M_C_Type+", M_S_Pesrsons : "+M_S_Pesrsons);
                    Log.i("test",urlString);
                    DoFileUpload(urlString, absolutePath);

                } else {
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
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
                        M_S_Pesrsons = Search_Email;
                        Toast.makeText(getApplicationContext(), "공유 사용자가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        Search_Email_Btn.setClickable(false);

                    } else {
                        M_S_Pesrsons = null;
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


        }
    }



    public void DoFileUpload(String apiUrl, String absolutePath) {
        Log.i("test","DoFileUpload");
        Log.i("test","apiUrl : "+apiUrl);
        Log.i("test","absolutePath : "+absolutePath);
        HttpFileUpload(apiUrl, "",absolutePath);


    }

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    public void HttpFileUpload(String urlString, String params, String fileName) {
        try {
            FileInputStream mFileInputStream = new FileInputStream(fileName);
            Log.i("test", "mFileInputStream  is " + mFileInputStream);

            URL connectUrl = new URL(urlString);
            Log.i("test", "connectUrl  is " + connectUrl);


            // open connection 
            HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName+"\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

            Log.i("test", "image byte is " + bytesRead);

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
            Log.i("test" , "File is written");
            mFileInputStream.close();
            dos.flush(); // finish upload...   

            // get response
            int ch;
            InputStream is = conn.getInputStream();
            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 ){
                b.append( (char)ch );
            }
            String s=b.toString();
            Log.i("test", "result = " + s);

            dos.close();

        } catch (Exception e) {

            Log.i("test", "exception " + e.getMessage());
            // TODO: handle exception
        }
    }
}