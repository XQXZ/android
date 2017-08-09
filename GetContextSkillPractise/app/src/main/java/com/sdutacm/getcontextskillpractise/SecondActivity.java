package com.sdutacm.getcontextskillpractise;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by bummer on 2017/8/9.
 */

public class SecondActivity extends AppCompatActivity{
   public static String TAG = "SecondActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Person person = (Person) getIntent().getParcelableExtra("person_data");
        LogUtil.d(TAG,"person name is "+person.getName());
        LogUtil.d(TAG,"person age is "+person.getAge());

    }
}
