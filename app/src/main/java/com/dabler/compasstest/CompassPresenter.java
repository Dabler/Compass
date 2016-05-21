package com.dabler.compasstest;

import android.app.Activity;
import android.hardware.SensorManager;
import android.location.Location;

import com.dabler.compasstest.Contracts.CompassContract;
import com.dabler.compasstest.Contracts.CompassHelperContract;
import com.dabler.compasstest.Contracts.CompassListenerContract;
import com.dabler.compasstest.Contracts.CompassPresenterContract;
import com.dabler.compasstest.Contracts.CompassViewContract;
import com.dabler.compasstest.Contracts.LocationContract;
import com.dabler.compasstest.Contracts.LocationHelperContract;
import com.dabler.compasstest.Contracts.LocationListenerContract;
import com.dabler.compasstest.Helpers.CompassHelper;
import com.dabler.compasstest.Helpers.LocationHelper;
import com.dabler.compasstest.Listeners.CompassListener;
import com.dabler.compasstest.Listeners.LocationListener;

/**
 * Created by Karolina on 11.05.2016.
 */
public class CompassPresenter implements CompassPresenterContract, CompassContract, LocationContract {

    private LocationListenerContract locationListener;
    private CompassListenerContract compassListener;
    LocationHelperContract locationHelper;
    CompassHelperContract compassHelper;


    private float oldNavigationPosition = 0;
    private float oldAzimuth = 0;

    private float lastDegreesToDestination;

    private final CompassViewContract compassView;

    private boolean navigate = false;

    public CompassPresenter(Activity activity, CompassViewContract compassView){
        SensorManager mSensorManager = (SensorManager)activity.getSystemService(Activity.SENSOR_SERVICE);
        locationListener = new LocationListener(activity, this);
        locationHelper = new LocationHelper();
        compassListener = new CompassListener(this, mSensorManager);
        compassHelper = new CompassHelper();
        this.compassView = compassView;
        compassView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void CompassChanged(int sensorType, float[] values) {
        float azimuth = compassHelper.ConvertValues(sensorType, values);
        compassView.AdjustCompassArrow(oldAzimuth, azimuth);
        oldAzimuth = azimuth;
        if(navigate) {
            if(!locationListener.IsLocationAvailable()){
                NotifyLocationUnavailable();
            }else {
                SetNavigationInformation(azimuth);
            }
        }
    }

    private void NotifyLocationUnavailable() {
        navigate = false;
        compassView.ShowInformation("Turn on GPS to continue");
    }

    private void SetNavigationInformation(float azimuth) {
        float arrowAngleInDegrees = locationHelper.GetDegreesToDestination() - azimuth;
        compassView.AdjustNavigationArrow(oldNavigationPosition, arrowAngleInDegrees);
        oldNavigationPosition = arrowAngleInDegrees;
        compassView.SetDistanceInformation("Distance left: "+String.format("%.2f", locationHelper.GetDistance()/1000)+" km");
        if(locationHelper.GetDistance() < 0.10f){
            navigate=false;
            compassView.SetDistanceInformation("Destination target reached");
        }
    }

    @Override
    public void LocationChanged(Location location) {
        locationHelper.UpdateLocation(location);
    }

    @Override
    public void OnResume() {
        compassListener.ResumeCompassListener();
        locationListener.OnListenerResume();
    }

    @Override
    public void OnPause() {
        compassListener.PauseCompassListener();
        locationListener.OnListenerPause();
    }

    @Override
    public void OnStart() {
        locationListener.OnListenerStart();
    }

    @Override
    public void OnStop() {
        locationListener.OnListenerStop();
    }

    @Override
    public void SetDestinationLocation(Float latitude, Float longitude) {
        navigate = true;
        locationHelper.SetDestinationLocation(latitude, longitude);
    }
}
