package com.sdutacm.progressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

/**
 * 1.自定义属性的声明
 */

public class MainActivity extends AppCompatActivity {
    private HorizontalProgressWithProgressBar mHProgressBar;
    private RoundProgressBarWithProgress mRBarWithProgress;
    private static final int MESG_UPDATE = 0X110;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int progress = mHProgressBar.getProgress();
            mHProgressBar.setProgress(++progress);
            mRBarWithProgress.setProgress(++progress);
            if(progress >= 100){
                handler.removeMessages(MESG_UPDATE);
            }
            handler.sendEmptyMessageDelayed(MESG_UPDATE,100);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mHProgressBar = (HorizontalProgressWithProgressBar) findViewById(R.id.id_progress01);

        mRBarWithProgress = (RoundProgressBarWithProgress) findViewById(R.id.id_progress02);

        handler.sendEmptyMessage(MESG_UPDATE);


    }

}



