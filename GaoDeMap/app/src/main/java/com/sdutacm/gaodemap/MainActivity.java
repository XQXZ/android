package com.sdutacm.gaodemap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.MyLocationStyle;

public class MainActivity extends AppCompatActivity implements LocationSource,AMapLocationListener{

    private MapView mapView;

    private AMap aMap;

    private OnLocationChangedListener listener;

    private AMapLocationClient locationClient;

    private AMapLocationClientOption locationOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        if(aMap == null){
            aMap = mapView.getMap();
            setUpMap();
        }
    }
    //设置amap的属性
    private void setUpMap() {
        //自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //设置小蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_maker));
        //设置圆形的边框颜色
        myLocationStyle.strokeColor(Color.BLACK);
        //设置图形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(100,0,0,180));
        //设置圆形的边框粗细
        myLocationStyle.strokeWidth(1.0f);
        aMap.setMyLocationStyle(myLocationStyle);
        //设置监听
        aMap.setLocationSource(this);
        //设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        //设置true表示显示定位层并可触发定位,flase表示隐藏定位层并不可触发定位,默认是false
        aMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
if(listener!=null && aMapLocation != null){
    if(aMapLocation!=null
            && aMapLocation.getErrorCode()==0){
        listener.onLocationChanged(aMapLocation); //显示系统小蓝点
    }else {
        String errText = "定位失败,"+aMapLocation.getErrorCode()+":"+aMapLocation.getErrorInfo();
        Log.d("AmapErr",errText);
    }
}
    }
//激活定位
    @Override
    public void activate(OnLocationChangedListener listener) {
        this.listener = listener;
        if(locationClient == null){
            locationClient = new AMapLocationClient(this);
            locationOption = new AMapLocationClientOption();
            //设置定位监听
            locationClient.setLocationListener(this);
            //设置高精度定位模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            locationClient.setLocationOption(locationOption);
            /**
             * 此方法为每个一段固定时间发起一次定位请求,为了减少电量消耗和网络流量消耗
             * 注意设置合适的时间间隔(支持最小间隔2000ms)
             * 定位结束后,在适合的周期调用onDestory()方法
             * 在单次定位情况下,定位无论成功与否,都无需调用stopLocation()方法移除请求
             * 定位sdk会内部移除
             */
            locationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */

    @Override
    public void deactivate() {
        listener = null;
        if(locationClient != null){
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }
}
