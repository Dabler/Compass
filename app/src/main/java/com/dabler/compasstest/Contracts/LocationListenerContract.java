package com.dabler.compasstest.Contracts;

/**
 * Created by HP on 2016-05-07.
 */
public interface LocationListenerContract {
    boolean IsLocationAvailable();

    void OnListenerResume();

    void OnListenerPause();

    void OnListenerStart();

    void OnListenerStop();
}
