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
    public WeatherDataViewModel(@NonNull final Application theApplication) {
        super(theApplication);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
        mHourlyData = new ArrayList<>();
        mDailyData = new ArrayList<>();
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

    /**
     * Supplies the hourly weather data of this view model
     *
     * @return the hourly weather data
     */
    public List<WeatherData> getHourlyData() {
        return new LinkedList<>(mHourlyData);
    }

    /**
     * Supplies the daily weather data of this view model
     *
     * @return the daily weather data
     */
    public List<WeatherData> getDailyData() {
        return new LinkedList<>(mDailyData);
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
    public void connectGet(final String theJwt, final String theLocation,
                           final boolean theDeletable) {
        String url = "https://team-1-tcss-450-server.herokuapp.com/weather/" + theLocation;
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                result -> handleResult(result, theDeletable),
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
    private void handleResult(final JSONObject theResult, final boolean theDeletable) {
        WeatherDataCurrent currentData = null;
        List<WeatherData> hourlyData = new ArrayList<>();
        List<WeatherData> dailyData = new ArrayList<>();
        try {
            JSONObject curr = theResult.getJSONObject("currentData");
            currentData = new WeatherDataCurrent(
                    !theDeletable ?
                            "My Location" + String.format(" (%s)",
                                    theResult.getString("location").split(",")[0]):
                            theResult.getString("location"),
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
            mCurrentData = currentData;
            mHourlyData = hourlyData;
            mDailyData = dailyData;
            mResponse.setValue(theResult);
        } catch (JSONException ex) {
            Map<String, String> map = new HashMap<>();
            map.put("code", "JSON parse error: " + ex.getMessage());
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
        mResponse.setValue(new JSONObject(map));
    }
}