/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A view model class that stores information about Contacts received from the server.
 *
 * @author Steven Omegna
 * @version Fall 2021
 */
public class ContactsViewModel extends AndroidViewModel {

    /**
     * The {@link MutableLiveData} that stores the JSON response from the server
     * when the user contact requests.
     */
    private MutableLiveData<JSONObject> mRequestResponse;

    /**
     * The {@link MutableLiveData} that stores the JSON response from the server
     * when the user gets contacts from the server.
     */
    private MutableLiveData<JSONObject> mContactResponse;


    /**
     * The {@link MutableLiveData} that stores the JSON response from the server
     * when the user gets contacts from the server.
     */
    private MutableLiveData<JSONObject> mDeleteResponse;

    /** The live data that stores the list of Contact objects. This should be observed. */
    private List<Contact> mContactList;

    /**
     * Creates a new ContactViewModel that is tied to the given application.
     *
     * @param theApplication the Application this ViewModel belongs to
     * @throws NullPointerException if theApplication is null
     */
    public ContactsViewModel(@NonNull final Application theApplication) {
        super(Objects.requireNonNull(theApplication, "theApplication can not be null"));
        mContactList = new ArrayList<>();

        mRequestResponse = new MutableLiveData<>();
        mRequestResponse.setValue(new JSONObject());

        mContactResponse = new MutableLiveData<>();
        mContactResponse.setValue(new JSONObject());

        mDeleteResponse = new MutableLiveData<>();
        mDeleteResponse.setValue(new JSONObject());

    }



    /**
     * Adds the given observer to the Contact list live data.
     *
     * @param theOwner the lifecycle owner of the fragment that contains the observer
     * @param theObserver the observer that is used when the response data changes state
     * @throws NullPointerException if theOwner is null
     * @throws NullPointerException if theObserver is null
     */
    public void addContactListObserver(@NonNull final LifecycleOwner theOwner,
                                    @NonNull final Observer<? super JSONObject> theObserver) {
        Objects.requireNonNull(theOwner, "theOwner can not be null");
        Objects.requireNonNull(theObserver, "theObserver can not be null");
        mContactResponse.observe(theOwner, theObserver);
    }


    /**
     * Adds the given observer to the contact request live data.
     *
     * @param theOwner the lifecycle owner of the fragment that contains the observer
     * @param theObserver the observer that is used when the response data changes state
     * @throws NullPointerException if theOwner is null
     * @throws NullPointerException if theObserver is null
     */
    public void addContactRequestObserver(@NonNull final LifecycleOwner theOwner,
                                    @NonNull final Observer<? super JSONObject> theObserver) {
        Objects.requireNonNull(theOwner, "theOwner can not be null");
        Objects.requireNonNull(theObserver, "theObserver can not be null");
        mRequestResponse.observe(theOwner, theObserver);
    }


    /**
     * Adds the given observer to the contact Delete request live data.
     *
     * @param theOwner the lifecycle owner of the fragment that contains the observer
     * @param theObserver the observer that is used when the response data changes state
     * @throws NullPointerException if theOwner is null
     * @throws NullPointerException if theObserver is null
     */
    public void addContactDeleteObserver(@NonNull final LifecycleOwner theOwner,
                                          @NonNull final Observer<? super JSONObject> theObserver) {
        Objects.requireNonNull(theOwner, "theOwner can not be null");
        Objects.requireNonNull(theObserver, "theObserver can not be null");
        mDeleteResponse.observe(theOwner, theObserver);
    }


    /**
     * Sends an HTTP GET request to the server attempting to get all contacts
     *
     * @param theJwt JWT token to be passed to server
     * @throws NullPointerException if theNickname is null

     */
    public void contactsConnect(@NonNull final String theJwt) {
        final String url = "https://team-1-tcss-450-server.herokuapp.com/contacts/";

        final Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                this::parseContactsListData,
                this::handleContactError){
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
     * Sends an HTTP DELETE request to the server attempting to delete contact
     *
     * @param theMemberId the Id of the contact to be deleted
     * @param theJwt JWT token to be passed to server
     * @throws NullPointerException if theNickname is null
     */
    public void sendDeleteResponse(final String theJwt, final String theMemberId) {
        Objects.requireNonNull(theMemberId, "theMemberId can not be null");
        final String url = "https://team-1-tcss-450-server.herokuapp.com/contacts/" + theMemberId;

        final JSONObject body = new JSONObject();
        try {
            body.put("memberId", theMemberId);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        final Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                mRequestResponse::setValue,
                this::handleRequestError){
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
     * Returns the current list of contacts
     * @return the contact list
     */
    public List<Contact> getContactList() {
        return new LinkedList<>(mContactList);
    }

    /**
     * Determines if this view model's data contacts are readable (i.e. non-null)
     *
     * @return true if readable, false otherwise
     */
    public boolean containsReadableContacts() {
        return mContactList != null;
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, parse the contact information.
     *
     * @param theResponse the Response from the server
     */
    private void parseContactsListData(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            List<Contact> formattedContactList = new ArrayList<>();
            if (theResponse.has("code")) {
                //this is an error
            } else {
                try {
                    JSONArray contacts = theResponse.getJSONArray("data");
                    for(int i = 0; i < contacts.length(); i++) {
                        JSONObject contact = (JSONObject) contacts.get(i);
                        formattedContactList.add(new Contact(contact.get("first").toString(),
                                contact.get("last").toString(),
                                contact.get("nickname").toString(),
                                contact.get("memberid").toString()));
                    }
                    mContactList = formattedContactList;
                    mContactResponse.setValue(theResponse);
                } catch (JSONException exception) {
                    Log.e("JSON Parse Error", exception.getMessage());
                }
            }
        } else {
            Log.d("Registration JSON Response", "No Response");
        }
    }

    /**
     * Sends an HTTP POST request to the server attempting to create a contact request
     * corresponding to the given information provided.
     *
     * @param theNickname the new account's nickname
     * @param theJwt JWT token to be passed to server
     * @throws NullPointerException if theNickname is null
     */
    public void requestConnect(@NonNull final String theNickname, @NonNull final String theJwt) {
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
                mRequestResponse::setValue,
                this::handleRequestError){
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
     * Completes the actions required when an error occurs during a HTTP request to the server
     * for contacts.
     *
     * @param theError the error that occurred
     */
    private void handleContactError(final VolleyError theError) {
        if (Objects.isNull(theError.networkResponse)) {
            try {
                mContactResponse.setValue(new JSONObject("{" +
                        "error:\"" + theError.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        } else {
            String data = new String(theError.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                JSONObject response = new JSONObject();
                response.put("code", theError.networkResponse.statusCode);
                response.put("data", new JSONObject(data));
                mContactResponse.setValue(response);
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }

    }

    /**
     * Completes the actions required when an error occurs during a HTTP request to the server
     * for contact requests.
     *
     * @param theError the error that occurred
     */
    private void handleRequestError(final VolleyError theError) {

            if (Objects.isNull(theError.networkResponse)) {
                try {
                    mRequestResponse.setValue(new JSONObject("{" +
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
                    mRequestResponse.setValue(response);
                } catch (JSONException e) {
                    Log.e("JSON PARSE", "JSON Parse Error in handleError");
                }
            }

        }

        /**
         * Clears the data stored in this view model.
         */
        public void removeData() {
            mRequestResponse.setValue(new JSONObject());
        }

}

