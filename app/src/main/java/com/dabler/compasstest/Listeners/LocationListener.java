package com.dabler.compasstest.Listeners;

import android.app.Activity;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dabler.compasstest.Contracts.LocationContract;
import com.dabler.compasstest.Contracts.LocationHelperContract;
import com.dabler.compasstest.Contracts.LocationListenerContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.lang.ref.WeakReference;

/**
 * Created by karolina on 05.05.2016.
 */
public class LocationListener implements LocationListenerContract, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    WeakReference<LocationContract> locationContract;

    private boolean requestingLocationUpdates = true;
    private boolean locationAvailable = false;

    WeakReference<Activity> activity;

    private GoogleApiClient googleApiClient;

    public LocationListener(Activity activity, LocationContract locationContract){
        this.locationContract = new WeakReference<>(locationContract);
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
    public void onConnected(@Nullable Bundle bundle) {
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        locationContract.get().LocationChanged(location);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void OnListenerStart(){
        googleApiClient.connect();
    }

    public void OnListenerStop(){
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

    public boolean IsLocationAvailable(){
        return locationAvailable;
    }
}
