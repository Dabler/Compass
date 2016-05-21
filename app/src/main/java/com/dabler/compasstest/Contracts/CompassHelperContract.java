package com.dabler.compasstest.Contracts;

/**
 * Created by HP on 2016-05-05.
 */
public interface CompassHelperContract {
    public float ConvertValues(int sensorType, float[] values);
    public void ResumeCompass();
}
