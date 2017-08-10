package com.sdutacm.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**功能: 向服务器发送请求
 * Created by bummer on 2017/8/10.
 */

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
