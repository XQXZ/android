package com.sdutacm.lightsensortest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor magneticSensor,acclerometerSensor;
    private TextView lightLevel;
    float[] acclerometerValues = new float[3];
    float[] magneticFildValues = new float[3];
    private ImageView compassImg;
    private float lastRotateDegree;
    private TextView orientationtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSensorManager();
    }

    private void getSensorManager() {
        orientationtext = (TextView) findViewById(R.id.orientation);
        compassImg = (ImageView) findViewById(R.id.compass_img);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        acclerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this,magneticSensor,SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this,acclerometerSensor,SensorManager.SENSOR_DELAY_GAME);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this,magneticSensor);
            sensorManager.unregisterListener(this,acclerometerSensor);

        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            acclerometerValues = event.values.clone();
        }else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magneticFildValues = event.values.clone();
        }
        float[] R = new float[9];
        float[] values = new float[3];

        SensorManager.getRotationMatrix(R,null,acclerometerValues,magneticFildValues);
        sensorManager.getOrientation(R,values);
        float rotateDegree = -(float) Math.toDegrees(values[0]);
        if(Math.abs(rotateDegree - lastRotateDegree)>1){
            RotateAnimation animation = new RotateAnimation(lastRotateDegree,rotateDegree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            animation.setFillAfter(true);
            compassImg.startAnimation(animation);
            lastRotateDegree = rotateDegree;
            compassImg.startAnimation(animation);
            lastRotateDegree = rotateDegree;
            float orientationNum = 180;
            orientationNum+=(int) lastRotateDegree;
            StringBuffer buffer = new StringBuffer();
            boolean inferContent = false;
            if(orientationNum >=67.5 && orientationNum <112.5){
                buffer.append("东");
                if((int)orientationNum>=83 && (int)orientationNum>=93){
                    buffer.append("\n寻龙分金看缠山，一重缠是一重关\n少侠,此处风水俱佳!必有重宝!!!");
                    inferContent  = true;
                }
            }else if(orientationNum >=22.5 && orientationNum <67.5) {
                buffer.append("东北");
                if((int)orientationNum>=42 && (int)orientationNum<=48){
                    buffer.append("\n关门若有千重锁，定有王侯居此间\n少侠,此处必有重宝!!!");
                    inferContent  = true;
                }
            }else if(orientationNum >=337.5 && orientationNum <22.5) {
                buffer.append("北");
                if((int)orientationNum>=3 && (int)orientationNum<=357){
                    buffer.append("\n关门如有八重险，不出阴阳八卦形\n少侠,此处风水俱佳!必有重宝!!!");
                    inferContent  = true;
                }
            }else if(orientationNum >=292.5 && orientationNum <337.5) {
                buffer.append("西北");
                if((int)orientationNum>=312 && (int)orientationNum <=318){
                    buffer.append("\n摸金校尉，合择生，分择死\n少侠,请保持和你队友的联系!!!");
                    inferContent  = true;
                }
            }else if(orientationNum >=247.5 && orientationNum <292.5) {
                buffer.append("西");
                if((int)orientationNum>=267 && (int)orientationNum <= 273){
                    buffer.append("\n摸金校尉，合择生，分择死\n少侠,请保持和你队友的联系!!!");
                    inferContent  = true;
                }

            }else if(orientationNum >=202.5 && orientationNum <247.5) {
                buffer.append("西南");
                if((int)orientationNum>=222 && (int)orientationNum<=228) {
                    buffer.append("\n鸡鸣灯灭不摸金\n少侠,快撤,有粽子!!!");
                    inferContent  = true;
                }
            }else if(orientationNum >=157.5 && orientationNum <202.5) {
                buffer.append("南");
                if((int)orientationNum>=177 && (int)orientationNum <=183) {
                    buffer.append("\n 天下第一奇书——风水残卷《十六字阴阳风水秘术》\n少侠,只有十块钱,十块钱你买不到吃亏,买不到上当!!!");
                    inferContent  = true;
                }

            }else {
                buffer.append("东南");
                if((int)orientationNum>=132 && (int)orientationNum<=138) {
                    buffer.append("\n世界上比鬼神可怕的是人心!\n少侠,我发现一个惊天的秘密,,,,你是世界上最帅的人!!!");
                    inferContent  = true;
                }
            }
            if( inferContent){
                orientationtext.setText((int)orientationNum+"\n"+buffer);
            }else {
                orientationtext.setText(buffer+"\n"+(int)orientationNum);
            }


            /**
             * 90-22.5 90+22.5 67.5  112.5  东
             * 45-22.5 45+22.5 22.5  67.5   东北
             * 360-22.5        337.5 22.5   北
             *                 337.5 292.5  西北
             *                 292.5 247.5 西
             *                 247.5 202.5 西南
             *                 202.5 157.5 南
             *                 157.5 112.5 东南
             */
        }
        Log.d("MainActivity","values[0] is "+Math.toDegrees(values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
