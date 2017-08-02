package lbstest.example.com.lbs.ServiceTest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import lbstest.example.com.lbs.R;

/**
 * Created by bummer on 2017/7/26.
 */

public class MyService extends Service {

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyService","onCreate executed");
        Intent intent = new Intent(this,Main3Activity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("This is content title")
                .setContentText("This is content text")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();
        startForeground(1,notification); //将MyService变成一个前台服务，并在系统状态栏显示出来
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "onStartCommand executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyService", "onDestroy executed");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DownloadBinder extends Binder {
        public void startDownload() {
            Log.d("Myservice", "startDownload executed");
        }

        public int getProgress() {
            Log.d("MyService", "getProgress");
            return 0;
        }

        public IBinder onBind(Intent intent) {
            return mBinder;
        }

    }


}
