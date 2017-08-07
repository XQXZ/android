package com.sdutacm.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Main2Activity extends AppCompatActivity {

    private NotificationCompat.Builder builder;
    private Notification notification;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        normalNotification("自定义通知","hello World");
    }

    private void normalNotification(String title, String content) {
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        setDown();
    }

    private void setDown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<=100; i++){
                    builder.setProgress(100,i,false);
                    openNotification();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //下载完成

                    builder.setContentText("下载完成").setProgress(0,0,false);
                    openNotification();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //下载完成后,等待5秒,关闭通知
                    notificationManager.cancel(0);

                }
            }
        }).start();
    }

    private void openNotification() {
        if(Build.VERSION.SDK_INT > 16){
            notification = builder.build();
        }else {
            notification = builder.getNotification();
        }

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);
    }
}
