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

    /** The current home weather data */
    private WeatherDataCurrent mCurrentDataHome;

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
    public void addResponseObserver(@NonNull final LifecycleOwner theOwner,
                                    @NonNull final Observer<? super JSONObject> theObserver) {
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

    public WeatherDataCurrent getCurrentDataHome() {
        return mCurrentDataHome;
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
     * Determines if this view model's data home fields are readable (i.e. non-null)
     *
     * @return true if readable, false otherwise
     */
    public boolean containsReadableHomeData() {
        return mCurrentDataHome != null;
    }

    /**
     * Determines if this view model's data fields are readable (i.e. non-null)
     *
     * @return true if readable, false otherwise
     */
    public boolean containsReadableData() {
        return mCurrentData != null && mHourlyData != null && mDailyData != null;
    }

    /**
     * Clears the JSONObject response of this view model
     */
    public void clearResponse() {
        mResponse.setValue(new JSONObject());
    }

    /**
     * Creates a weather endpoint get request to receive live weather data
     *
     * @param theJwt the user's JWT
     */
    public void connectGet(final String theJwt, final boolean theCalledFromHome) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/weather/98423";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                result -> handleResult(result, theCalledFromHome),
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
    private void handleResult(final JSONObject theResult, final boolean theCalledFromHome) {
        WeatherDataCurrent currentData = null;
        List<WeatherData> hourlyData = new ArrayList<>();
        List<WeatherData> dailyData = new ArrayList<>();
        try {
            JSONObject curr = theResult.getJSONObject("currentData");
            currentData = new WeatherDataCurrent(
                    "Tacoma",
                    curr.getInt("curTemp"),
                    curr.getInt("curFeels_like"),
                    (int) Math.round(curr.getDouble("curRain") * 100),
                    curr.getInt("curHumidity"),
                    curr.getString("ccurIcon"));
            JSONArray hourArray = theResult.getJSONArray("hourData");
            for (int i = 0; i < hourArray.length(); i++) {
                JSONObject hourData = (JSONObject) hourArray.get(i);
                int hour = hourData.getInt("hHours");
                WeatherData newData = new WeatherData(
                        i == 0 ? "Now" :
                                (hour % 12 == 0 ? 12 : hour % 12) + (hour < 12 ? "AM" : "PM"),
                        hourData.getInt("hTemp"),
                        hourData.getString("hIcon"));
                hourlyData.add(newData);
            }
            JSONArray dailyArray = theResult.getJSONArray("dailyData");
            for (int i = 0; i < dailyArray.length(); i++) {
                JSONObject dayData = (JSONObject) dailyArray.get(i);
                WeatherData newData = new WeatherData(
                        i == 0 ? "Today" : dayData.getString("dDay"),
                        dayData.getInt("dTemp"),
                        dayData.getString("dIcon"));
                dailyData.add(newData);
            }
            if (theCalledFromHome) {
                mCurrentDataHome = currentData;
            } else {
                mCurrentData = currentData;
                mHourlyData = hourlyData;
                mDailyData = dailyData;
            }
            mResponse.setValue(theResult);
        } catch (JSONException ex) {
            Map<String, String> map = new HashMap<>();
            map.put("code", "JSON parse error");
            mResponse.setValue(new JSONObject(map));
            ex.printStackTrace();
        }
    }

    /**
     * Handles errors generated when requesting weather data from the server
     *
     * @param theError the resulting Volley error to be handled
     */
    private void handleError(final VolleyError theError) {
        Log.e("WEATHER REQUEST ERROR", theError.getLocalizedMessage());
        Map<String, String> map = new HashMap<>();
        map.put("code", "server error");
        mResponse.setValue(new JSONObject(map));
    }
}
