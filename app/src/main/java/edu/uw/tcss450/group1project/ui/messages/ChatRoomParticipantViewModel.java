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
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.tcss450.group1project.ui.contacts.Contact;

/**
 * ChatRoomParticipantViewModel is a class for storing data pertaining to chat room
 * participant additions and selections.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChatRoomParticipantViewModel extends AndroidViewModel {

    /** The JSONObject response assigned to chat room participant additions */
    private MutableLiveData<JSONObject> mAddParticipantsResponse;

    /** The JSONObject response assigned to chat room participant "gets" */
    private MutableLiveData<JSONObject> mGetParticipantsResponse;

    /** The JSONObject response assigned to leaving a particular chat room */
    private MutableLiveData<JSONObject> mLeaveRoomResponse;

    /** The list of current participants */
    private List<Contact> mParticipants;

    /** The list of selected participants to be added */
    private Set<Contact> mSelected;

    /**
     * Constructs a new ChatRoomParticipantViewModel with the provided application
     *
     * @param theApplication the application
     */
    public ChatRoomParticipantViewModel(@NonNull final Application theApplication) {
        super(theApplication);
        mAddParticipantsResponse = new MutableLiveData<>();
        mAddParticipantsResponse.setValue(new JSONObject());
        mGetParticipantsResponse = new MutableLiveData<>();
        mGetParticipantsResponse.setValue(new JSONObject());
        mLeaveRoomResponse = new MutableLiveData<>();
        mLeaveRoomResponse.setValue(new JSONObject());
        mParticipants = new ArrayList<>();
        mSelected = new HashSet<>();
    }

    /**
     * Provides the set of contacts selected to be added
     *
     * @return the set of contacts
     */
    public Set<Contact> getSelected() {
        return mSelected;
    }

    /**
     * Provides a copy of the list of current participants
     *
     * @return the list of current participants
     */
    public List<Contact> getParticipants() {
        return new LinkedList<>(mParticipants);
    }

    /**
     * Clears the response associated with current participant "get" requests
     */
    public void clearGetResponse() {
        mGetParticipantsResponse.setValue(new JSONObject());
    }

    /**
     * Clears the response associated with new participant addition requests
     */
    public void clearAddResponse() {
        mAddParticipantsResponse.setValue(new JSONObject());
    }

    /**
     * Clears the response associated with leaving a specific chat room
     */
    public void clearLeaveResponse() {
        mLeaveRoomResponse.setValue(new JSONObject());
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
     * Adds an observer to the JSONObject response assigned to chat room participant retrieval
     *
     * @param theOwner the lifecycle owner
     * @param theObserver the observer to be assigned
     */
    public void addGetParticipantsResponseObserver(
            @NonNull final LifecycleOwner theOwner,
            @NonNull final Observer<? super JSONObject> theObserver) {
        mGetParticipantsResponse.observe(theOwner, theObserver);
    }

    /**
     * Adds an observer to the JSONObject response assigned to leaving a chat room
     *
     * @param theOwner the lifecycle owner
     * @param theObserver the observer to be assigned
     */
    public void addLeaveRoomResponseObserver(
            @NonNull final LifecycleOwner theOwner,
            @NonNull final Observer<? super JSONObject> theObserver) {
        mLeaveRoomResponse.observe(theOwner, theObserver);
    }

    /**
     * Creates a request to add this view model's set of selected
     * participants to an existing chat room.
     *
     * @param theJwt the JWT of the user
     * @param theNickname the nickname of the user adding ot the chat room
     * @param theChatRoomId the id of the chat room to be added to
     */
    public void connectAddParticipants(final String theJwt,
                                       final String theNickname,
                                       final int theChatRoomId) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/chats";
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("memberIds", createMemberIdArray(mSelected));
        bodyMap.put("chatId", theChatRoomId);
        bodyMap.put("message", constructAddMessage(mSelected, theNickname));
        JSONObject body = new JSONObject(bodyMap);

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                mAddParticipantsResponse::setValue,
                this::handleAddParticipantsError) {
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
     * Creates a request to retrieve the set of existing participants in a given chat room.
     *
     * @param theJwt the JWT of the user
     * @param theChatRoomId the id of the chat room
     */
    public void connectGetParticipants(final String theJwt, final int theChatRoomId) {
        String url =
                "https://team-1-tcss-450-server.herokuapp.com/chats/chat_members/" + theChatRoomId;
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::handleGetParticipantsSuccess,
                this::handleGetParticipantsError) {
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
     * Creates a request to leave a particular chat room
     *
     * @param theJwt the user's JWT
     * @param theChatRoomId the id of the chat room to be left
     * @param theEmail the user's email
     * @param theNickname the user's nickname
     */
    public void connectLeaveRoom(final String theJwt, final int theChatRoomId,
                                 final String theEmail, final String theNickname) {
        String message = theNickname + " has left the chat room.";
        String base = "https://team-1-tcss-450-server.herokuapp.com/chats";
        String url = String.format("%s/%d/%s/%s", base, theChatRoomId, theEmail, message);
        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                mLeaveRoomResponse::setValue,
                this::handleLeaveError) {
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

    private void handleLeaveError(final VolleyError theError) {
        Map<String, Object> map = new HashMap<>();
        if (theError.networkResponse != null) {
            map.put("code", String.valueOf(theError.networkResponse.statusCode));
            try {
                String data = new String(theError.networkResponse.data, Charset.defaultCharset())
                        .replace('\"', '\'');
                map.put("data", theError.networkResponse.data == null ? new JSONObject() :
                        new JSONObject(data));
            } catch (JSONException ex) {
                Log.e("JSON PARSE ERROR IN LEAVE ROOM ERROR HANDLER", ex.getMessage());
            }
        }
        mLeaveRoomResponse.setValue(new JSONObject(map));
    }

    /**
     * Handles a successful response for retrieving a chat room's current participants
     *
     * @param theResponse the server response
     */
    private void handleGetParticipantsSuccess(final JSONObject theResponse) {
        List<Contact> participants = new ArrayList<>();
        try {
            JSONArray participantArray = theResponse.getJSONArray("chatMembersList");
            for (int i = 0; i < participantArray.length(); i++) {
                JSONObject participant = (JSONObject) participantArray.get(i);
                Contact contact = new Contact(
                     participant.getString("firstname"),
                     participant.getString("lastname"),
                     participant.getString("nickname"),
                     participant.getString("memberid")
                );
                participants.add(contact);
            }
            mParticipants = participants;
            mGetParticipantsResponse.setValue(theResponse);
        } catch (JSONException ex) {
            Map<String, String> map = new HashMap<>();
            map.put("code", "JSON parse error: " + ex.getMessage());
            mGetParticipantsResponse.setValue(new JSONObject(map));
            ex.printStackTrace();
        }
    }

    /**
     * Handles an unsuccessful response for retrieving a chat room's current participants
     *
     * @param theError the returned error
     */
    private void handleGetParticipantsError(final VolleyError theError) {
        Map<String, Object> map = new HashMap<>();
        if (theError.networkResponse != null) {
            map.put("code", String.valueOf(theError.networkResponse.statusCode));
            try {
                String data = new String(theError.networkResponse.data, Charset.defaultCharset())
                        .replace('\"', '\'');
                map.put("data", theError.networkResponse.data == null ? new JSONObject() :
                        new JSONObject(data));
            } catch (JSONException ex) {
                Log.e("JSON PARSE ERROR IN GETPARTICIPANTS ERROR HANDLER", ex.getMessage());
            }
        }
        mGetParticipantsResponse.setValue(new JSONObject(map));
    }

    /**
     * Handles an unsuccessful response for adding participants to a chat room
     *
     * @param theError the returned error
     */
    private void handleAddParticipantsError(final VolleyError theError) {
        Map<String, Object> map = new HashMap<>();
        if (theError.networkResponse != null) {
            map.put("code", String.valueOf(theError.networkResponse.statusCode));
            try {
                String data = new String(theError.networkResponse.data, Charset.defaultCharset())
                        .replace('\"', '\'');
                map.put("data", theError.networkResponse.data == null ? new JSONObject() :
                        new JSONObject(data));
            } catch (JSONException ex) {
                Log.e("JSON PARSE ERROR IN ADDPARTICIPANTS ERROR HANDLER", ex.getMessage());
            }
        }
        mAddParticipantsResponse.setValue(new JSONObject(map));
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
     * Creates an chat room message informing participants of added members
     *
     * @param theParticipants the participants to be added
     * @param theNickname the nickname of the user adding to the chat room
     * @return the formatted message String
     */
    private String constructAddMessage(final Set<Contact> theParticipants,
                                       final String theNickname) {
        StringBuilder builder = new StringBuilder();
        int size = theParticipants.size();
        builder.append(theNickname + " added ");
        int i = 0;
        for (Contact cont : theParticipants) {
            if (i < size - 1) {
                builder.append(cont.getNickname() + (size == 2 ? " and " : ", "));
            } else {
                builder.append((size > 2 ? "and " : "") + cont.getNickname());
            }
            i++;
        }
        builder.append(" to the chat room.");
        return builder.toString();
    }
}
