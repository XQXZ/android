package lbstest.example.com.lbs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    public static final int UPDATE_TEXT = 1;

    public LocationClient mLocationClient;

    private TextView positionText;

    StringBuilder currentPosition;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_TEXT:
                    positionText.setText(currentPosition);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLoactionListener());
        setContentView(R.layout.activity_main);
        positionText = (TextView) findViewById(R.id.position_text_view);
        List<String> positionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED){
            positionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){
            positionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=
                PackageManager.PERMISSION_GRANTED){
            positionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(!positionList.isEmpty()){
            String [] permissions = positionList.toArray(new String[positionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else {
            requestLocation();
        }

    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 ){
                    for (int result : grantResults){
                     if (result != PackageManager.PERMISSION_DENIED){
                         Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                         finish();
                         return;
                     }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    private class MyLoactionListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
           currentPosition = new StringBuilder();
            currentPosition.append("纬度: ").append(bdLocation.getLatitude()).append("\n");
            currentPosition.append("经线: ").append(bdLocation.getLongitude()).append("\n");
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                currentPosition.append("GPS");
            }else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                currentPosition.append("网络");
            }else {
                currentPosition.append("Error:"+bdLocation.getLocType());
            }
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Message message = new Message();
//                    message.what = UPDATE_TEXT;
//                    handler.sendMessage(message);
//                }
//            }).start();
            Log.d("Main","本地位置是"+currentPosition);
          //  positionText.setText(currentPosition);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
