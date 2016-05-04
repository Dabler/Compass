package com.dabler.compasstest;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by karolina on 03.05.2016.
 */
public class LocationHelper implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;
    private boolean requestingLocationUpdates = true;

    private Location lastLocation;
    private Location destinationLocation;
    private String lastUpdateTime;
    private GeomagneticField geoField;

    WeakReference<Activity> activity;
    private boolean locationAvailable = false;

    public LocationHelper(Activity activity){
        this.activity = new WeakReference(activity);
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) throws SecurityException {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() throws SecurityException {
        LocationRequest locationRequest = createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        AddLocationSettingsChecker(locationRequest);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        lastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        geoField = new GeomagneticField(
                Double.valueOf(location.getLatitude()).floatValue(),
                Double.valueOf(location.getLongitude()).floatValue(),
                Double.valueOf(location.getAltitude()).floatValue(),
                System.currentTimeMillis()
        );
    }

    public void SetDestinationLocation(float latitude, float longitude){
        destinationLocation = new Location("");
        destinationLocation.setLatitude(latitude);
        destinationLocation.setLongitude(longitude);
    }

    public float GetDegrees() {
        if(lastLocation !=null) {
            Float bearing = lastLocation.bearingTo(destinationLocation);
            Float heading = lastLocation.getBearing();
            float newHeading = geoField.getDeclination();
            newHeading = (bearing - heading) * -1;
            Float degrees = normalizeDegree(newHeading);
            return degrees;
        }else{
            return 0f;
        }
    }

    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }

    public float GetDistance(){
        return lastLocation.distanceTo(destinationLocation);
    }

    public boolean IsLocationAvailable(){
        return locationAvailable;
    }

    public void OnStartLocationHandler(){
        googleApiClient.connect();
    }

    public void OnStopLocationHandler(){
        googleApiClient.disconnect();
    }

    public void OnListenerPause(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    public void OnListenerResume(){
        if(googleApiClient.isConnected() && !requestingLocationUpdates){
            startLocationUpdates();
        }
    }

    public void OnListenerStart(){
        googleApiClient.connect();
    }

    public void OnListenerStop(){
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onConnectionSuspended(int i) {}

    private void AddLocationSettingsChecker(LocationRequest locationRequest){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStates= result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        locationAvailable = true;
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        locationAvailable = false;
                        try {
                            status.startResolutionForResult(activity.get(), 1);
                        } catch (IntentSender.SendIntentException e) {}
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        locationAvailable = false;
                        break;
                }
            }
        });
    }
}
