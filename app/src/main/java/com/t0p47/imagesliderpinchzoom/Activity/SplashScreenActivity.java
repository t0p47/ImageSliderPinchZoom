package com.t0p47.imagesliderpinchzoom.Activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.VideoView;

import com.t0p47.imagesliderpinchzoom.R;

public class SplashScreenActivity extends Activity {

    private static final String TAG = "LOG_TAG";
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        try{

            videoView = (VideoView) findViewById(R.id.videoView);

            Uri path = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.splash_video);
            videoView.setVideoURI(path);

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    GoToMainScreen();
                }
            });
            videoView.start();
        }catch(Exception e){
            e.printStackTrace();
            Log.d(TAG,"SplashScreenActivity: no video, set picture logo");
        }

    }

    private void GoToMainScreen(){
        startActivity(new Intent(this, GridViewActivity.class));
        finish();
    }


}
