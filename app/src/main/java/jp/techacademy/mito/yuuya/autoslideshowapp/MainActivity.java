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

        Button mPlayButton = (Button) findViewById(R.id.playButton);
        final Button mForwardButton = (Button) findViewById(R.id.forwardButton);
        final Button mReverseButton = (Button) findViewById(R.id.reverseButton);

        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
        );






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
                                //タイマーが進んでいた場合にボタンを押すとタイマーが止まる。
                                if (mTimerSec == 0) {
                                    //indexからIDを習得し、そのIDから画像のURIを習得する
                                    if (cursor.moveToNext() == true) {
                                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = cursor.getLong(fieldIndex);
                                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                        //ImageViewで表示を依頼する
                                        ImageView imageView = (ImageView) findViewById(R.id.image);
                                        imageView.setImageURI(imageUri);
                                    } else {
                                        cursor.moveToFirst();
                                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                                        Long id = cursor.getLong(fieldIndex);
                                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                        //ImageViewで表示を依頼する
                                        ImageView imageView = (ImageView) findViewById(R.id.image);
                                        imageView.setImageURI(imageUri);
                                    }
                                }else if(mTimer != null){
                                    mTimer.cancel();
                                    mTimer = null;
                                }
                            }

                        });
                    }
                },2000,2000);

            }

        });


        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ボタンを押すと進む。スライドショー中はボタンを無効化
                if(mTimerSec ==0 ) {
                    if (cursor.moveToNext() == true) {
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        //ImageViewで表示を依頼する
                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageURI(imageUri);
                    } else {
                        cursor.moveToFirst();
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        //ImageViewで表示を依頼する
                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageURI(imageUri);
                    }
                }else{
                    mForwardButton.setEnabled(false);
                }
            }
        });

        mReverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ボタンを押すと戻る。スライドショー中は無効化
                if (mTimerSec < 0) {
                    if (cursor.moveToPrevious() == true) {
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        //ImageViewで表示を依頼する
                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageURI(imageUri);
                    } else {
                        cursor.moveToFirst();
                        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                        Long id = cursor.getLong(fieldIndex);
                        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        //ImageViewで表示を依頼する
                        ImageView imageView = (ImageView) findViewById(R.id.image);
                        imageView.setImageURI(imageUri);
                    }
                } else {
                    mReverseButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        cursor.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

}
