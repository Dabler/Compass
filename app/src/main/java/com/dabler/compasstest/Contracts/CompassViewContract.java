package com.dabler.compasstest.Contracts;

/**
 * Created by Karolina on 11.05.2016.
 */
public interface CompassViewContract extends BaseView<CompassPresenterContract> {
    public void AdjustCompassArrow(float oldPosition, float newPosition);
    public void AdjustNavigationArrow(float oldPosition, float newPosition);
    public void ShowInformation(String information);
    public void SetDistanceInformation(String information);
}
