package edu.uw.tcss450.group1project.model;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.group1project.ui.weather.WeatherData;
import edu.uw.tcss450.group1project.ui.weather.WeatherDataCurrent;

public class WeatherDataViewModel extends AndroidViewModel {

    private final MutableLiveData<WeatherDataCurrent> mCurrentData;

    private final MutableLiveData<List<WeatherData>> mHourlyData;

    private final MutableLiveData<List<WeatherData>> mDailyData;

    public WeatherDataViewModel(@NonNull Application theApplication) {
        super(theApplication);
        mCurrentData = new MutableLiveData<>();
        mHourlyData = new MutableLiveData<>();
        mDailyData = new MutableLiveData<>();
    }

    public void addCurrentDataObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super WeatherDataCurrent> observer) {
        mCurrentData.observe(owner, observer);
    }

    public void addHourlyDataObserver(@NonNull LifecycleOwner owner,
                                      @NonNull Observer<? super List<WeatherData>> observer) {
        mHourlyData.observe(owner, observer);
    }

    public void addDailyDataObserver(@NonNull LifecycleOwner owner,
                                     @NonNull Observer<? super List<WeatherData>> observer) {
        mDailyData.observe(owner, observer);
    }

    public void setCurrentData(final WeatherDataCurrent theCurrentData) {
        mCurrentData.setValue(theCurrentData);
    }

    public void setHourlyData(final List<WeatherData> theHourlyData) {
        mHourlyData.setValue(theHourlyData);
    }

    public void setDailyData(final List<WeatherData> theDailyData) {
        mDailyData.setValue(theDailyData);
    }

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
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        mCurrentData.setValue(currentData);
        mHourlyData.setValue(hourlyData);
        mDailyData.setValue(dailyData);
    }

    private void handleError(final VolleyError theError) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PROJECT
        Log.e("CONNECTION ERROR", theError.getLocalizedMessage());
        throw new IllegalStateException(theError.getMessage());
    }
}
