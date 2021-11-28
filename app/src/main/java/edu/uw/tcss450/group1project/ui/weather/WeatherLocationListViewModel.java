/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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

    /** The list of LatLong locations */
    private List<LatLong> mLocations;

    /**
     * Constructs a new weather location list view model with the provided application
     *
     * @param theApplication the application to be assigned
     */
    public WeatherLocationListViewModel(@NonNull final Application theApplication) {
        super(theApplication);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
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
     * Determines if this view model's data fields are readable (i.e. non-null)
     *
     * @return true if readable, false otherwise
     */
    public boolean containsReadableData() {
        return mLocations != null;
    }

    /**
     * Clears this view model's JSONObject response
     */
    public void clearResponse() {
        mResponse.setValue(new JSONObject());
    }

    /**
     * Provides the list of latitude longitude locations stored in this view model
     *
     * @return the list of LatLongs
     */
    public List<LatLong> getLocations() {
        return mLocations;
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
     * Handles a result returned from the web service weather endpoint and parses needed data
     *
     * @param theResult the result to be parsed
     */
    private void handleResult(final JSONObject theResult) {
        List<LatLong> locations = new ArrayList<>();
        System.out.println(theResult);
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
}
