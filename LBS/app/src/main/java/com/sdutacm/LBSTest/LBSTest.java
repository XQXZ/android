package com.sdutacm.LBSTest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.sdutacm.download.R;

import java.util.ArrayList;
import java.util.List;

public class LBSTest extends AppCompatActivity {

    public LocationClient mLocationClient;

    private TextView positionText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //首先创建了一个LocationClient的实例 ,在这个实例中的构造参数传入了一个Context参数,
        // 在这里调用getApplicationContext()方法获取一个全局的Context参数并传入
        mLocationClient = new LocationClient(getApplicationContext());
        //注册一个监听器,获取位置信息的时候就会回调这个监听器
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_lbstest);
        positionText = (TextView) findViewById(R.id.position_text_view);

        //创建List集合,以此判断三个权限有没有授权,如果没有授权就添加到List集合中,最后将List转化成数组,再调用ActivityCompat.requestPermissions()方法一次性申请
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LBSTest.this,permissions,1);
        }else {
            requestLocation();
        }

    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start(); //调用LocationClient的start方法就能开始定位了
        //定位结果会回调到我们前面注册的监听器中,也就是MyLocationListener.
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
//        //强制指定只使用GPS进行定位
//        Log.d("currentPosition","the progress is running here1");
//        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        Log.d("currentPosition","the progress is running here");
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
//将每个权限进行判断,如果有一个权限被拒绝,直接调用finish() 关闭当前程序
        switch (requestCode){
            case 1:
                if(grantResults.length > 0){
                    for (int result : grantResults){
                        if(result != PackageManager.PERMISSION_DENIED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation(); //当所有权限都同意了,调用requestLocation方法开始地理位置定位
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度: ").append(bdLocation.getLatitude())
            .append("\n");
            Log.d("currentPosition","currentPosition is the "+currentPosition);
            currentPosition.append("经度: ").append(bdLocation.getLongitude())
            .append("\n");
            currentPosition.append("国家: ").append(bdLocation.getCountry()).append("\n");
            currentPosition.append("省: ").append(bdLocation.getProvince()).append("\n");
            currentPosition.append("市: ").append(bdLocation.getCity()).append("\n");
            currentPosition.append("区: ").append(bdLocation.getDirection()).append("\n");
            currentPosition.append("街道: ").append(bdLocation.getStreet()).append("\n");
            Log.d("currentPosition","currentPosition is the "+currentPosition);
            currentPosition.append("定位方式: ");
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                currentPosition.append("GPS");
            }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                currentPosition.append("网络");
            }
            Log.d("currentPosition","the final currentPosition is the "+currentPosition);
            positionText.setText(currentPosition);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
