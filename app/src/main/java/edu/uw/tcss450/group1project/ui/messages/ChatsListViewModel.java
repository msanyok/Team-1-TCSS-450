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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * An {@link AndroidViewModel} child class that gets, parses, and stores
 * data about a user's list of current chats.
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ChatsListViewModel extends AndroidViewModel {

    /** The live data that stores the chats/list/ JSON response. */
    private MutableLiveData<JSONObject> mResponse;

    /** The live data that stores the list of ChatRoom objects. This should be observed. */
    private List<ChatRoom> mChatRoomList;

    /**
     * Creates a new Chats view model with default values
     *
     * @param theApplication the app this view model is assigned to
     * @throws NullPointerException if theApplication is null
     */
    public ChatsListViewModel(@NonNull final Application theApplication) {
        super(Objects.requireNonNull(theApplication, "theApplication can not be null."));
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
        mChatRoomList = new ArrayList<>();
    }

    /**
     * Adds the given observer to the JSON response object.
     *
     * @param theOwner the lifecycle owner of the fragment that contains the observer
     * @param theObserver the observer that is used when the response data changes state
     * @throws NullPointerException if theOwner is null
     * @throws NullPointerException if theObserver is null
     */
    public void addResponseObserver(@NonNull final LifecycleOwner theOwner,
                                    @NonNull final Observer<? super JSONObject> theObserver) {
        Objects.requireNonNull(theOwner, "theOwner can not be null");
        Objects.requireNonNull(theObserver, "theObserver can not be null");
        mResponse.observe(theOwner, theObserver);
    }

    /**
     * Sends an HTTP GET request to the server attempting to get data about the list of chats
     * the current user is a member in.
     */
    public void getChatListData(final String theJwt) {
        final String url = "https://team-1-tcss-450-server.herokuapp.com/chats/all";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
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
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    /**
     * Returns the current list of ChatRooms
     * @return
     */
    public List<ChatRoom> getChatList() {
        return mChatRoomList;
    }

    /**
     * Handles the JSON response object that is sent back from a successful
     * chat list endpoint request.
     *
     * @param theResponse the response sent back from the http request
     * @throws NullPointerException if theResponse is null
     */
    private void handleSuccess(final JSONObject theResponse) {
        Objects.requireNonNull(theResponse, "theResponse can not be null");

        // parse the response and turn it into a new ChatRoom list
        mChatRoomList = new ArrayList<>();


        try {
            JSONArray chats = theResponse.getJSONArray("data");
            for (int i = 0; i < chats.length(); i++) {

                // the names of the get(...) fields are determined
                // by the server and can be found in the documentation
                JSONObject chat = (JSONObject) chats.get(i);
                mChatRoomList.add(new ChatRoom(chat.get("chat_name").toString(),
                        chat.get("chatid").toString(),
                        chat.get("message").toString(),
                        chat.get("timestamp").toString()));
            }

            // once the list has been repopulated, sort the chat rooms based on the timestamp
            // of the most recent message sent
            Collections.sort(mChatRoomList);


        } catch (JSONException exception) {
            // should we do something specific here if the json isn't parsed properly/
            exception.printStackTrace();
        }

        // set the response value AFTER setting the new list of chat rooms
        mResponse.setValue(theResponse);
    }

    /**
     * Completes the actions required when an error occurs during a HTTP request to the server.
     *
     * @param theError the error that occurred
     */
    private void handleError(final VolleyError theError) {
        if (Objects.isNull(theError.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + theError.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(theError.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                JSONObject response = new JSONObject();
                response.put("code", theError.networkResponse.statusCode);
                response.put("data", new JSONObject(data));
                mResponse.setValue(response);
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

}
