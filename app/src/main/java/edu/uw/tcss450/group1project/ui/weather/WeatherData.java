/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

/**
 * WeatherData is a class for storing and retrieving forecast information for a specific
 * day or hour.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherData {

    /** Indicates the hour of the day or day of the week for this weather data */
    private final String myTimeDescriptor;

    /** The temperature */
    private final int myTemperature;

    /** An integer code mapped to the weather condition */
    private final String myWeatherCondition;

    /**
     * Creates a new WeatherData with the provided descriptor, temperature, and condition code
     *
     * @param theDescriptor the condition code
     * @param theTemp the temperature
     * @param theCond the condition code
     */
    public WeatherData(final String theDescriptor, final int theTemp, final String theCond) {
        myTemperature = theTemp;
        myWeatherCondition = theCond;
        myTimeDescriptor = theDescriptor;
    }

    /**
     * Provides this weather data's time descriptor string
     *
     * @return the descriptor string
     */
    public String getTimeDescriptor() {
        return myTimeDescriptor;
    }

    /**
     * Provides this weather data's condition code
     *
     * @return the weather condition code
     */
    public String getWeatherCondition() {
        return myWeatherCondition;
    }

    /**
     * Provides this weather data's temperature
     *
     * @return the temperature
     */
    public int getTemperature() {
        return myTemperature;
    }
}
