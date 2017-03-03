package kr.tangomike.sima_20170304_kik_d;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;

/**
 * Created by shimaz on 2017-02-24.
 */

public class MediaActivity extends Activity implements Runnable{

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            finish();

        }
    };

    private IntentFilter mFilter = new IntentFilter("shimaz.restart");

    private Button btnClose;
    private Button btnPlayPause;
    private Button btnVideo1;
    private Button btnVideo2;

    private DataCollection dc;

    private SeekBar sbVideo;
    private SeekBar sbAudio;


//    private ImageView ivTitle;

    private VideoView vv;
    private AudioManager am;

    private int videoNumber;

    private Thread videoThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        super.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        dc = (DataCollection)getApplicationContext();
//        dc.startTick();

        registerReceiver(mReceiver, mFilter);

        videoNumber = 1;

        btnClose = (Button)findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);

            }
        });

        btnPlayPause = (Button)findViewById(R.id.btn_play_pause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(vv.isPlaying()){
                    btnPlayPause.setBackgroundResource(R.drawable.media_btn_play);
                    vv.pause();
                    dc.startTick();

                }else{
                    btnPlayPause.setBackgroundResource(R.drawable.media_btn_pause);
                    vv.start();
                    dc.stopTick();

                }


            }
        });

        btnVideo1 = (Button)findViewById(R.id.btn_video1);
        btnVideo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoNumber == 2){
                    setVideo(1);
                }

            }
        });


        btnVideo2 = (Button)findViewById(R.id.btn_video2);
        btnVideo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoNumber == 1){
                    setVideo(2);
                }

            }
        });


        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        sbAudio = (SeekBar)findViewById(R.id.sb_audio);
        sbAudio.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbAudio.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
        sbAudio.setOnSeekBarChangeListener(onSeekVolume);
        sbAudio.setThumb(ResourcesCompat.getDrawable(getResources(), R.drawable.media_btn_thumb_audio, null));

        sbAudio.setProgress(7);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 7, 0);



        sbVideo = (SeekBar)findViewById(R.id.sb_video);
        sbVideo.setOnSeekBarChangeListener(onSeekVideo);
        sbVideo.setThumb(ResourcesCompat.getDrawable(getResources(), R.drawable.media_btn_thumb_video, null));



        vv = (VideoView)findViewById(R.id.vv_media);

//        ivTitle = (ImageView)findViewById(R.id.iv_video_title);
        setVideo(1);
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                setupLeftovers();
                dc.stopTick();
                vv.start();
                btnPlayPause.setBackgroundResource(R.drawable.media_btn_pause);
            }
        });

        vv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });

        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                sbVideo.setProgress(0);
                btnPlayPause.setBackgroundResource(R.drawable.media_btn_play);
                dc.startTick();
            }
        });

//        vv.start();



    }

    private SeekBar.OnSeekBarChangeListener onSeekVolume = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

            android.util.Log.i("shimaz", "" + progress);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekVideo = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) vv.seekTo(progress);
            sbVideo.setProgress(progress);
//            nowTime.setText(getTimeString(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            if(vv.isPlaying()) vv.pause();

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            vv.start();
        }
    };


    private void setVideo(int number){
        videoNumber = number;

//        ivTitle.setBackgroundResource(getResources().getIdentifier("media_img_title_" + number, "drawable", getPackageName()));

//        String path = "android.resource://" + getPackageName() + "/raw/media_mov_" + number;

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/video_d_" + number + ".mp4";

        if(vv.isPlaying()) vv.stopPlayback();
        vv.setVideoURI(Uri.parse(path));

        if(number == 1){

            btnVideo1.setBackgroundResource(R.drawable.media_btn_1_push);
            btnVideo2.setBackgroundResource(R.drawable.media_btn_video_2);
        }else{
            btnVideo1.setBackgroundResource(R.drawable.media_btn_video_1);
            btnVideo2.setBackgroundResource(R.drawable.media_btn_2_push);


        }


    }


    private void setupLeftovers(){

        sbVideo.setMax(vv.getDuration());
        videoThread = new Thread(MediaActivity.this);

        videoThread.start();

    }

    @Override
    public void run(){
        int current;
        while(vv != null)
        {
            try{
                Thread.sleep(1000);
                current = vv.getCurrentPosition();
                if(vv.isPlaying()){
                    dc.resetTimer();
                    sbVideo.setProgress(current);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy(){
        if(vv != null){
            if(vv.isPlaying()) vv.stopPlayback();
            vv = null;
        }

        unregisterReceiver(mReceiver);

        super.onDestroy();

    }
}

