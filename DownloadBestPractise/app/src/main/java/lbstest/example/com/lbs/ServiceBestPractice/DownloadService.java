package lbstest.example.com.lbs.ServiceBestPractice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

import lbstest.example.com.lbs.R;

public class DownloadService extends Service {

    DownloadTask downloadTask;

    private String downloadUrl;

    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1,getNotification("Downloading...",
                    progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            //下载成功将前台服务通知关闭,并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",
                    -1));
            Toast.makeText(DownloadService.this,"Download Success",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
          downloadTask = null;
            //下载失败时将前台服务关闭,并创建一个下载失败的通知
            stopForeground(true);
            //触发通知，这样就可以在下拉状态栏中实时看到下载进度
            getNotificationManager().notify(1,getNotification("Download Failed",-1));
            Toast.makeText(DownloadService.this,"Download Failed",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this,"Download Pause",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this,"Download Canceled",Toast.LENGTH_SHORT).show();
        }
    };
    /**
     * 让DownloadService 可以和活动进行通信
     */
    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class DownloadBinder extends Binder{
        //用于开始下载
        public void startDownload(String Url){
            //创建一个downloadTask实例，
            if(downloadTask == null){
                downloadUrl = Url;
                downloadTask = new DownloadTask(listener); //把刚才的DownloadListener作为参数传入
                downloadTask.execute(downloadUrl);
                //让服务成为前台服务 ，在状态栏创建一个持续运行的通知
                startForeground(1,getNotification("Downloading......",0));
                Toast.makeText(DownloadService.this,"Downloading......",Toast.LENGTH_SHORT).show();

            }
        }
        //用于暂停下载
        public void pauseDownload(){
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }
        //用于取消下载
        public void cancelDownload(){
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            }else {
                //取消下载将正在下载的文件删除掉
                if (downloadUrl != null) {
                    //取消下载时需将文件删除，并将通知进行关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if(file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this,"Canceled",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private Notification getNotification(String title, int progress){
        Intent intent = new Intent(this,Main4Activity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if(progress > 0){
            //当progress 大于或等于0时才需要显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }



    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    };





}
