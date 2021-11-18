/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.app.Application;
import android.util.Log;
import android.widget.ArrayAdapter;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.group1project.ui.weather.WeatherData;
import edu.uw.tcss450.group1project.ui.weather.WeatherDataCurrent;

/**
 * WeatherDataViewModel is a view model class storing live current, hourly, and daily weather
 * data.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherDataViewModel extends AndroidViewModel {

    /** The mutable live data JSONObject response */
    private final MutableLiveData<JSONObject> mResponse;

    /** The current weather data */
    private WeatherDataCurrent mCurrentData;

    /** The list of hourly weather data */
    private List<WeatherData> mHourlyData;

    /** The list of daily weather data */
    private List<WeatherData> mDailyData;

    /**
     * Creates a new WeatherDataViewModel
     *
     * @param theApplication the application to be assigned
     */
    public WeatherDataViewModel(@NonNull Application theApplication) {
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
    public void addResponseObserver(@NonNull LifecycleOwner theOwner,
                                    @NonNull Observer<? super JSONObject> theObserver) {
        mResponse.observe(theOwner, theObserver);
    }

    /**
     * Supplies the current weather data of this view model
     *
     * @return the current weather data
     */
    public WeatherDataCurrent getCurrentData() {
        return mCurrentData;
    }

    /**
     * Supplies the hourly weather data of this view model
     *
     * @return the hourly weather data
     */
    public List<WeatherData> getHourlyData() {
        return mHourlyData;
    }

    /**
     * Supplies the daily weather data of this view model
     *
     * @return the daily weather data
     */
    public List<WeatherData> getDailyData() {
        return mDailyData;
    }

    /**
     * Determines if this view model's data components are readable (i.e. non-null)
     *
     * @return true if readable, false otherwise
     */
    public boolean containsReadableContents() {
        return mCurrentData != null && mHourlyData != null && mDailyData != null;
    }

    /**
     * Creates a weather endpoint get request to receive live weather data
     *
     * @param theJwt the user's JWT
     */
    public void connectGet(final String theJwt) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/weather/98423";
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
        WeatherDataCurrent currentData = null;
        List<WeatherData> hourlyData = new ArrayList<>();
        List<WeatherData> dailyData = new ArrayList<>();
        try {
            JSONObject curr = theResult.getJSONObject("currentData");
            currentData = new WeatherDataCurrent(
                    "Tacoma",
                    (int) curr.get("curTemp"),
                    (int) curr.get("curFeels_like"),
                    (int) curr.get("curRain"),
                    (int) curr.get("curHumidity"),
                    curr.get("ccurIcon").toString());
            JSONArray hourArray = theResult.getJSONArray("hourData");
            for (int i = 0; i < hourArray.length(); i++) {
                JSONObject hourData = (JSONObject) hourArray.get(i);
                int hour = (int) hourData.get("hHours");
                WeatherData newData = new WeatherData(
                        i == 0 ? "Now" :
                                (hour % 12 == 0 ? 12 : hour % 12) + (hour < 12 ? "AM" : "PM"),
                        (int) hourData.get("hTemp"),
                        hourData.get("hIcon").toString());
                hourlyData.add(newData);
            }
            JSONArray dailyArray = theResult.getJSONArray("dailyData");
            for (int i = 0; i < dailyArray.length(); i++) {
                JSONObject dayData = (JSONObject) dailyArray.get(i);
                WeatherData newData = new WeatherData(
                        i == 0 ? "Today" : dayData.get("dDay").toString(),
                        (int) dayData.get("dTemp"),
                        dayData.get("dIcon").toString());
                dailyData.add(newData);
            }
            mCurrentData = currentData;
            mHourlyData = hourlyData;
            mDailyData = dailyData;
            mResponse.setValue(theResult);
        } catch (JSONException ex) {
            mResponse.setValue(new JSONObject()); // add error field here
            ex.printStackTrace();
        }
    }

    /**
     * Handles errors generated when requesting weather data from the server
     *
     * @param theError the resulting Volley error to be handled
     */
    private void handleError(final VolleyError theError) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PROJECT
        Log.e("CONNECTION ERROR", theError.getLocalizedMessage());
        throw new IllegalStateException(theError.getMessage());
    }
}
