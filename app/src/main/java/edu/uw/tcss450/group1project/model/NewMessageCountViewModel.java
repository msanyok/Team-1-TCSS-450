/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

/**
 * A view model class that stores information about how many new messages have been received and
 * are unread.
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @version Fall 2021
 */
public class NewMessageCountViewModel extends ViewModel {

    /** The live data that stores the number of new messages received. */
    private MutableLiveData<Integer> mNewMessageCount;

    /**
     * Creates a new message count view model with 0 initial messages.
     */
    public NewMessageCountViewModel() {
        mNewMessageCount = new MutableLiveData<>();
        mNewMessageCount.setValue(0);
    }

    /**
     * Adds an observer to the new message count live data
     *
     * @param theOwner
     * @param theObserver
     * @throws NullPointerException if theOwner is null
     * @throws NullPointerException if theObserver is null
     */
    public void addMessageCountObserver(@NonNull final LifecycleOwner theOwner,
                                        @NonNull final Observer<? super Integer> theObserver) {
        Objects.requireNonNull(theOwner, "theOwner can not be null");
        Objects.requireNonNull(theObserver, "theObserver can not be null");
        mNewMessageCount.observe(theOwner, theObserver);
    }

    /**
     * Increment the new message count by 1.
     */
    public void increment() {
        mNewMessageCount.setValue(mNewMessageCount.getValue() + 1);
    }

    /**
     * Reset the message count to 0.
     */
    public void reset() {
        mNewMessageCount.setValue(0);
    }
}
