package kr.tangomike.sima_20170304_kik_d;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * Created by shimaz on 2017-02-24.
 */

public class DataCollection extends Application {

    private int ticktime;
    private static final int RESET_TIME = 60;
    private boolean isTicking;

    private Handler mHandler;

    public void onCreate(){
        super.onCreate();
        ticktime = 0;
        isTicking = false;

        // TODO: Use broadcast for timer function

        mHandler = new Handler(){
            public void handleMessage(Message msg){
                if(isTicking) ticktime++;

                if(ticktime < RESET_TIME){
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                    android.util.Log.i("shimaz", "" + ticktime);
                }else if(ticktime >= RESET_TIME){
                    ticktime = 0;
                    isTicking = false;
                    mHandler.removeMessages(0);

                    Intent intent  = new Intent("shimaz.restart");
                    sendBroadcast(intent);
                }

            }


        };
    }




//    public int getTicktime(){
//        return ticktime;
//    }

    public void resetTimer(){
        ticktime = 0;
    }

    public void stopTick(){
        isTicking = false;
        mHandler.removeMessages(0);
    }

    public void startTick(){
        ticktime = 0;
        isTicking = true;
        mHandler.sendEmptyMessageDelayed(0, 1000);

    }

}
