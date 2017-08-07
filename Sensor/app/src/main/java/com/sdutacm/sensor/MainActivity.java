package com.sdutacm.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;

    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSensorManager();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        //Sensor 发生变化时,在次通过event.values获取数据
        float x = event.values[0];
        float y = event.values[0];
        float z = event.values[0];
        if(x>20 || y>20 || z>20){
            Toast.makeText(this,"欢迎使用摇一摇",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void getSensorManager() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        /**
         * 传入的参数决定传感器的类型
         * Senor.TYPE_ACCELEROMETER: 加速度传感器
         * Senor.TYPE_LIGHT:光照传感器
         * Senor.TYPE_GRAVITY:重力传感器
         * SenorManager.getOrientation(); //方向传感器
         */
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sensorManager != null){
            //一般在Resume方法中注册
            /**
             * 第三个参数决定传感器信息更新速度
             * SensorManager.SENSOR_DELAY_NORMAL:一般
             * SENSOR_DELAY_FASTEST:最快
             * SENSOR_DELAY_GAME:比较快,适合游戏
             * SENSOR_DELAY_UI:慢
             */
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensorManager != null){
            //解除注册
            sensorManager.unregisterListener(this,sensor);
        }
    }

}
