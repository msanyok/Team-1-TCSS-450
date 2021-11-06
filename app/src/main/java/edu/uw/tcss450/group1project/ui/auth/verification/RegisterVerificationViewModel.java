/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.auth.verification;

import android.app.AlertDialog;
import android.app.Application;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import android.widget.inline.InlineContentView;

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
 * verifying a user's account.
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public class RegisterVerificationViewModel extends AndroidViewModel {

    /**
     * The {@link MutableLiveData} that stores the JSON response from the server
     * when the user tries to verify their account.
     */
    private MutableLiveData<JSONObject> mResponse;

    /**
     * Creates a new RegisterVerificationViewModel that is tied to the given application.
     *
     * @param theApplication the Application this ViewModel belongs to
     * @throws NullPointerException if theApplication is null
     */
    public RegisterVerificationViewModel(@NonNull final Application theApplication) {
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
     * Sends an HTTP POST request to the server attempting to validate
     * the account that corresponds to the given email using the
     * given verification code.
     *
     * @param theEmail the user's email attatched to their account
     * @param theCode  the user's inputted verification code
     * @throws NullPointerException if theEmail is null
     * @throws NullPointerException if theCode is null
     */
    public void connect(final String theEmail, final String theCode) {
        Objects.requireNonNull(theEmail, "theEmail can not be null");
        Objects.requireNonNull(theCode, "theCode can not be null");

        final String url = "https://team-1-tcss-450-server.herokuapp.com/auth/verify";

        final JSONObject body = new JSONObject();
        try {
            body.put("email", theEmail);
            body.put("code", theCode);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        final Request request = new JsonObjectRequest(
                Request.Method.POST,
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
     * Clear the live data stored in this view model
     */
    public void removeData() {
        mResponse.setValue(new JSONObject());
    }


    /**
     * Sends a request to the server asking
     * it to send a new verification code to the given email.
     * @param theEmail the user's email
     * @throws NullPointerException if theEmail is null
     */
    protected void sendResendCodeRequest(final String theEmail) {
        Objects.requireNonNull(theEmail, "theEmail can not be null");

        // todo: UPDATE URL
        final String url = "https://team-1-tcss-450-server.herokuapp.com/auth/resendcode";

        // todo: update json body
        final JSONObject body = new JSONObject();
        try {
            body.put("email", theEmail);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        // should the UI update if the code fails to send?

        final Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handleResendSuccess,
                this::handleResendError);

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue

        // todo: uncomment request send and delete log
//        Volley.newRequestQueue(getApplication().getApplicationContext())
//                .add(request);
        Log.d("TO BE DELETED", "WILL SEND RESEND CODE REQUEST, EMAIL: " + theEmail);
        handleResendSuccess(null); // delete

    }



    // todo: do what should happen when the code resend is successful
    private void handleResendSuccess(JSONObject theJsonObject) {

        final Toast successToast = new Toast(this.getApplication().getApplicationContext());
        successToast.setText("Email sent!");
        successToast.setDuration(Toast.LENGTH_SHORT);
        successToast.show();



    }

    // todo: do what should happen when the code resend fails
    private void handleResendError(VolleyError theVolleyError) {
        final Toast successToast = new Toast(this.getApplication().getApplicationContext());
        successToast.setText("Email failed to send");
        successToast.setDuration(Toast.LENGTH_SHORT);
        successToast.show();
    }

}
