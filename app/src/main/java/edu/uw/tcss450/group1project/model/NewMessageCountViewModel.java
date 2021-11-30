/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;
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

    /** The live data that stores the total number of new messages received. */
    private MutableLiveData<Integer> mNewMessageCount;

    /**
     * The map (chatid -> new message count) that stores the number of missed
     * messages for each chat with a missed message.
     */
    private MutableLiveData<Map<Integer, Integer>> mNewMessageMap;

    /**
     * Creates a new message count view model with 0 initial messages.
     */
    public NewMessageCountViewModel() {
        mNewMessageCount = new MutableLiveData<>();
        mNewMessageCount.setValue(0);
        mNewMessageMap = new MutableLiveData<>();
        mNewMessageMap.setValue(new HashMap<>());
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
Log.d("NEW MESSAGE VIEW MODEL", "INCREMENT CALLED");
        mNewMessageCount.setValue(mNewMessageCount.getValue() + 1);

    }

    /**
     * Increments the total new messages and the new message mapping corresponding
     * to the chatId that the new message was from
     *
     * @param theChatId the chatId the new message came from
     */
    public void increment(final int theChatId) {
Log.d("NEW MESSAGE VIEW MODEL", "INCREMENT " + theChatId + " + 1");
        // increment the value that corresponds to this chatId
        final Map<Integer, Integer> map = mNewMessageMap.getValue();
        map.put(theChatId, map.getOrDefault(theChatId, 0) + 1);
        mNewMessageMap.setValue(map);

        // increment the total new chats
        mNewMessageCount.setValue(mNewMessageCount.getValue() + 1);
        System.out.println(map.get(theChatId));
    }


    /**
     * Clears the new message count mapping for the given chat, and decrements the total
     * new chat count by the amount that was cleared in the map.
     *
     * @param theChatId the chat that is being navigated to
     */
    public void decrement(final int theChatId) {
        final Map<Integer, Integer> map = mNewMessageMap.getValue();

        // check to make sure a value exists for the given chatId
        if (map.containsKey(theChatId)) {
            final int numMessagesRemoved = map.get(theChatId);
            map.put(theChatId, 0);
            mNewMessageMap.setValue(map);
            mNewMessageCount.setValue(mNewMessageCount.getValue() - numMessagesRemoved);
            Log.d("NEW MESSAGE VIEW MODEL", "DECREMENT " + theChatId + " - " + numMessagesRemoved);
        }
    }


    /**
     * Reset the message count to 0.
     */
    public void reset() {
        mNewMessageCount.setValue(0);
    }


}
