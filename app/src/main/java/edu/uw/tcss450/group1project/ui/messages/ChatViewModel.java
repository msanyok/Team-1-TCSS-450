/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.io.RequestQueueSingleton;

/**
 * ChatViewModel is a class containing all messages for all chat rooms.
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ChatViewModel extends AndroidViewModel {

    /**
     * A Map of Lists of Chat Messages.
     * The Key represents the Chat ID
     * The value represents the List of (known) messages for that that room.
     */
    private Map<Integer, MutableLiveData<List<ChatMessage>>> mMessages;

    /**
     * Creates a new ChatViewModel with the provided application
     *
     * @param theApplication the application to be assigned
     */
    public ChatViewModel(@NonNull final Application theApplication) {
        super(theApplication);
        mMessages = new HashMap<>();
    }

    /**
     * Register as an observer to listen to a specific chat room's list of messages.
     * @param theChatId the chatid of the chat to observer
     * @param theOwner the fragments lifecycle owner
     * @param theObserver the observer
     */
    public void addMessageObserver(final int theChatId,
                                   @NonNull final LifecycleOwner theOwner,
                                   @NonNull final Observer<? super List<ChatMessage>> theObserver) {
        getOrCreateMapEntry(theChatId).observe(theOwner, theObserver);
    }

    /**
     * Return a reference to the List<> associated with the chat room. If the View Model does
     * not have a mapping for this chatID, it will be created.
     *
     * WARNING: While this method returns a reference to a mutable list, it should not be
     * mutated externally in client code. Use public methods available in this class as
     * needed.
     *
     * @param theChatId the id of the chat room List to retrieve
     * @return a reference to the list of messages
     */
    public List<ChatMessage> getMessageListByChatId(final int theChatId) {
        return getOrCreateMapEntry(theChatId).getValue();
    }

    /**
     * Provides the chat list for a provided chat id. If a chat does not exist, a new one is
     * created.
     *
     * @param theChatId the chat id
     * @return the corresponding chat list
     */
    private MutableLiveData<List<ChatMessage>> getOrCreateMapEntry(final int theChatId) {
        if(!mMessages.containsKey(theChatId)) {
            mMessages.put(theChatId, new MutableLiveData<>(new ArrayList<>()));
        }
        return mMessages.get(theChatId);
    }

    /**
     * Makes a request to the web service to get the first batch of messages for a given Chat Room.
     * Parses the response and adds the ChatMessage object to the List associated with the
     * ChatRoom. Informs observers of the update.
     *
     * Subsequent requests to the web service for a given chat room should be made from
     * getNextMessages()
     *
     * @param theChatId the chatroom id to request messages of
     * @param theJwt the users signed JWT
     */
    public void getFirstMessages(final int theChatId, final String theJwt) {
        final String url = "https://team-1-tcss-450-server.herokuapp.com/messages/" + theChatId;

        final Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleSuccess,
                this::handleError) {

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
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);

        //code here will run
    }

    /**
     * Makes a request to the web service to get the next batch of messages for a given Chat Room.
     * This request uses the earliest known ChatMessage in the associated list and passes that
     * messageId to the web service.
     * Parses the response and adds the ChatMessage object to the List associated with the
     * ChatRoom. Informs observers of the update.
     *
     * Subsequent calls to this method receive earlier and earlier messages.
     *
     * @param theChatId the chatroom id to request messages of
     * @param theJwt the users signed JWT
     */
    public void getNextMessages(final int theChatId, final String theJwt) {

        final String url = "https://team-1-tcss-450-server.herokuapp.com/messages/"
                + theChatId + "/" + mMessages.get(theChatId).getValue().get(0).getMessageId();
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleSuccess,
                this::handleError) {

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
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * When a chat message is received externally to this ViewModel, add it
     * with this method.
     *
     * @param theChatId the chat id
     * @param theMessage the message to be added
     */
    public void addMessage(final int theChatId, final ChatMessage theMessage) {
        final List<ChatMessage> list = getMessageListByChatId(theChatId);

        // there are some instances where a message may be added to the message list due
        // to navigation BEFORE a pushy notification makes it to the app.
        // In those cases, we should prevent that message from happening twice
        if (list.size() > 0 && theMessage.getMessageId() != list.get(list.size() - 1).getMessageId()) {
            list.add(theMessage);
            getOrCreateMapEntry(theChatId).setValue(list);
        }
    }

    /**
     * Handles a successful response from hitting the chat list server endpoint
     *
     * @param theResponse the JSONObject response
     */
    private void handleSuccess(final JSONObject theResponse) {
        List<ChatMessage> list;
        if (!theResponse.has("chatId")) {
            throw new IllegalStateException("Unexpected response in ChatViewModel: " + theResponse);
        }
        try {
            list = getMessageListByChatId(theResponse.getInt("chatId"));
            JSONArray messages = theResponse.getJSONArray("rows");
            for(int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                ChatMessage cMessage = new ChatMessage(
                        message.getInt("messageid"),
                        message.getString("message"),
                        message.getString("nickname"),
                        message.getString("timestamp")
                );
                if (!list.contains(cMessage)) {
                    // don't add a duplicate
                    list.add(0, cMessage);
                } else {
                    // this shouldn't happen but could with the asynchronous
                    // nature of the application
                    Log.wtf("Chat message already received",
                            "Or duplicate id:" + cMessage.getMessageId());
                }

            }
            //inform observers of the change (setValue)
            getOrCreateMapEntry(theResponse.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    /**
     * Handles unsuccessful requests to the chat list server endpoint
     *
     * @param theError the returned error
     */
    private void handleError(final VolleyError theError) {
        Log.d("GET NEXT MESSAGES", "HANDLE ERROR");
        if (Objects.isNull(theError.networkResponse)) {
            Log.e("NETWORK ERROR", theError.getMessage());
        }
        else {
            String data = new String(theError.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    theError.networkResponse.statusCode +
                            " " +
                            data);
        }
    }
}
