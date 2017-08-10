package com.sdutacm.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by bummer on 2017/8/10.
 */

public class Province extends DataSupport{

    private int id;            //实体类都有的字段

    private String provinceName;  //省的名字

    private int provinceCode;  //省的代号

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
