package jp.techacademy.mito.yuuya.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;


    double mTimerSec =0.0;


    Timer mTimer;
    Handler mHandler = new Handler();

    Cursor cursor;
    Uri imageUri;

    Button mPlayButton;
    Button mForwardButton;
    Button mReverseButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mPlayButton = (Button) findViewById(R.id.playButton);
        Button mForwardButton = (Button) findViewById(R.id.forwardButton);
        Button mReverseButton = (Button) findViewById(R.id.reverseButton);


        //Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //パーミッションの許可状態を確認する
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                //許可されている
                getContentsInfo();
            }else{
                //許可されていないので、ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
            }
        }else{
            getContentsInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getContentsInfo();
                    }
                break;
            default:
                break;
        }
    }

    public void getContentsInfo() {

        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );

        //indexからIDを習得し、そのIDから画像のURIを習得する
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


        mPlayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mTimerSec +=2.0;

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                cursor.moveToNext();
                                    //ImageViewで表示を依頼する
                                    ImageView imageView = (ImageView) findViewById(R.id.image);
                                    imageView.setImageURI(imageUri);
                                cursor.close();

                            }
                        });
                    }
                },2000,2000);

            }

        });


        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        mReverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });
    }
}
