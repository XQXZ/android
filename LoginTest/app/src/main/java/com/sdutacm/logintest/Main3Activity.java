package com.sdutacm.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Main3Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Button forceOffLine = (Button) findViewById(R.id.force_offonline);
        forceOffLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.wangdeqiang.www.chatwithrobot.BroadcastBestPractice.FORCE_OFFLINE");
                sendBroadcast(intent);
            }
        });
    }
}
