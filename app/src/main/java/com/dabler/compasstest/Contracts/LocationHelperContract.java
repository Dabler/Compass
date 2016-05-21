package com.dabler.compasstest.Contracts;

import android.location.Location;

/**
 * Created by karolina on 05.05.2016.
 */
public interface LocationHelperContract {
    public void UpdateLocation(Location location);

    float GetDegreesToDestination();

    float GetDistance();

    public void SetDestinationLocation(float latitude, float longitude);

    }
