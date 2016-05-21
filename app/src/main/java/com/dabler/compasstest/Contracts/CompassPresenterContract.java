package com.dabler.compasstest.Contracts;

/**
 * Created by Karolina on 11.05.2016.
 */
public interface CompassPresenterContract extends BasePresenter{
    public void OnResume();
    public void OnPause();
    public void OnStart();
    public void OnStop();

    void SetDestinationLocation(Float latitude, Float longitude);
}
