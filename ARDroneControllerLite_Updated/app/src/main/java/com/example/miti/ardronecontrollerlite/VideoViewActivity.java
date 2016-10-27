package com.example.miti.ardronecontrollerlite;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.miti.ardronecontrollerlite.mjpeg.MjpegInputStream;
import com.example.miti.ardronecontrollerlite.mjpeg.MjpegView;


public class VideoViewActivity extends ActionBarActivity {

    private static final String TAG = "Video View" ;
    MjpegView videoView;
    ProgressDialog pDialog;

    String videoURL = "http://webcam.st-malo.com/axis-cgi/mjpg/video.cgi?resolution=352x288";  //<-  URL FOR THE DRONE'S MJPEG STREAM

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoURL = "http://192.168.1.4:3002/nodecopter.mjpeg";


        MjpegInputStream mjp = new MjpegInputStream(null);
        mjp = mjp.read(videoURL);


        Log.i(TAG, "stream created");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        videoView = new MjpegView(this);
        videoView.setSource(mjp);
        videoView.setDisplayMode(MjpegView.SIZE_BEST_FIT);
        videoView.startPlayback();
        videoView.showFps(true);

    }


    public void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }

}
