/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.app.Application;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.group1project.io.RequestQueueSingleton;
import me.pushy.sdk.Pushy;

/**
 * A View Model designed to retrieve and store the PUSHY token for a given application.
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @author Chris Ding
 */
public class PushyTokenViewModel extends AndroidViewModel{

    /** The Pushy token for this program. */
    private final MutableLiveData<String> mPushyToken;

    /** The Json response from the http request. */
    private final MutableLiveData<JSONObject> mResponse;

    /**
     * Creates a new Pushy token view model
     *
     * @param theApplication the application this view model belongs to
     * @throws NullPointerException if theApplication is null
     */
    public PushyTokenViewModel(@NonNull final Application theApplication) {
        super(Objects.requireNonNull(theApplication, "theApplication can not be null"));
        mPushyToken = new MutableLiveData<>();
        mPushyToken.setValue("");
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Register as an observer to listen for the PushToken.
     *
     * @param theOwner the fragments lifecycle owner
     * @param theObserver the observer
     * @throws NullPointerException if theOwner is null
     * @throws NullPointerException if theObserver is null
     */
    public void addTokenObserver(@NonNull final LifecycleOwner theOwner,
                                 @NonNull final Observer<? super String> theObserver) {
        mPushyToken.observe(
                Objects.requireNonNull(theOwner, "theOwner can not be null"),
                Objects.requireNonNull(theObserver, "theObserver can not be null"));
    }

    /**
     * Register as an observer to listen for the Json response.
     *
     * @param theOwner the fragments lifecycle owner
     * @param theObserver the observer
     * @throws NullPointerException if theOwner is null
     * @throws NullPointerException if theObserver is null
     */
    public void addResponseObserver(@NonNull final LifecycleOwner theOwner,
                                    @NonNull final Observer<? super JSONObject> theObserver) {
        mResponse.observe(
                Objects.requireNonNull(theOwner, "theOwner can not be null"),
                Objects.requireNonNull(theObserver, "theObserver can not be null"));
    }

    /**
     * Retrieves a PUSHY token and stores it in the model.
     */
    public void retrieveToken() {
        if (!Pushy.isRegistered(getApplication().getApplicationContext())) {

            Log.d("PUSH VIEW MODEL", "FETCHING NEW TOKEN");
            new RegisterForPushNotificationsAsync().execute();

        } else {
            Log.d("PUSH VIEW MODEL", "USING OLD TOKEN");
            mPushyToken.setValue(
                    Pushy.getDeviceCredentials(getApplication().getApplicationContext()).token);
        }
    }

    /**
     * This is the method described in the Pushy documentation.
     *
     * Note the Android class AsyncTask is deprecated as of Android Q.
     * It is fine to use here and for project.
     * In future Android development, look for an alternative solution.
     */
    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, String> {
        /**
         * Returns a unique Pushy token to this device.
         *
         * @param theParams unused
         * @return the token
         */
        protected String doInBackground(final Void... theParams) {
            String deviceToken;
            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getApplication().getApplicationContext());
            }
            catch (Exception e) {
                // Return exc to onPostExecute
                return e.getMessage();
            }
            // Success
            return deviceToken;
        }

        @Override
        protected void onPostExecute(final String theToken) {
            if (theToken.isEmpty()) {
                // Show error in log - You should add error handling for the user.
                Log.e("ERROR RETRIEVING PUSHY TOKEN", theToken);
            } else {
                mPushyToken.setValue(theToken);
            }
        }
    }

    /**
     * Sends this Pushy device's token to the web service.
     *
     * @param theJwt the Jwt used for authorizing the current user
     * @throws IllegalStateException when this method is called before the token is retrieve
     */
    public void sendTokenToWebservice(final String theJwt) {
        if (mPushyToken.getValue().isEmpty()) {
            throw new IllegalStateException("No pushy token. Do NOT call until token is retrieved");
        }

        // TODO: consider using R.string.base_url for project urls? probably a good idea...
        final String url = "https://team-1-tcss-450-server.herokuapp.com/auth";
//                getApplication().getResources().getString(R.string.base_url) + "auth";

        final JSONObject body = new JSONObject();
        try {
            body.put("token", mPushyToken.getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body, //push token found in the JSONObject body
                mResponse::setValue,
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
     * Handles any errors that occur when trying to send
     * the server this application's Pushy token.
     *
     * @param theError the error that occurred
     */
    private void handleError(final VolleyError theError) {
        Log.wtf("WTF", "SIGN IN FAILED");
        Log.wtf("WTF", theError.getMessage());
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
            String data = new String(theError.networkResponse.data, Charset.defaultCharset());
            try {
                mResponse.setValue(new JSONObject("{" +
                        "code:" + theError.networkResponse.statusCode +
                        ", data:" + data +
                        "}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /**
     * Deletes the pushy token for this user from the database so push messages are not received
     *
     * @param theJwt the user's jwt
     */
    public void deleteTokenFromWebservice(final String theJwt) {
        final String url = "https://team-1-tcss-450-server.herokuapp.com/auth";
        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                mResponse::setValue,
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
}