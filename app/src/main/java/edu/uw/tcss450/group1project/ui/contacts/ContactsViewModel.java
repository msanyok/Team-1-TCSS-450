package edu.uw.tcss450.group1project.ui.contacts;

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

public class ContactsViewModel extends AndroidViewModel {

        /**
         * The {@link MutableLiveData} that stores the JSON response from the server
         * when the user tries to register an account.
         */
        private MutableLiveData<JSONObject> mResponse;

        /**
         * Creates a new RegisterViewModel that is tied to the given application.
         *
         * @param theApplication the Application this ViewModel belongs to
         * @throws NullPointerException if theApplication is null
         */
        public ContactsViewModel(@NonNull final Application theApplication) {
            super(Objects.requireNonNull(theApplication, "theApplication can not be null"));
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

        /**a
         * Sends an HTTP POST request to the server attempting to register a new account
         * corresponding to the given information provided.
         *
         * @param theNickname the new account's nickname
         * @throws NullPointerException if theNickname is null

         */
        public void connect(@NonNull final String theNickname, @NonNull final String theJwt) {

            Objects.requireNonNull(theNickname, "theNickname can not be null");

            final String url = "https://team-1-tcss-450-server.herokuapp.com/contacts/requests";

            final JSONObject body = new JSONObject();
            try {
                body.put("nickname", theNickname);
            } catch (JSONException exception) {
                exception.printStackTrace();
            }

            final Request request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    body,
                    mResponse::setValue,
                    this::handleError){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
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

        /**
         * Clears the data stored in this view model.
         */
        public void removeData() {
            mResponse.setValue(new JSONObject());
        }

}

