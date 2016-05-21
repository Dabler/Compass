package com.dabler.compasstest.Helpers;

import android.hardware.GeomagneticField;
import android.location.Location;

import com.dabler.compasstest.Contracts.LocationHelperContract;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by karolina on 03.05.2016.
 */
public class LocationHelper implements LocationHelperContract {

    private Location lastLocation;
    private Location destinationLocation;
    private GeomagneticField geoField;

    public void UpdateLocation(Location location) {
        lastLocation = location;

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

    public float GetDegreesToDestination() {
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

}
