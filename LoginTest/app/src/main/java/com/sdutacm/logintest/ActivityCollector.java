package com.sdutacm.logintest;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 王德强
 * 功能 : 管理所有的活动
 * 强制下线功能需要关闭所有的活动,然后回到登录界面
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    //关闭所有活动
    public static void finishAll(){
        for (Activity activity: activities) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}