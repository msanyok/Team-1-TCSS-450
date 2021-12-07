/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.Map;

/**
 * A View Model that stores information about contact notifications including new contacts,
 * new sent/received contact requests, and total contact notifications
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ContactNotificationViewModel extends AndroidViewModel {

    /** The key that stores the total number of contact notifications */
    public static final String TOTAL_KEY = "TOTAL";

    /**
     * The live data map that contains the totals for the "Contacts",
     * "Requests", and total notifications.
     */
    private MutableLiveData<Map<String, Integer>> mTabCounts;

    /**
     * Creates a new view model with default values.
     *
     * @param theApplication the app this model belongs to
     */
    public ContactNotificationViewModel(@NonNull Application theApplication) {
        super(theApplication);

        mTabCounts = new MutableLiveData<>();
        mTabCounts.setValue(new HashMap<>());
    }

    /**
     * Adds a single notification count to the value mapped to given specified key.
     *
     * @param theTab the key that determines which navigation tab the notification belongs to
     */
    public void addNotification(final String theTab) {
        final Map<String, Integer> map = mTabCounts.getValue();
        map.put(theTab, map.getOrDefault(theTab, 0) + 1);
        map.put(TOTAL_KEY, map.getOrDefault(TOTAL_KEY, 0) + 1);

        mTabCounts.setValue(map);
    }

    /**
     * Removes the entire notification count for the given tab key
     *
     * @param theTab the key that determines which navigation tab the
     *               notifications should be deleted from
     */
    public void removeTabNotifications(final String theTab) {
        final Map<String, Integer> map = mTabCounts.getValue();
        map.put(TOTAL_KEY, map.getOrDefault(TOTAL_KEY, 0) - map.getOrDefault(theTab, 0));
        map.put(theTab, 0);

        mTabCounts.setValue(map);
    }

    /**
     *
     * @param theContactNotificationMap
     */
    public void putData(final Map<String, Integer> theContactNotificationMap) {
        mTabCounts.setValue(theContactNotificationMap);
    }

    /**
     * Adds an observer to the contact notifications live data
     *
     * @param theOwner the lifecycle owner of the observer
     * @param theObserver the observer
     */
    public void addContactNotifObserver(@NonNull final LifecycleOwner theOwner,
                                        @NonNull final Observer<? super
                                                Map<String, Integer>> theObserver) {
        mTabCounts.observe(theOwner, theObserver);
    }

    /**
     * Returns the live data this view model stores
     * @return the live data this view model stores
     */
    public Map<String, Integer> getData() {
        return mTabCounts.getValue();
    }
}
