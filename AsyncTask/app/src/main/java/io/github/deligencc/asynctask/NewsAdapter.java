package io.github.deligencc.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by bummer on 2017/8/22.
 */

public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{

    private List<NewsBean> mList;

    private LayoutInflater mInflater;

    private ImageLoader mImageLoader;

    private int mStart,mEnd;

    public static String[] URLS;

    private boolean mFirstIn;


    public NewsAdapter(Context context, List<NewsBean> data, ListView listView) {
        this.mList = data;
        mInflater = LayoutInflater.from(context);
        mImageLoader = new ImageLoader(listView);
        URLS = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            URLS[i] = data.get(i).newsIconUrl;
            //获得data所有URL 转到静态数组中
        }
        mFirstIn = true;
        //注册事件接口
        listView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_layout,null);
           viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
           // new ImageLoader().showImageByThread(viewHolder.ivIcon,mList.get(position).newsIconUrl);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        String url = mList.get(position).newsIconUrl;
        viewHolder.ivIcon.setTag(url);//作为身份标识
       // new ImageLoader().showImageByThread(viewHolder.ivIcon,mList.get(position).newsIconUrl);
//        mImageLoader.showImageByAsyncTask(viewHolder.ivIcon,url);
        //如果缓存中已经存在则设置缓存图片
        Bitmap bitmap = mImageLoader.getBitmapFromCache(url);
        if(bitmap != null){
            viewHolder.ivIcon.setImageBitmap(bitmap);
        }else {
            viewHolder.ivIcon.setImageResource(R.mipmap.ic_launcher);
        }
        viewHolder.tvTitle.setText(mList.get(position).newsTitle);
        viewHolder.tvContent.setText(mList.get(position).newsContent);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //状态改变时会调用
        if(scrollState == SCROLL_STATE_IDLE){
            //加载可见项
            mImageLoader.loadImages(mStart,mEnd);
        }else {
            //停止所有的加载任务
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
      //整个滑动中都会被调用
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem+visibleItemCount;
        if(mFirstIn && visibleItemCount > 0){
            //当前列表第一次显示， item已经显示出来
            mImageLoader.loadImages(mStart,mEnd);
            mFirstIn = false;
        }
    }

    class ViewHolder{
        public TextView tvTitle,tvContent;
        public ImageView ivIcon;
    }
}
