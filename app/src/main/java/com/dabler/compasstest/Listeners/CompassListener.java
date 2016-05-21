package com.dabler.compasstest.Listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.dabler.compasstest.Contracts.CompassContract;
import com.dabler.compasstest.Contracts.CompassHelperContract;
import com.dabler.compasstest.Contracts.CompassListenerContract;

import java.lang.ref.WeakReference;

/**
 * Created by HP on 2016-05-05.
 */
public class CompassListener implements CompassListenerContract, SensorEventListener {
    WeakReference<CompassContract> compassContract;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    public CompassListener(CompassContract compassContract, SensorManager sensorManager){
        this.compassContract = new WeakReference<>(compassContract);
        this.sensorManager = sensorManager;
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        compassContract.get().CompassChanged(event.sensor.getType(), event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void ResumeCompassListener(){
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void PauseCompassListener(){
        sensorManager.unregisterListener(this);
    }
}
