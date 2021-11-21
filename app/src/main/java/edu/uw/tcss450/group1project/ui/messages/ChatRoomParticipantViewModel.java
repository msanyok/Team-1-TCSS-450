/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.app.Application;

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

import java.util.Arrays;
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

    private MutableLiveData<JSONObject> mCreateRoomResponse;

    private MutableLiveData<JSONObject> mAddParticipantsResponse;

    public ChatRoomParticipantViewModel(@NonNull final Application theApplication) {
        super(theApplication);
        mCreateRoomResponse = new MutableLiveData<>();
        mAddParticipantsResponse = new MutableLiveData<>();
        mCreateRoomResponse.setValue(new JSONObject());
        mAddParticipantsResponse.setValue(new JSONObject());
    }

    public void addChatRoomCreationResponseObserver(
            @NonNull final LifecycleOwner theOwner,
            @NonNull final Observer<? super JSONObject> theObserver) {
        mCreateRoomResponse.observe(theOwner, theObserver);
    }

    public void addParticipantAdditionResponseObserver(
            @NonNull final LifecycleOwner theOwner,
            @NonNull final Observer<? super JSONObject> theObserver) {
        mAddParticipantsResponse.observe(theOwner, theObserver);
    }

    public void createChatRoom(final String theJwt, final String theRoomName,
                               final Set<Contact> theParticipants) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/chats";

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("name", theRoomName);
        bodyMap.put("memberIds", createMemberIdArray(theParticipants));
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

    public void addParticipants(final String theJwt, final int theChatRoomId,
                                final Set<Contact> theParticipants) {

    }

    private JSONArray createMemberIdArray(final Set<Contact> theParticipants) {
        JSONArray array = new JSONArray();
        for (Contact cont : theParticipants) {
            array.put(cont.getmMemberid());
        }
        return array;
    }

    private void handleRoomCreationError(final VolleyError theError) {

    }



//    /** The set of added participants */
//    private final MutableLiveData<Set<Contact>> mParticipants;
//
//    /**
//     * Creates a new ChatRoomParticipantViewModel
//     */
//    public ChatRoomParticipantViewModel() {
//        mParticipants = new MutableLiveData<>();
//        mParticipants.setValue(new HashSet<>());
//    }
//
//    /**
//     * Adds a new participant to this view model
//     *
//     * @param theContact the participant to be added
//     */
//    public void addParticipant(final Contact theContact) {
//        mParticipants.getValue().add(theContact);
//    }
//
//    /**
//     * Removes a new participant to this view model
//     *
//     * @param theContact the participant to be removed
//     */
//    public void removeParticipant(final Contact theContact) {
//        mParticipants.getValue().remove(theContact);
//    }
//
//    /**
//     * Returns whether this view model contains a provided participant
//     *
//     * @param theContact the participant in question
//     * @return true if the participant is found, false otherwise
//     */
//    public boolean containsParticipant(final Contact theContact) {
//        return mParticipants.getValue().contains(theContact);
//    }
}
