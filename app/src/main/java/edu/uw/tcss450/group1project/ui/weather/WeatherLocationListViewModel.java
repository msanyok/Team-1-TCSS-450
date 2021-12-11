/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

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

/**
 * WeatherLocationListViewModel is a class for retrieving and storing a user's stored
 * list of weather locations.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherLocationListViewModel extends AndroidViewModel {

    /** The mutable live data JSONObject response */
    private final MutableLiveData<JSONObject> mResponse;

    /** The mutable live data JSONObject addition response */
    private final MutableLiveData<JSONObject> mAdditionResponse;

    /** The mutable live data JSONObject addition deletion */
    private final MutableLiveData<JSONObject> mDeleteResponse;

    /** The list of LatLong locations */
    private List<LatLong> mLocations;

    /** Indicates whether this view model's location list has been modified */
    private boolean mModificationFlag;

    /**
     * Constructs a new weather location list view model with the provided application
     *
     * @param theApplication the application to be assigned
     */
    public WeatherLocationListViewModel(@NonNull final Application theApplication) {
        super(theApplication);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
        mAdditionResponse = new MutableLiveData<>();
        mAdditionResponse.setValue(new JSONObject());
        mDeleteResponse = new MutableLiveData<>();
        mDeleteResponse.setValue(new JSONObject());
        mLocations = new ArrayList<>();
        mModificationFlag = false;
    }

    /**
     * Adds an observer to this view model's mutable JSONObject response
     *
     * @param theOwner the lifecycle owner
     * @param theObserver the observer to be assigned
     */
    public void addResponseObserver(@NonNull final LifecycleOwner theOwner,
                                    @NonNull final Observer<? super JSONObject> theObserver) {
        mResponse.observe(theOwner, theObserver);
    }

    /**
     * Adds an observer to this view model's mutable JSONObject location addition response
     *
     * @param theOwner the lifecycle owner
     * @param theObserver the observer to be assigned
     */
    public void addAdditionResponseObserver(
            @NonNull final LifecycleOwner theOwner,
            @NonNull final Observer<? super JSONObject> theObserver) {
        mAdditionResponse.observe(theOwner, theObserver);
    }

    /**
     * Adds an observer to this view model's mutable JSONObject location deletion response
     *
     * @param theOwner the lifecycle owner
     * @param theObserver the observer to be assigned
     */
    public void addDeletionResponseObserver(
            @NonNull final LifecycleOwner theOwner,
            @NonNull final Observer<? super JSONObject> theObserver) {
        mDeleteResponse.observe(theOwner, theObserver);
    }

    /**
     * Clears this view model's JSONObject response
     */
    public void clearResponse() {
        mResponse.setValue(new JSONObject());
    }

    /**
     * Clears this view model's JSONObject location addition response
     */
    public void clearAdditionResponse() {
        mAdditionResponse.setValue(new JSONObject());
    }

    /**
     * Clears this view model's JSONObject location deletion response
     */
    public void clearDeletionResponse() {
        mDeleteResponse.setValue(new JSONObject());
    }

    /**
     * Provides the list of latitude longitude locations stored in this view model
     *
     * @return the list of LatLongs
     */
    public List<LatLong> getLocations() {
        return new LinkedList<>(mLocations);
    }

    /**
     * Indicates whether this view model's list has modified
     *
     * @return true if added to, false otherwise
     */
    public boolean isListModified() {
        return mModificationFlag;
    }

    /**
     * Informs this view model that new modifications have been noted
     */
    public void checkModifications() {
        mModificationFlag = false;
    }

    /**
     * Initiates a request to the web service to retrieve saved weather locations
     *
     * @param theJwt the user's JWT
     */
    public void connectGet(final String theJwt) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/weather/locations";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleResult,
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
     * Initiates a request to the web service to retrieve saved weather locations
     *
     * @param theJwt the user's JWT
     */
    public void connectPost(final String theJwt, final String theLocation) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/weather/locations";
        Map<String, String> bodyMap = new HashMap<>();
        if (theLocation.contains(":")) { // a lat long
            String[] latLong = theLocation.split(":");
            bodyMap.put("lat", latLong[0]);
            bodyMap.put("long", latLong[1]);
        } else { // a zip code
            bodyMap.put("zip", theLocation);
        }
        JSONObject body = new JSONObject(bodyMap);
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    mAdditionResponse.setValue(response);
                    mModificationFlag = true;
                },
                this::handleAddError) {
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
     * Initiates a request to the web service to retrieve saved weather locations
     *
     * @param theJwt the user's JWT
     */
    public void connectDelete(final String theJwt, final String theLocation) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/weather/locations/"
                + theLocation;
        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                mDeleteResponse::setValue,
                this::handleDeleteError) {
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
     * Handles a result returned from the web service weather endpoint and parses needed data
     *
     * @param theResult the result to be parsed
     */
    private void handleResult(final JSONObject theResult) {
        System.out.println(theResult);
        List<LatLong> locations = new ArrayList<>();
        try {
            JSONArray locArray = theResult.getJSONArray("data");
            for (int i = 0; i < locArray.length(); i++) {
                JSONObject nextLoc = (JSONObject) locArray.get(i);
                double latitude = nextLoc.getDouble("lat");
                double longitude = nextLoc.getDouble("long");
                locations.add(new LatLong(latitude, longitude));
            }
            mLocations = locations;
            mResponse.setValue(theResult);
        } catch (JSONException ex) {
            Map<String, String> map = new HashMap<>();
            map.put("code", "JSON parse error");
            mResponse.setValue(new JSONObject(map));
            ex.printStackTrace();
        }
    }

    /**
     * Handles errors generated when requesting location list data from the server
     *
     * @param theError the resulting Volley error to be handled
     */
    private void handleError(final VolleyError theError) {
        Map<String, String> map = new HashMap<>();
        map.put("code", theError.getLocalizedMessage());
        mResponse.setValue(new JSONObject(map));
    }

    /**
     * Handles errors generated when deleting a saved weather location from the server
     *
     * @param theError the resulting Volley error to be handled
     */
    private void handleDeleteError(final VolleyError theError) {
        Map<String, String> map = new HashMap<>();
        map.put("code", theError.getLocalizedMessage());
        mDeleteResponse.setValue(new JSONObject(map));
    }

    /**
     * Handles errors generated when adding a saved weather location
     *
     * @param theError the resulting Volley error to be handled
     */
    private void handleAddError(final VolleyError theError) {
        Map<String, Object> map = new HashMap<>();
        if (theError.networkResponse != null) {
            map.put("code", String.valueOf(theError.networkResponse.statusCode));
            try {
                String data = new String(theError.networkResponse.data, Charset.defaultCharset())
                        .replace('\"', '\'');
                map.put("data", theError.networkResponse.data == null ? new JSONObject() :
                        new JSONObject(data));
            } catch (JSONException ex) {
                Log.e("JSON PARSE ERROR IN ERROR HANDLER", ex.getMessage());
            }
        }
        mAdditionResponse.setValue(new JSONObject(map));
    }
}
