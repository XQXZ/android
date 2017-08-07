package com.sdutacm.download;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bummer on 2017/7/26.
 */

/**
 * params AsyncTask 中的三个泛型参数
 * String 需要传入一个字符串给给后台任务
 * Integer 整型数据表示进度显示的单位
 * Integer 整型数据反馈执行的结果
 */
public class DownloadTask extends AsyncTask <String,Integer,Integer> {

    public final static int TYPE_SUCCESS = 0; //表示下载成功
    public final static int TYPE_FAILED = 1;  //表示下载失败
    public final static int TYPE_PAUSED = 2;  //表示下载暂停
    public final static int TYPE_CANCELED = 3;//表示下载取消

    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;
    //在DownloadTask的构造函数中传入一个刚定义的DownloadListener参数,我们待会会将下载的状态通过这个状态进行回调
    public DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            intent.putExtra("progress",msg.what);
        }
    };

    /**
     *   功能:后台执行的具体的下载逻辑
     *
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;

        try {
            long downloadLength = 0; //记录下载文件长度
//            从参数中获得了下载的URL地址
            String downloadUrl = params[0];
//            并根据URL地址解析出了下载的文件名
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
//            将指定的文件下载到Environment.DIRECTORY_DOWNLOADS目录,也就是SD卡的Download目录
            String directory = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);
            Log.d("File","file is"+file+"  "+"directory is "+directory+"  "+"fileName is"+fileName);
            //判断Download中是否已经存在要下载的文件了
            if (file.exists()){
                //如果存在则读取已下载的字节数 这样后面就能实现断点续传的功能
                downloadLength = file.length();
            }
            //调用getContentLength()方法获得文件的总长度
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0){
                //如果文件长度为0,说明文件有问题
                return TYPE_FAILED;
            }else if (contentLength == downloadLength){
                //已下载字节和文件总字节相等，说明已经下载完成了
                return TYPE_SUCCESS;
            }
            //使用OKhttp发送网络请求
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    //断点下载，指定从哪个字节开始下载
                    .addHeader("RANGE","bytes="+downloadLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            //读取服务器响应的数据,并使用java的IO流的方式,不断从网络上读取数据,不断写入到本地
            if(response != null){
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file,"rw");
                savedFile.seek(downloadLength); //跳过已下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b))!= -1){
                    if(isCanceled){ //判断用户有没有触发取消按钮
                        return TYPE_CANCELED;
                    }else if (isPaused){ //判断你用户有没有触发暂停按钮
                        return TYPE_PAUSED;
                    }else {
                        total += len;
                        savedFile.write(b,0,len);
                        //计算已下载的百分比
                        int progress = (int)((total + downloadLength)*100/contentLength);
                        Log.d("XXX","the download radio is the "+progress);
                        Log.d("XXX","the contentLength  is the "+contentLength);

                        //调用publicProgress方法进行通知
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            try {
                if (is != null) {
                    is.close();
                }
                if(savedFile != null){
                    savedFile.close();
                }
                if(isCanceled && file != null){
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return TYPE_FAILED;
    }

    /**
     * 功能:界面上更新当前的下载逻辑
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        //从参数中获得当前下载进度
        int progress = values[0];
        if(progress > lastProgress){
            //和上一次进度进行对比,如果有变化

            //调用listener.onProgress通知下载进度进行更新
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    /**
     *  功能:用于通知最后的下载结果
     * @param status
     */

    @Override
    protected void onPostExecute(Integer status) {
        //将参数中传入的下载状态进行回调,下载成功就调用DownloadListener.onSuccess()方法,
        // 取消下载就调用onCanceled()方法
        switch (status){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }
    }
    public void pauseDownload(){
        isPaused = true;
    }
    public void cancelDownload(){
        isCanceled = true;
    }
    private long getContentLength(String downloadUrl) throws Exception{

        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(new URI(downloadUrl));
        HttpResponse response = client.execute(httpGet);
        HttpEntity entity  = response.getEntity();
        client.getConnectionManager().shutdown();
        return  entity.getContentLength();
    }
}