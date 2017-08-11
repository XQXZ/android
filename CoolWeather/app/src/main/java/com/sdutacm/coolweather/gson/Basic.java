package com.sdutacm.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bummer on 2017/8/11.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }
}
