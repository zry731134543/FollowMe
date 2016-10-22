package com.winorout.followme;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.winorout.base.BaseActivity;


public class SportDataActivity extends BaseActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mStepCounter;

    @Override
    public void initVariables() {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sportdata);
        initTopbar("数据");
    }

    @Override
    public void loadData() {

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        Log.d("Test", "Got the step count : " +String.valueOf(event.values[0]));
    }

}
