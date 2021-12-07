/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A View Model designed to store and handle typing notifications for chats.
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public class IsTypingViewModel extends AndroidViewModel {

    /** The amount of time it takes for a timer to notify the server the user is done typing */
    private static final int TIMER_TIMEOUT = 10000; // 10 seconds

    /** The live data that stores the current valid typing timers */
    private MutableLiveData<Map<Integer, Map<String, Timer>>> mTimers;

    /**
     * Constructor that sets default values
     */
    public IsTypingViewModel(@NonNull final Application theApplication) {
        super(theApplication);
        mTimers = new MutableLiveData<>();
        mTimers.setValue(new HashMap<>());
    }

    /**
     * Adds a new typing timer for the given chatId that notes the given user theNickname
     * has started typing.
     *
     * @param theChatId the chat id the user is typing in
     * @param theNickname the nickname of the user who started typing
     */
    public void putTyping(final int theChatId, final String theNickname) {

        final Map<Integer, Map<String, Timer>> chatIdMap = mTimers.getValue();
        final Map<String, Timer> nicknameMap = chatIdMap.getOrDefault(theChatId, new HashMap<>());

        // cancel and remove the old timer if there already exists one for the given chat id
        if (nicknameMap.containsKey(theNickname)) {
            nicknameMap.get(theNickname).cancel();
            nicknameMap.remove(theNickname);
        }

        // create a new timer for the given chat id
        final Timer timer = new Timer();
        final TimerTask newTask = new TimerTask() {
            @Override
            public void run() {
                stopTyping(theChatId, theNickname);
            }
        };
        timer.schedule(newTask, TIMER_TIMEOUT);
        nicknameMap.put(theNickname, timer);
        chatIdMap.put(theChatId, nicknameMap);
        mTimers.postValue(chatIdMap);

    }

    /**
     * Cancels and removes the timer corresponding to the chatId and nickname if one exists,
     * notifying that the user has stopped typing in that chat.
     *
     * @param theChatId the chat id of the chat the user stopped typing in
     * @param theNickname the nickname of the user who stopped typing
     */
    public void stopTyping(final int theChatId, final String theNickname) {
        final Map<Integer, Map<String, Timer>> chatIdMap = mTimers.getValue();

        if (chatIdMap.containsKey(theChatId)) {
            final Map<String, Timer> nicknameMap = chatIdMap.getOrDefault(theChatId,
                    new HashMap<>());
            if (nicknameMap.containsKey(theNickname)) {
                nicknameMap.get(theNickname).cancel();
                nicknameMap.remove(theNickname);
                chatIdMap.put(theChatId, nicknameMap);
                mTimers.postValue(chatIdMap);
            }
        }

    }

    /**
     * Sends a typing notification to the server that will send pushy
     * notifications to the users in the chat.
     *
     * @param theChatId the chat id of the chat we want to send the notifications to
     * @param theJwt the user's jwt to verify them
     * @param theUserIsTyping whether or not this user is typing or not
     */
    public void sendTypingNotification(final int theChatId,
                                       final String theJwt,
                                       final boolean theUserIsTyping) {
        final String url = "https://team-1-tcss-450-server.herokuapp.com/messages/typing";

        final JSONObject body = new JSONObject();
        try {
            body.put("chatId", theChatId);
            body.put("isStartingToType", theUserIsTyping);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                null,
                null) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", theJwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    /**
     * Adds the given observer to the typing timers map.
     *
     * @param theOwner the lifecycle owner
     * @param theObserver the observer that observes the map
     */
    public void addTimersObserver(@NonNull final LifecycleOwner theOwner,
                                  @NonNull final Observer<? super Map<Integer, Map<String, Timer>>> theObserver) {
        mTimers.observe(theOwner, theObserver);
    }

}
