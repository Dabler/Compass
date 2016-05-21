package com.dabler.compasstest;

import android.location.Location;

import com.dabler.compasstest.Helpers.CompassHelper;
import com.dabler.compasstest.Helpers.LocationHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by Karolina on 12.05.2016.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class HelpersTest {

    CompassHelper compassHelper;
    LocationHelper locationHelper;

    @Before
    public void setup() {
        compassHelper = new CompassHelper();
        locationHelper = new LocationHelper();
    }

//    @Test
//    public void compass_is_returning_correct_degree() throws Exception {
//    }

    @Test
    public void location_is_returning_correct_degree_for_same_location() throws Exception {
        Location location = new Location("");
        location.setLatitude(50);
        location.setLongitude(39);
        location.setAltitude(135);
        locationHelper.UpdateLocation(location);
        locationHelper.SetDestinationLocation(50,39);
        float degreesToDestination = locationHelper.GetDegreesToDestination();
        assertEquals(0, degreesToDestination, 4);
    }

    @Test
    public void location_is_returning_correct_degree() throws Exception {
        Location location = new Location("");
        location.setLatitude(50);
        location.setLongitude(38);
        location.setAltitude(135);
        locationHelper.UpdateLocation(location);
        locationHelper.SetDestinationLocation(53,38);
        float degreesToDestination = locationHelper.GetDegreesToDestination();
        assertEquals(0, degreesToDestination, 4);
    }

    @Test
    public void location_is_returning_correct_degree2() throws Exception {
        Location location = new Location("");
        location.setLatitude(22);
        location.setLongitude(30);
        location.setAltitude(276);
        locationHelper.UpdateLocation(location);
        locationHelper.SetDestinationLocation(22,13);
        float degreesToDestination = locationHelper.GetDegreesToDestination();
        assertEquals(90, degreesToDestination, 4);
    }

    @Test
    public void is_returning_correct_distance_from_warsaw_to_berlin() throws Exception {
        Location berlin = new Location("");
        berlin.setLatitude(52.507629);
        berlin.setLongitude(13.1449523);
        berlin.setAltitude(45);

        Location warsaw = new Location("");
        warsaw.setLatitude(52.2330269);
        warsaw.setLongitude(20.7810097);
        warsaw.setAltitude(92);

        float anticipatedDistance = 519000;
        locationHelper = new LocationHelper();
        locationHelper.UpdateLocation(berlin);
        locationHelper.SetDestinationLocation(52.2330269f, 20.7810097f);
        float calculatedDistance = locationHelper.GetDistance();
        assertEquals(anticipatedDistance, calculatedDistance, 3000);
    }
}
