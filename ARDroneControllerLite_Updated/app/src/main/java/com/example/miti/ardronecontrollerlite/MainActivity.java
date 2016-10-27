package com.example.miti.ardronecontrollerlite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import com.example.miti.ardronecontrollerlite.JoyStickClass;
import com.example.miti.ardronecontrollerlite.JoyStickClass.OnJoystickMoveListener;
import com.example.miti.ardronecontrollerlite.mjpeg.MjpegInputStream;
import com.example.miti.ardronecontrollerlite.mjpeg.MjpegView;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    MjpegView videoView;
    ProgressDialog pDialog;

    String videoURL = "http://webcam.st-malo.com/axis-cgi/mjpg/video.cgi?resolution=352x288";  //<-  URL FOR THE DRONE'S MJPEG STREAM

    Button b;
    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    DataInputStream dataInputStream = null;
    String serverIPAdress = "192.168.1.2";
    int serverPort = 3001;
    byte[] response = new byte[256];
    private JoyStickClass joystick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.TakeoffButton);
        b = (Button)findViewById(R.id.my_button);
        joystick = (JoyStickClass) findViewById(R.id.joystickclass);

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    socket = new Socket(serverIPAdress, serverPort);
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();


        videoURL = "http://192.168.1.2:3002/nodecopter.mjpeg";


            MjpegInputStream mjp = new MjpegInputStream(null);
            mjp = mjp.read(videoURL);


            Log.i(TAG, "stream created");
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            videoView = (MjpegView) findViewById(R.id.VideoStream);
            videoView.setSource(mjp);
            videoView.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            videoView.startPlayback();
            videoView.showFps(true);


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    onCommandClick();
                    joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

                        @Override
                        public void onValueChanged(double xPos, double yPos, double cenX, double cenY,double buttonRadius) {
                            // TODO Auto-generated method stub
                            /*
                            switch (direction) {
                                case JoyStickClass.FRONT:
                                    onFrontClick();
                                    break;
                                case JoyStickClass.FRONT_RIGHT:
                                    onFrontClick();
                                    onLeftClick();
                                    break;
                                case JoyStickClass.RIGHT:
                                    onLeftClick();
                                    break;
                                case JoyStickClass.RIGHT_BOTTOM:
                                    onLeftClick();
                                    onBackClick();
                                    break;
                                case JoyStickClass.BOTTOM:
                                    onBackClick();
                                    break;
                                case JoyStickClass.BOTTOM_LEFT:
                                    onBackClick();
                                    onRightClick();
                                    break;
                                case JoyStickClass.LEFT:
                                    onRightClick();
                                    break;
                                case JoyStickClass.LEFT_FRONT:
                                    onRightClick();
                                    onFrontClick();
                                    break;
                                default:
                                }
                               */
                            Log.i("xMain", String.valueOf(cenX));
                            Log.i("yMain", String.valueOf(cenY));
                            Log.i("xMain", String.valueOf(xPos));
                            Log.i("yMain", String.valueOf(yPos));


                            if (xPos==cenX && yPos==cenY)
                                new CommandWorkerThread("[\"stop\",[],1]\n").start();
                            if (xPos<(cenX-buttonRadius))
                                onLeftClick();
                            if (xPos>(cenX+buttonRadius))
                                onRightClick();

                            if (yPos>(cenY+buttonRadius))
                                onBackClick();
                            if (yPos<(cenY-buttonRadius))
                                onFrontClick();

                        }
                    });
                            //, JoyStickClass.DEFAULT_LOOP_INTERVAL);

                } else {
                    // The toggle is disabled
                    joystick.setOnJoystickMoveListener(null);
                    landCommandClick();

                }
            }
        });


    }

    public void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }

    public void onCommandClick(){
        Log.i(TAG, "sending takeoff request");
        new CommandWorkerThread("[\"takeoff\",[],1]\n").start();
    }

    public void onLeftClick(){
        new CommandWorkerThread("[\"left\",[0.1],2]\n").start();
    }

    public void onClockwiseRotateClick(View v){
        new CommandWorkerThread("[\"clockwise\",[0.3],2]\n").start();
    }

    public void onCounterClockwiseRotateClick(View v){
        new CommandWorkerThread("[\"counterClockwise\",[0.3],2]\n").start();
    }

    public void onRightClick(){
        new CommandWorkerThread("[\"right\",[0.1],2]\n").start();
    }

    public void landCommandClick(){
        new CommandWorkerThread("[\"land\",[],1]\n").start();
    }

    public void onCalibrateDroneClick(View v){
        new CommandWorkerThread("[\"calibrate\",[],1]\n").start();
    }

    public void onUpClick(View v){
        new CommandWorkerThread("[\"up\",[0.1],2]\n").start();

    }

    public void onDownClick(View v){
        new CommandWorkerThread("[\"down\",[0.1],1]\n").start();
    }

    public void onFrontClick(){
        new CommandWorkerThread("[\"front\",[0.1],2]\n").start();

    }

    public void onBackClick(){
        new CommandWorkerThread("[\"back\",[0.1],2]\n").start();

    }

    public void onStopClick(View v){
        new CommandWorkerThread("[\"stop\",[],1]\n").start();
    }

    public void onTestVideoStreamClick(View v){
        Intent i = new Intent(this, VideoViewActivity.class);
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (dataInputStream != null) {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CommandWorkerThread extends Thread{

        private String _command="";

        public CommandWorkerThread(String command){
            _command = command;
        }

        @Override
        public void run(){
            try {
                Log.i(TAG, "sending request");
                dataOutputStream.writeBytes(_command);
                Log.i(TAG, _command);
                dataInputStream.readFully(response);
                dataOutputStream.flush();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
