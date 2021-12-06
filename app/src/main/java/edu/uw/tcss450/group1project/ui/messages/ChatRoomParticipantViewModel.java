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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.tcss450.group1project.ui.contacts.Contact;

public class ChatRoomParticipantViewModel extends AndroidViewModel {

    /** The JSONObject response assigned to chat room participant additions */
    private MutableLiveData<JSONObject> mAddParticipantsResponse;

    private MutableLiveData<JSONObject> mGetParticipantsResponse;

    private MutableLiveData<JSONObject> mLeaveRoomResponse;

    private List<Contact> mParticipants;

    private List<Contact> mSelected;

    public ChatRoomParticipantViewModel(@NonNull final Application theApplication) {
        super(theApplication);
        mAddParticipantsResponse = new MutableLiveData<>();
        mAddParticipantsResponse.setValue(new JSONObject());
        mGetParticipantsResponse = new MutableLiveData<>();
        mGetParticipantsResponse.setValue(new JSONObject());
        mLeaveRoomResponse = new MutableLiveData<>();
        mLeaveRoomResponse.setValue(new JSONObject());
        mParticipants = new ArrayList<>();
        mSelected = new ArrayList<>();
    }

    public List<Contact> getSelected() {
        return new LinkedList<>(mSelected);
    }

    public List<Contact> getParticipants() {
        return new LinkedList<>(mParticipants);
    }

    public void setSelected(final List<Contact> theSelected) {
        mSelected = theSelected;
    }

    public boolean containsReadableParticipants() {
        return !mParticipants.isEmpty();
    }

    public void clearGetResponse() {
        mGetParticipantsResponse.setValue(new JSONObject());
    }

    public void clearAddResponse() {
        mAddParticipantsResponse.setValue(new JSONObject());
    }

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
     * Adds a set of participants to an existing chat room.
     *
     * @param theJwt the JWT of the user
     * @param theNickname the nickname of the user adding ot the chat room
     * @param theChatRoomId the id of the chat room to be added to
     * @param theParticipants the participants to be added
     */
    public void connectAddParticipants(final String theJwt,
                                       final String theNickname,
                                       final int theChatRoomId,
                                       final Set<Contact> theParticipants) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/chats";
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("memberIds", createMemberIdArray(theParticipants));
        bodyMap.put("chatId", theChatRoomId);
        bodyMap.put("message", constructAddMessage(theParticipants, theNickname));
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
     * Retrieves the set of existing participants in a given chat room.
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
