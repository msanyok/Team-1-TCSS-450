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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.uw.tcss450.group1project.ui.contacts.Contact;

/**
 * ChatRoomParticipantViewModel is a fragment-level view model for creating chat rooms and
 * adding participants to an existing chat room.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChatRoomParticipantViewModel extends AndroidViewModel {

    /** The JSONObject response assigned to chat room creation */
    private MutableLiveData<JSONObject> mCreateRoomResponse;

    /** The JSONObject response assigned to chat room participant additions */
    private MutableLiveData<JSONObject> mAddParticipantsResponse;

    /**
     * Creates a new chat room participant view model
     *
     * @param theApplication the corresponding application
     */
    public ChatRoomParticipantViewModel(@NonNull final Application theApplication) {
        super(theApplication);
        mCreateRoomResponse = new MutableLiveData<>();
        mAddParticipantsResponse = new MutableLiveData<>();
        mCreateRoomResponse.setValue(new JSONObject());
        mAddParticipantsResponse.setValue(new JSONObject());
    }

    /**
     * Adds an observer to the JSONObject response assigned to chat room creation
     *
     * @param theOwner the lifecycle owner
     * @param theObserver the observer to be assigned
     */
    public void addChatRoomCreationResponseObserver(
            @NonNull final LifecycleOwner theOwner,
            @NonNull final Observer<? super JSONObject> theObserver) {
        mCreateRoomResponse.observe(theOwner, theObserver);
    }

    /**
     * Adds an observer to the JSONObject response assigned to chat room participant additions
     *
     * @param theOwner the lifecycle owner
     * @param theObserver the observer to be assigned
     */
    public void addParticipantAdditionResponseObserver(
            @NonNull final LifecycleOwner theOwner,
            @NonNull final Observer<? super JSONObject> theObserver) {
        mAddParticipantsResponse.observe(theOwner, theObserver);
    }

    /**
     * Sends a request to create a new chat room with the provided room name
     *
     * @param theJwt the user's JWT
     * @param theEmail the email of the user creating the chat room
     * @param theRoomName the name of the room to be created
     * @param theParticipants the participants to be added to the room
     */
    public void createChatRoom(final String theJwt, final String theEmail, final String theRoomName,
                               final Set<Contact> theParticipants) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/chats";
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("name", theRoomName);
        bodyMap.put("memberIds", createMemberIdArray(theParticipants));
        bodyMap.put("firstMessage", constructFirstMessage(theParticipants, theEmail));
        JSONObject body = new JSONObject(bodyMap);

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mCreateRoomResponse::setValue,
                this::handleRoomCreationError) {
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
     * Adds a set of participants to an existing chat room.
     *
     * @param theJwt the JWT of the user
     * @param theChatRoomId the id of the chat room to be added to
     * @param theParticipants the participants to be added
     */
    public void addParticipants(final String theJwt, final int theChatRoomId,
                                final Set<Contact> theParticipants) {

    }

    /**
     * Helper method to create a JSONArray with a provided set of participants
     *
     * @param theParticipants the set of participants
     * @return the corresponding JSONArray
     */
    private JSONArray createMemberIdArray(final Set<Contact> theParticipants) {
        JSONArray array = new JSONArray();
        for (Contact cont : theParticipants) {
            array.put(cont.getMemberId());
        }
        return array;
    }

    /**
     * Creates an initial chat room message informing participants of creation
     *
     * @param theParticipants the participants to be added
     * @param theEmail the email of the user creating the chat room
     * @return the formatted message String
     */
    private String constructFirstMessage(final Set<Contact> theParticipants,
                                         final String theEmail) {
        StringBuilder builder = new StringBuilder();
        int size = theParticipants.size();
        if (size == 0) {
            builder.append("Hey! You have created an empty TalkBox chat room.");
        } else {
            builder.append("Hey! " + theEmail + " has created a TalkBox chat room with ");
            int i = 0;
            for (Contact cont : theParticipants) {
                if (i < size - 1) {
                    builder.append(cont.getNickname() + (size == 2 ? " and " : ", "));
                } else {
                    builder.append((size > 2 ? "and " : "") + cont.getNickname());
                }
                i++;
            }
            builder.append(".");
        }
        return builder.toString();
    }

    /**
     * Adds an error code field to the JSONObject response for chat room creation and
     * logs the volley error sent back by a failed server request
     *
     * @param theError the returned volley error
     */
    private void handleRoomCreationError(final VolleyError theError) {
        Log.e("CONTACT CONNECTION ERROR", theError.getLocalizedMessage());
        Map<String, String> map = new HashMap<>();
        map.put("code", "server error");
        mCreateRoomResponse.setValue(new JSONObject(map));
    }
}
