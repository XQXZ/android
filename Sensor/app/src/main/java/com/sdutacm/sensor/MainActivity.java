package com.sdutacm.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;

    private Sensor accelerometerSensor;

    private Sensor magneticFieldSensor;

    private float[] accelerometerValues = new float[3];

    private float[] magneticValues = new float[3];

    //旋转矩阵,用来保存磁场和加速度的数据
    private float[] r = new float[9];

    //模拟方向传感器的数据(原始数据为弧度)
    private float[] values = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSensorManager();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            //这里是对象,需要克隆一份,否则共用一份数据
            accelerometerValues = event.values.clone();
        }else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            //这里是对象,需要克隆一份,否则共用一份数据
            magneticValues = event.values.clone();
        }
        /**
         * 填充旋转数组r
         * r:要填充的旋转数组
         * I:将磁场数据转换成实际的重力坐标中,一般可以设置为null
         * gravity:加速度传感器数据
         * geomagnetic:地磁传感器数据
         */
        SensorManager.getRotationMatrix(r,null,accelerometerValues,magneticValues);
        /**
         * R:旋转数组
         * values :模拟方向传感器的数据
         */
        sensorManager.getOrientation(r,values);

        float degree = (float) Math.toDegrees(values[0]);
        Log.d("指南针","当前手机角度为:"+degree);
        Toast.makeText(this,"当前手机角度为:"+degree,Toast.LENGTH_SHORT).show();


        //Sensor 发生变化时,在次通过event.values获取数据
//        float x = event.values[0];
//        float y = event.values[0];
//        float z = event.values[0];
//        float light = event.values[0];
//        Log.d("Light","当前的光线强度为:"+light+"勒克斯");
//        Toast.makeText(this,"当前的光线强度为:"+light+"勒克斯",Toast.LENGTH_SHORT).show();

//        if(x>20 || y>20 || z>20){
//            Toast.makeText(this,"欢迎使用摇一摇",Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //注册的Sensor精度发生变化时,在此处处理
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
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
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
            sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this,magneticFieldSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sensorManager != null){
            //解除注册
            sensorManager.unregisterListener(this,accelerometerSensor);
            sensorManager.unregisterListener(this,magneticFieldSensor);
        }
    }

}
