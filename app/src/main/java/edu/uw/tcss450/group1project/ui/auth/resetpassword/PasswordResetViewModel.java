/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */
package edu.uw.tcss450.group1project.ui.auth.resetpassword;

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
import java.util.Objects;

/**
 * An {@link AndroidViewModel} child class that handles the data related to
 * verify code and reset password.
 *
 * @author Chris Ding
 * @author Austn Attaway
 * @version Fall 2021
 */
public class PasswordResetViewModel extends AndroidViewModel {

    /**
     * The {@link MutableLiveData} that stores the JSON response from the server
     * when the user tries to reset their password.
     */
    private MutableLiveData<JSONObject> mResponse;

    /**
     * Creates a new PasswordResetViewModel that is tied to the given application.
     *
     * @param theApplication the Application this ViewModel belongs to
     */
    public PasswordResetViewModel(@NonNull final Application theApplication) {
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
     * the account that corresponds to the given email using the
     * given verification code, and reset the password.
     *
     * @param theEmail the user's email attached to their account
     * @param thePassword the user's password attached to their account
     * @param theCode  the user's inputted verification code
     * @throws NullPointerException if theEmail is null
     * @throws NullPointerException if thePassword is null
     * @throws NullPointerException if theCode is null
     */
    public void connect(@NonNull final String theEmail,
                        @NonNull final String thePassword,
                        @NonNull final String theCode) {

        Objects.requireNonNull(theEmail, "theEmail can not be null");
        Objects.requireNonNull(thePassword, "thePassword can not be null");
        Objects.requireNonNull(theCode, "theCode can not be null");


        final String url = "https://team-1-tcss-450-server.herokuapp.com/password/reset";

        final JSONObject body = new JSONObject();
        try {
            body.put("email", theEmail);
            body.put("newPassword", thePassword);
            body.put("code", theCode);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        final Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body,
                mResponse::setValue,
                this::handleError);

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

    /**
     * Clears the data stored in this view model.
     */
    public void removeData() {
        mResponse.setValue(new JSONObject());
    }
}
