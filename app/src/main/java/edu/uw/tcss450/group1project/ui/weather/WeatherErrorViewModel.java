/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

/**
 * WeatherErrorViewModel is a class for tracking successive errors when displaying weather data.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherErrorViewModel extends ViewModel {

    /** The observed error flag */
    private final MutableLiveData<Boolean> mErrorFlag;

    /** Indicates whether this view model has already been flagged for an error */
    private boolean mErrorReceived;

    /**
     * Constructs a new weather data view model
     */
    public WeatherErrorViewModel() {
        mErrorFlag = new MutableLiveData<>();
        mErrorFlag.setValue(false);
        mErrorReceived = false;
    }

    /**
     * Adds an observer to the error flag of this view model
     *
     * @param theOwner the view lifecycle owner
     * @param theObserver the observer to be assigned
     */
    public void addErrorFlagObserver(@NonNull final LifecycleOwner theOwner,
                                     @NonNull final Observer<? super Boolean> theObserver) {
        mErrorFlag.observe(theOwner, theObserver);
    }

    /**
     * Notifies this view model that an error has been detected
     */
    public void notifyErrorFlag() {
        if (!mErrorFlag.getValue()) {
            mErrorFlag.setValue(true);
        } else {
            mErrorReceived = true;
        }
    }

    /**
     * Resets this view model's error flag
     */
    public void resetErrorFlag() {
        mErrorFlag.setValue(false);
        mErrorReceived = false;
    }

    /**
     * Indicates whether this view model has received an error
     *
     * @return true if received, false otherwise
     */
    public boolean isErrorReceived() {
        return mErrorReceived;
    }
}
