package com.sdutacm.materialtest;

/**
 * Created by bummer on 2017/8/9.
 */

public class Fruit {
    private String name; //水果的名字

    private int imageId; //水果图片资源对应的Id

    public Fruit(String name,int imageId){
        this.name = name;
        this.imageId = imageId;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
