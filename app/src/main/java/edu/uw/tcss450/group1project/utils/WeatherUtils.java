/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.utils;

import java.util.HashMap;
import java.util.Map;

import edu.uw.tcss450.group1project.R;

/**
 * WeatherUtils is a class storing weather icon mappings based on weather condition codes.
 * WeatherUtils is a Singleton class.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherUtils {

    /** The singleton instance */
    private static WeatherUtils mInstance;

    /** The weather icon resource mapping */
    private static Map<String, Integer> mResourceMap;

    /**
     * Creates a new WeatherUtils and assigns a weather icon mapping
     */
    private WeatherUtils() {
        mResourceMap = getIconMapping();
    }

    /**
     * Provides the single WeatherUtils instance
     *
     * @return the WeatherUtils instance
     */
    public static synchronized WeatherUtils getInstance() {
        if (mInstance == null) {
            mInstance = new WeatherUtils();
        }
        return mInstance;
    }

    /**
     * Provides the icon resource for a provided weather icon code.
     *
     * @param theIconCode the weather icon code
     * @return the weather icon resource
     */
    public int getIconResource(final String theIconCode) {
        return mResourceMap.get(theIconCode);
    }

    /**
     * Returns a mapping from weather icon String code to black integer weather icon resources
     *
     * @return the code to icon mapping
     */
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