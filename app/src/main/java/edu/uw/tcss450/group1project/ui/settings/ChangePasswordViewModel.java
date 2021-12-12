
/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */package edu.uw.tcss450.group1project.ui.settings;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An {@link AndroidViewModel} child class that handles the data related to
 * verify the existing password and a create a new one inside app (on settings).
 *
 * @author Chris Ding
 * @author Austn Attaway
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChangePasswordViewModel extends AndroidViewModel {

    /**
     * The {@link MutableLiveData} that stores the JSON response from the server
     * verify the existing password and a create a new one.
     */
    private final MutableLiveData<JSONObject> mResponse;

    /**
     * Creates a new ChangePasswordViewModel that is tied to the given application.
     *
     * @param theApplication the Application this ViewModel belongs to
     */
    public ChangePasswordViewModel(@NonNull Application theApplication) {
        super(Objects.requireNonNull(theApplication, "theApplication can not be null."));
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Adds the given observer to the response live data.
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
     * Sends an HTTP PUT request to the server attempting to validate
     * the account that verify the existing password that user provided
     * and allow them to create a new one.
     *
     * @param theOldPassword the user's email attached to their account
     * @param theNewPassword the user's password attached to their account
     * @param theJwt the users signed JWT
     * @throws NullPointerException if theOldPassword is null
     * @throws NullPointerException if theNewPassword is null
     */
    public void connect(@NonNull final String theOldPassword,
                        @NonNull final String theNewPassword,
                        @NonNull final String theJwt) {

        Objects.requireNonNull(theOldPassword, "theOldPassword can not be null");
        Objects.requireNonNull(theNewPassword, "theNewPassword can not be null");


        final String url = "https://team-1-tcss-450-server.herokuapp.com/password/change";

        final JSONObject body = new JSONObject();
        try {
            body.put("oldPassword", theOldPassword);
            body.put("newPassword", theNewPassword);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        final Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
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
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
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