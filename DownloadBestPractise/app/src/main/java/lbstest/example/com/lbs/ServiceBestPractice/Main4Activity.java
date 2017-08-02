package lbstest.example.com.lbs.ServiceBestPractice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import lbstest.example.com.lbs.R;

/**
 * 功能：完整版的下载实例
 */
public class Main4Activity extends AppCompatActivity implements View.OnClickListener {

    private static boolean Justice = false;

    EditText urlWeb = null;

    private DownloadService.DownloadBinder downloadBinder;
//先创建一个SerViceConnection 的匿名类，
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
          //  onServiceConnection()方法中获得DownloadBinder的实例
            //有了这个实例，可以在活动中调用服务提供的各种方法
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Button startDownload = (Button) findViewById(R.id.start_download);
        Button pauseDownload = (Button) findViewById(R.id.pause_download);
        Button cancelDownload = (Button) findViewById(R.id.cancel_download);
         urlWeb = (EditText) findViewById(R.id.webUrl);
        startDownload.setOnClickListener(this);
        pauseDownload.setOnClickListener(this);
        cancelDownload.setOnClickListener(this);
        Intent intent = new Intent(this,DownloadService.class);
        //启动和绑定服务
        startService(intent); //启动服务可以保证DownloadService一直在后台运行
        bindService(intent,connection,BIND_AUTO_CREATE); //绑定服务  让Main4Activity和DownloadService进行通信
        if(ContextCompat.checkSelfPermission(Main4Activity.this, Manifest
        .permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Main4Activity.this,new
            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onClick(View v) {
        if(downloadBinder == null){
            return;
        }
        switch (v.getId()){
            case R.id.start_download:
                String url = null;
                    if(!urlWeb.getText().toString().equals("")){
                    Log.d("XXX","urlWeb.get is"+urlWeb.getText().toString());
                    url = urlWeb.getText().toString();
                    downloadBinder.startDownload(url);
                    Justice = true;
                }else {
                    Justice = false;
                    Toast.makeText(Main4Activity.this,"请先填写要下载的网址!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pause_download:
                if(Justice){
                    downloadBinder.pauseDownload();
                }else {
                    Toast.makeText(Main4Activity.this,"请先填写要下载的网址!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel_download:
                if(Justice){
                    downloadBinder.cancelDownload();
                }else {
                    Toast.makeText(Main4Activity.this,"请先填写要下载的网址!",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"拒绝权限将无法使用程序",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
