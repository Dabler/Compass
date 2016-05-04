package com.dabler.compasstest;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.lang.ref.WeakReference;

/**
 * Created by karolina on 02.05.2016.
 */
public class CompassHelper implements SensorEventListener {
    WeakReference<CompassContract> compassContract;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    private float[] r = new float[9];
    private float[] orientation = new float[3];

    public CompassHelper(CompassContract compassContract, SensorManager sensorManager){
        this.compassContract = new WeakReference<CompassContract>(compassContract);
        this.sensorManager = sensorManager;
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;
        if (event.sensor == accelerometer) {
            lastAccelerometer[0] = alpha * lastAccelerometer[0] + (1 - alpha) * event.values[0];
            lastAccelerometer[1] = alpha * lastAccelerometer[1] + (1 - alpha) * event.values[1];
            lastAccelerometer[2] = alpha * lastAccelerometer[2] + (1 - alpha) * event.values[2];
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer){
            lastMagnetometer[0] = alpha * lastMagnetometer[0] + (1 - alpha) * event.values[0];
            lastMagnetometer[1] = alpha * lastMagnetometer[1] + (1 - alpha) * event.values[1];
            lastMagnetometer[2] = alpha * lastMagnetometer[2] + (1 - alpha) * event.values[2];
            lastMagnetometerSet = true;
        }
        if (lastAccelerometerSet && lastMagnetometerSet) {
            float azimuth = calculateAzimuth(lastAccelerometer, lastMagnetometer);
            compassContract.get().CompassChanged(azimuth);
        }
    }

    private float calculateAzimuth(float[] accelerometerSet,float[] magnetometerSet) {
        SensorManager.getRotationMatrix(r, null, accelerometerSet, magnetometerSet);
        SensorManager.getOrientation(r, orientation);
        float azimuth = (float) Math.toDegrees(orientation[0]);
        azimuth = (azimuth + 360) % 360;
        return azimuth;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void ResumeCompass(){
        lastAccelerometerSet = false;
        lastMagnetometerSet = false;
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void PauseCompass(){
        sensorManager.unregisterListener(this);
    }
}
