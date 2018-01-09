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
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;


    Timer mTimer;
    Handler mHandler = new Handler();

    Cursor cursor;
    Uri imageUri;

    Button mPlayButton;
    Button mForwardButton;
    Button mReverseButton;

    ImageView imageView;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlayButton = findViewById(R.id.playButton);
        mForwardButton = findViewById(R.id.forwardButton);
        mReverseButton = findViewById(R.id.reverseButton);
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.textView);

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


        mPlayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (mTimer == null) {
                    mTimer = new Timer();

                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mForwardButton.setEnabled(false);
                                    mReverseButton.setEnabled(false);
                                    mPlayButton.setText("停止");
                                    textView.setText("スライドショー再生中");
                                    //mTimerSecが0の場合、スライドショーを開始する

                                    if (cursor.moveToNext() == true) {
                                        setImageView();
                                    } else {
                                        cursor.moveToFirst();
                                        setImageView();
                                        }
                                }

                            });
                        }
                    }, 0, 2000);

                }else{
                mTimer.cancel();
                mTimer = null;
                mForwardButton.setEnabled(true);
                mReverseButton.setEnabled(true);
                mPlayButton.setText("再生");
                textView.setText("スライドショー停止中");
            }
            }

        });


        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ボタンを押すと進む

                if (cursor.moveToNext() == true) {
                    setImageView();
                } else {
                    cursor.moveToFirst();
                    setImageView();
                }
            }
        });

        mReverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ボタンを押すと戻る

                    if (cursor.moveToPrevious() == true) {
                        setImageView();
                    } else {
                        cursor.moveToLast();
                        setImageView();
                    }
            }
        });
    }

    private void setImageView() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        //ImageViewで表示を依頼する
        imageView.setImageURI(imageUri);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        cursor.close();
    }


}
