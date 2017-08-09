package com.sdutacm.vipvideo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://yun.meik.pw/index.php/sports");
        intent.setData(content_url);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://yun.meik.pw/index.php/sports");
        intent.setData(content_url);
        startActivity(intent);

    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();

    }
}
