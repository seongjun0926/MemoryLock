package seongjun0926.com.naver.blog.memorylock;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class test extends AppCompatActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMAGE=1;
    private static final String  Server_ADRESS="Http://cometocu.com/MemoryLock/";
    ImageView imageToUpload;
    Button bUploadImage;
    EditText uploadimageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        imageToUpload=(ImageView)findViewById(R.id.imageToUpload);
        bUploadImage=(Button)findViewById(R.id.bUploadImage);
        uploadimageName=(EditText)findViewById(R.id.etUploadName);


        imageToUpload.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.imageToUpload:

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);

                break;
            case R.id.bUploadImage:
                Bitmap image=((BitmapDrawable)imageToUpload.getDrawable()).getBitmap();
                new UploadImage(image,uploadimageName.getText().toString()).execute();

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

                if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data !=null){

                    Uri selectedImage = data.getData();
                    imageToUpload.setImageURI(selectedImage);

                }
    }

    private class UploadImage extends AsyncTask<Void, Void, Void>{
        Bitmap image;
        String name;

        public UploadImage(Bitmap image, String name){
            this.image=image;
            this.name=name;

        }

        @Override
        protected Void doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
            String encodedImage= Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

            ArrayList<NameValuePair> dataTosend = new ArrayList<>();
            dataTosend.add(new BasicNameValuePair("image",encodedImage));
            dataTosend.add(new BasicNameValuePair("name",name));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(Server_ADRESS+"MemoryLock/저장 할 jsp 파일");


            try{
                post.setEntity(new UrlEncodedFormEntity(dataTosend));
                client.execute(post);

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),"Image upload",Toast.LENGTH_SHORT).show();
        }
    }


    private HttpParams getHttpRequestParams(){
        HttpParams httpRequestParams=new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpRequestParams,1000*30);
        HttpConnectionParams.setSoTimeout(httpRequestParams,1000*30);

        return httpRequestParams;
    }
}