package edu.uw.tcss450.group1project.ui.weather;

import java.util.HashMap;
import java.util.Map;

import edu.uw.tcss450.group1project.R;

public class WeatherUtils {

    private static WeatherUtils mInstance;

    private static Map<String, Integer> mResourceMap;

    private WeatherUtils() {
        mResourceMap = getIconMapping();
    }

    public static synchronized WeatherUtils getInstance() {
        if (mInstance == null) {
            mInstance = new WeatherUtils();
        }
        return mInstance;
    }

    public int getIconResource(final String theIconCode) {
        return mResourceMap.get(theIconCode);
    }

    private static Map<String, Integer> getIconMapping() {
        Map<String, Integer> mapping = new HashMap<>();
        mapping.put("01d", R.drawable.clear_day);
        mapping.put("01n", R.drawable.clear_night);
        mapping.put("02d", R.drawable.partly_cloudy_day);
        mapping.put("02n", R.drawable.partly_cloudy_night);
        mapping.put("03d", R.drawable.cloudy);
        mapping.put("03n", R.drawable.cloudy);
        mapping.put("04d", R.drawable.broken_clouds);
        mapping.put("04n", R.drawable.broken_clouds);
        mapping.put("09d", R.drawable.rainy);
        mapping.put("09n", R.drawable.rainy);
        mapping.put("10d", R.drawable.rain_sun_day);
        mapping.put("10n", R.drawable.rain_moon_night);
        mapping.put("11d", R.drawable.thunder);
        mapping.put("11n", R.drawable.thunder);
        mapping.put("13d", R.drawable.snow);
        mapping.put("13n", R.drawable.snow);
        mapping.put("50d", R.drawable.mist);
        mapping.put("50n", R.drawable.mist);
        return mapping;
    }
}
