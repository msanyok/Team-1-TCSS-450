/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.HashSet;
import java.util.Set;

/**
 * A View Model that stores information about contact notifications including new contacts,
 * new sent/received contact requests, and total contact notifications
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ContactNotificationViewModel extends AndroidViewModel {

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
        final Set<String> set = mContactsSet.getValue();
        set.add(theNickname);
        mContactsSet.setValue(set);
    }

    /**
     * Sets the value of the contact request notification live data to the given String set
     *
     * @param theContactRequestSet the set that the live data is set to
     */
    public void putContactRequestData(final Set<String> theContactRequestSet) {
        mContactRequestSet.setValue(theContactRequestSet);
    }

    /**
     * Sets the value of the contact notification live data to the given String set
     *
     * @param theContactsSet the set that the live data is set to
     */
    public void putContactsData(final Set<String> theContactsSet) {
        mContactsSet.setValue(theContactsSet);
    }

    /**
     * Adds the given observer to the contact request notification live data
     *
     * @param theOwner the lifecycle owner of the observer
     * @param theObserver the observer
     */
    public void addContactRequestNotifObserver(@NonNull final LifecycleOwner theOwner,
                                                @NonNull final Observer<? super
                                                       Set<String>> theObserver) {
        mContactRequestSet.observe(theOwner, theObserver);
    }

    /**
     * Adds the given observer to the contact notification live data
     *
     * @param theOwner the lifecycle owner of the observer
     * @param theObserver the observer
     */
    public void addContactsNotifObserver(@NonNull final LifecycleOwner theOwner,
                                               @NonNull final Observer<? super
                                                       Set<String>> theObserver) {
        mContactsSet.observe(theOwner, theObserver);
    }

    /**
     * Returns the total number of contact notifications
     * (sum of contact and contact request notifications)
     *
     * @return the total notification count
     */
    public int getTotalContactsNotificationCount() {
        return mContactsSet.getValue().size() + mContactRequestSet.getValue().size();
    }

    /**
     * Clears the contact notification data in the view model AND removes it from local storage
     *
     * @param theContext where this method was called
     */
    public void clearAllContactsNotifications(final Context theContext) {
        mContactsSet.setValue(new HashSet<>());
        LocalStorageUtils.clearContactsNotifications(theContext);
    }

    /**
     * Clears the contact request notification data in the view model AND removes it from local storage
     *
     * @param theContext where this method was called
     */
    public void clearAllContactRequestNotifications(final Context theContext) {
        mContactRequestSet.setValue(new HashSet<>());
        LocalStorageUtils.clearContactRequestsNotifications(theContext);
    }

    /**
     * Removes the given contact request notification specified by the nickname
     * (who rejected/deleted the request)
     *
     * @param theContext where this method was called
     * @param theNickname which contact request notification should be deleted
     */
    public void removeContactRequestNotification(final Context theContext,
                                                 final String theNickname) {
        final Set<String> set = mContactRequestSet.getValue();
        set.remove(theNickname);
        mContactRequestSet.setValue(set);
        LocalStorageUtils.decrementContactRequestNotifications(theContext, theNickname);
    }
}
