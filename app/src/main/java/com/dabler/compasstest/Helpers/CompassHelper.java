package com.dabler.compasstest.Helpers;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.dabler.compasstest.Contracts.CompassHelperContract;

/**
 * Created by karolina on 02.05.2016.
 */
public class CompassHelper implements CompassHelperContract {

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;

    private float[] r = new float[9];
    private float[] orientation = new float[3];

    @Override
    public float ConvertValues(int sensorType, float[] values) {
        final float alpha = 0.97f;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            lastAccelerometer[0] = alpha * lastAccelerometer[0] + (1 - alpha) * values[0];
            lastAccelerometer[1] = alpha * lastAccelerometer[1] + (1 - alpha) * values[1];
            lastAccelerometer[2] = alpha * lastAccelerometer[2] + (1 - alpha) * values[2];
            lastAccelerometerSet = true;
        } else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD){
            lastMagnetometer[0] = alpha * lastMagnetometer[0] + (1 - alpha) * values[0];
            lastMagnetometer[1] = alpha * lastMagnetometer[1] + (1 - alpha) * values[1];
            lastMagnetometer[2] = alpha * lastMagnetometer[2] + (1 - alpha) * values[2];
            lastMagnetometerSet = true;
        }
        if (lastAccelerometerSet && lastMagnetometerSet) {
            return calculateAzimuth(lastAccelerometer, lastMagnetometer);
        }else{
            return 0f;
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
    public void ResumeCompass(){
        lastAccelerometerSet = false;
        lastMagnetometerSet = false;
    }
}
