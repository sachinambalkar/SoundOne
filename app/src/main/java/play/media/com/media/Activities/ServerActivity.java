package play.media.com.media.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import play.media.com.media.ClientThread;
import play.media.com.media.R;
import play.media.com.media.model.GroupModel;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ServerActivity extends AppCompatActivity implements  MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener
{
    ProgressDialog pDialog;
    Firebase myFirebaseRef;
    String groupName;
    String sonGName;
    int totalNumberOfCliets=1;
    int totalConnectedClient=0;
    boolean keep_listening=true;
    private SeekBar seekBarProgress;
    public TextView textViewSongURL;
    public  TextView textViewCount;
    private MediaPlayer mediaPlayer;
    private int mediaFileLengthInMilliseconds; // this value contains the song duration in milliseconds. Look at getDuration() method in MediaPlayer class

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_server);
        myFirebaseRef = new Firebase("https://soundone.firebaseio.com/");

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        textViewSongURL=(TextView)findViewById(R.id.textViewSongURL);
        textViewCount=(TextView)findViewById(R.id.tv_count);
        textViewCount.setText(totalConnectedClient+"");
        initView();
     mContentView.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         toggle();
     }
 });
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        Intent intent=getIntent();
        GroupModel groupModel =(GroupModel)intent.getSerializableExtra("DATA");

        TextView textView=(TextView) findViewById(R.id.tv_group_name_title);
        textView.setText(groupModel.getGroup_name());
        ((TextView)findViewById(R.id.tv_group_info_server)).setText("Server  address : "+groupModel.getIp_address()+" :"+groupModel.getPort_number());
        totalNumberOfCliets=intent.getIntExtra("CLIENT", 1);
        groupName=groupModel.getGroup_name();
        sonGName=groupModel.getSong_name();

        textViewSongURL.setText(sonGName);

        pDialog = ProgressDialog.show(ServerActivity.this, null, null, true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pDialog.getWindow().setGravity(Gravity.CENTER);
        pDialog.setContentView(R.layout.progressdialog);
        pDialog.show();



        MyClientTask myClientTask = new MyClientTask(
                groupModel.getIp_address() ,
                Integer.parseInt(groupModel.getPort_number()) );
        myClientTask.execute();


    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    public void delaySong(View v){
        mediaPlayer.seekTo(10);
    }

    public void startSong(View v){
        initView();
        try {
                mediaPlayer.setDataSource(sonGName); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            } catch (Exception e) {
            e.printStackTrace();
        }
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
        mediaPlayer.start();
        primarySeekBarProgressUpdater();

}

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        seekBarProgress.setSecondaryProgress(percent);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }



    private void initView() {
        seekBarProgress = (SeekBar) findViewById(R.id.SeekBarTestPlay);
        seekBarProgress.setMax(99); // It means 100% .0-99
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    /**
     * Method which updates the SeekBar primary progress by current song playing position
     */
    private void primarySeekBarProgressUpdater() {
        seekBarProgress.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaFileLengthInMilliseconds) * 100)); // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }



    public class MyClientTask extends AsyncTask<Void, ClientThread, Void> {

        String ip_Address;
        int port_Number;
        String response = "";

        MyClientTask(String addr, int port){
            ip_Address = addr;
            port_Number = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {


            try {
                mediaPlayer.setDataSource(sonGName); // setup song from http://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3 URL to mediaplayer data source
                mediaPlayer.prepare(); // you must call this method after setup the datasource in setDataSource method. After calling prepare() the instance of MediaPlayer starts load data from URL to internal buffer.
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL


            int count=0;
            Runnable cyclicAction=new Runnable() {
                @Override
                public void run() {
//                Toast.makeText(ServerActivity.this,"Playing song",Toast.LENGTH_LONG).show();
                }
            };
            final CyclicBarrier cyclicBarrier=new CyclicBarrier(totalNumberOfCliets+1,cyclicAction);
            try {
                    ServerSocket serverSocket=new ServerSocket(port_Number);
                    while(keep_listening&&count<totalNumberOfCliets){
                        final Socket socket=serverSocket.accept();
                       ClientThread clientThread = new ClientThread(cyclicBarrier, socket);
 //                       new Thread(clientThread).start();
                        publishProgress(clientThread);
                        count++;
                    }


            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                cyclicBarrier.await();
                mediaPlayer.start();
                primarySeekBarProgressUpdater();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(ClientThread... values) {
            super.onProgressUpdate(values);
            totalConnectedClient++;
            textViewCount.setText(totalConnectedClient+"");
            new Thread(values[0]).start();

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(ServerActivity.this, "Starting", Toast.LENGTH_SHORT).show();
            myFirebaseRef.child(groupName).removeValue();

            pDialog.dismiss();
        }

    }

}
