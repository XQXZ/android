package lbstest.example.com.lbs.ServiceTest;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by bummer on 2017/7/26.
 */

public class MyIntentService extends IntentService {

    /**
     * 首先要提供一个无参构造函数，在其内部调用其父类的有参构造函数
     */
    public MyIntentService() {

        super("MyIntentService"); //调用父类的有参构造函数
        //打印当前线程的id
    }

    /**
     * 在抽象的onHandleIntent()这个方法中可以去处理一些具体的逻辑，不用担心ANR问题，
     * 因为这个方法已经是在子线程中运行的了
     * @param intent
     */
    @Override
    protected void onHandleIntent( Intent intent) {

        //打印当前线程的id
        Log.d("MyIntentService","Thread is "+Thread.currentThread().getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyIntentService","Thread is over");
    }
}
