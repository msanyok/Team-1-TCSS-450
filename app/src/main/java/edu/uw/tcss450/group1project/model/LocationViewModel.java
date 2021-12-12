/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

/**
 * LocationViewModel is a class for storing and providing the user's device location.
 *
 * @author Charles Bryan
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class LocationViewModel extends ViewModel {

    /** The stored location */
    private MutableLiveData<Location> mLocation;

    /**
     * Constructs a new location view model
     */
    public LocationViewModel() {
        mLocation = new MediatorLiveData<>();
    }

    /**
     * Adds an observer to this view model's stored location
     *
     * @param theOwner the lifecycle owner
     * @param theObserver the observer to be assigned
     */
    public void addLocationObserver(@NonNull final LifecycleOwner theOwner,
                                    @NonNull final Observer<? super Location> theObserver) {
        mLocation.observe(theOwner, theObserver);
    }

    /**
     * Sets the location of this view model
     *
     * @param theLocation the location to be assigned
     */
    public void setLocation(final Location theLocation) {
        if (!theLocation.equals(mLocation.getValue())) {
            mLocation.setValue(theLocation);
        }
    }

    /**
     * Provides access to this view model's location
     *
     * @return the stored location
     */
    public Location getCurrentLocation() {
        return mLocation.getValue();
    }
}