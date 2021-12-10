/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A View Model that stores information about contact notifications including new contacts,
 * new sent/received contact requests, and total contact notifications
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ContactNotificationViewModel extends AndroidViewModel {

//    /** The key that stores the total number of contact notifications */
//    public static final String TOTAL_KEY = "TOTAL";

    /**
     * The live data map that contains the set of contact request notifications
     */
    private MutableLiveData<Set<String>> mContactRequestSet;

    /**
     * The live data map that contains the set of contacts notifications
     */
    private MutableLiveData<Set<String>> mContactsSet;

    /**
     * Creates a new view model with default values.
     *
     * @param theApplication the app this model belongs to
     */
    public ContactNotificationViewModel(@NonNull Application theApplication) {
        super(theApplication);

        mContactRequestSet = new MutableLiveData<>();
        mContactRequestSet.setValue(new HashSet<>());

        mContactsSet = new MutableLiveData<>();
        mContactsSet.setValue(new HashSet<>());
    }


    /**
     * Adds the notification to the contact request notification set specified by the
     * nickname of who sent the request.
     *
     * @param theNickname the nickname who sent us the contact request
     */
    public void addContactRequestNotification(final String theNickname) {
        final Set<String> set = mContactRequestSet.getValue();
        set.add(theNickname);
        mContactRequestSet.setValue(set);
    }

    /**
     * Adds the notification to the contacts notification set specified by the
     * nickname of who sent the new contact is.
     *
     * @param theNickname the nickname who sent us the contact request
     */
    public void addContactsNotification(final String theNickname) {
Log.d("ADDING NICKNAME", theNickname == null ? "null": theNickname);
        final Set<String> set = mContactsSet.getValue();
        set.add(theNickname);
        mContactsSet.setValue(set);
    }

//    /**
//     * Decrements the contact notification count for the specific tab by 1
//     * @param theTab the tab that should be decremented by one
//     */
//    public void decrementNotification(final String theTab) {
//        final Map<String, Integer> map = mTabCounts.getValue();
//        map.put(TOTAL_KEY, map.getOrDefault(TOTAL_KEY, 1) - 1);
//        map.put(theTab, map.getOrDefault(theTab, 1) - 1);
//
//        mTabCounts.setValue(map);
//    }






//    /**
//     * Removes the entire notification count for the given tab key
//     *
//     * @param theTab the key that determines which navigation tab the
//     *               notifications should be deleted from
//     */
//    public void removeTabNotifications(final String theTab) {
//        final Map<String, Integer> map = mTabCounts.getValue();
//        map.put(TOTAL_KEY, map.getOrDefault(TOTAL_KEY, 0) - map.getOrDefault(theTab, 0));
//        map.put(theTab, 0);
//
//        mTabCounts.setValue(map);
//    }


    public void putContactRequestData(final Set<String> theContactRequestSet) {
        mContactRequestSet.setValue(theContactRequestSet);
    }

    public void putContactsData(final Set<String> theContactsSet) {
        mContactsSet.setValue(theContactsSet);
    }


    public void addContactRequestNotifObserver(@NonNull final LifecycleOwner theOwner,
                                               @NonNull final Observer<? super
                                                       Set<String>> theObserver) {
        mContactRequestSet.observe(theOwner, theObserver);
    }

    public void addContactsNotifObserver(@NonNull final LifecycleOwner theOwner,
                                               @NonNull final Observer<? super
                                                       Set<String>> theObserver) {
        mContactsSet.observe(theOwner, theObserver);
    }


    public int getTotalContactsNotificationCount() {
        return mContactsSet.getValue().size() + mContactRequestSet.getValue().size();
    }


    public void clearAllContactsNotifications(final Context theContext) {
        mContactsSet.setValue(new HashSet<>());

        // clear the local storage of the notification too
        LocalStorageUtils.clearContactsNotifications(theContext);
    }

    public void clearAllContactRequestNotifications(final Context theContext) {
        mContactRequestSet.setValue(new HashSet<>());

        // clear the local storage of the notification too
        LocalStorageUtils.clearContactRequestsNotifications(theContext);
        System.out.println("CLEARING ALL CONTACT REQUEST NOTIFICATIONS");
    }

    public void removeContactRequestNotification(final Context theContext,
                                                 final String theNickname) {
        final Set<String> set = mContactRequestSet.getValue();
        set.remove(theNickname);
        mContactRequestSet.setValue(set);

        // clear the local storage of the notification too
        LocalStorageUtils.decrementContactRequestNotifications(theContext, theNickname);
    }
}
