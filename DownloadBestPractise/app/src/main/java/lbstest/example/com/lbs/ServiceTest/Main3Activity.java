package lbstest.example.com.lbs.ServiceTest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import lbstest.example.com.lbs.R;

public class Main3Activity extends AppCompatActivity implements View.OnClickListener {

    private MyService.DownloadBinder downloadBinder;
    //创建一个ServiceConnection匿名类

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (MyService.DownloadBinder) service;
            downloadBinder.startDownload();
            downloadBinder.getProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Button bindService = (Button) findViewById(R.id.bing_service);
        Button unbindService = (Button) findViewById(R.id.unbing_service);
        Button startService = (Button) findViewById(R.id.start_service);
        Button stopService = (Button) findViewById(R.id.stop_service);
        Button start_intent_service = (Button) findViewById(R.id.start_intent_service);
        start_intent_service.setOnClickListener(this);
        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);
        bindService.setOnClickListener(this);
        unbindService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bing_service:
                Intent bindIntent = new Intent(this, MyService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE); //绑定服务
                break;
            case R.id.unbing_service:
                unbindService(connection); //解绑服务
                break;
            case R.id.start_service:
                Intent startIntent = new Intent(this, MyService.class);
                startService(startIntent);//启动服务
                break;
            case R.id.stop_service:
                Intent stopIntent = new Intent(this,MyService.class);
                stopService(stopIntent);//停止服务
                break;
            case R.id.start_intent_service:
                //打印主线程id
                Log.d("MyIntentService","Thread is "+Thread.currentThread().getId());
                Intent intentService = new Intent(this, MyIntentService.class);
                startService(intentService);
                break;
            default:
                break;
        }
    }
}
