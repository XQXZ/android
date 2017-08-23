package io.github.deligencc.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bummer on 2017/8/23.
 */

public class ImageLoader {

    private ImageView mImageView;

    private String mUrl;

    //创建Cache
    private LruCache<String, Bitmap> mCaches;//需要保存缓存对象的名字,保存对象  本质是map

    private ListView mListView;

    private Set<NewsAsyncTask> mTasks;

    public ImageLoader(ListView listView) {
        mListView = listView;
        mTasks = new HashSet<>();
        //获取最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCaches = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回图片大小，每次存入缓存时调用
                return value.getByteCount(); //将bitmap的实际大小传入
            }
        }; //初始化缓存大小
    }

    /**
     * 将内容保存的Cache
     */
    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) { //判断当前缓存是否存在
            mCaches.put(url, bitmap);
        }
    }

    /**
     * 从Cache中读取内容
     */
    public Bitmap getBitmapFromCache(String url) {
        //从缓存中获取数据
        return mCaches.get(url);
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl)) {
                mImageView.setImageBitmap((Bitmap) msg.obj);
            }
        }
    };

    public void showImageByThread(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getBitmapFromUrl(url);
                Message message = Message.obtain(); //通过这种方式创建的message,可以使用现有的,已经回收到的message,提高message的使用效率
                message.obj = bitmap;
                mhandler.sendMessage(message);

            }
        }).start();
    }

    public Bitmap getBitmapFromUrl(String urlstring) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlstring);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
//            Thread.sleep(1000);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void showImageByAsyncTask(ImageView imageView, String url) {
        //先判断缓存中是否存在数据，减少下载时间  从缓存中取出对应的图片
        Bitmap bitmap = getBitmapFromCache(url);
        if (bitmap == null) { //如果缓存中没有，必须从网络下载
            //new NewsAsyncTask(url).execute(url);
            imageView.setImageResource(R.mipmap.ic_launcher);
        } else { //直接从内存中获取 并设置
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * 用来加载从start到end所有图片
     * @param start
     * @param end
     */
    public void loadImages(int start, int end) {
        for (int i = start; i < end; i++) {
            //从缓存中取出从start到end位置对应的图片
            String url = NewsAdapter.URLS[i];
            Bitmap bitmap = getBitmapFromCache(url);

            //如果缓存中没有，那么必须去下载
            if (bitmap == null) {
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mTasks.add(task);

            } else {
                ImageView imageView = (ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 取消当前正在取消的任务
     */
    public void cancelAllTasks() {
        if (mTasks != null) {
            for (NewsAsyncTask task : mTasks) {
               task.cancel(true);
            }
        }
    }

    private class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView mImageView;
        private String mUrl;

        public NewsAsyncTask(String url) {
            //  mImageView = imageView;
            mUrl = url;
        }


        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            //将下载的图片保存缓存中 从网络获取图片
            Bitmap bitmap = getBitmapFromUrl(url);
            if (bitmap != null) {
                //将不再缓存的图片加入缓存
                addBitmapToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            if (mImageView.getTag().equals(mUrl)) {
//                mImageView.setImageBitmap(bitmap);
//            }
            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
            mTasks.remove(this);
        }
    }
}
