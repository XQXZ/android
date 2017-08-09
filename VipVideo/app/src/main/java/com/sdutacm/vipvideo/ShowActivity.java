package com.sdutacm.vipvideo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;

public class ShowActivity extends AppCompatActivity {

    private boolean isStartVideoList = false; //判断是否启动标志
    private Handler handler = new Handler();
    @Override
    protected void onCreate( Bundle savedInstanceState) {         //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        //启动线程 让里面的方法运行2秒后关闭
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startVideList();//启动方法
            }
        }, 3000);
    }

    private void startVideList() {

        if(!isStartVideoList){//判断是否第一次运行
            isStartVideoList = true;
            Intent intent = new Intent(this,MainActivity.class);//转到MainActivity页面
            startActivity(intent);
            //关闭当前的启动页面
            finish();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //当触摸屏幕的时候 就会立马进入这个方法
        startVideList();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        //把所有的消息和回调移除
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}

