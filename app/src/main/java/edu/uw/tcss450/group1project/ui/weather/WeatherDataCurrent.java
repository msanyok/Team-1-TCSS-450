/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

/**
 * WeatherDataCurrent is a class for storing and retrieving current weather data.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherDataCurrent {

    /** The city name */
    private final String mCity;

    /** The temperature */
    private final int mTemperature;

    /** The feels like temperature */
    private final int mFeelsLike;

    /** The chance of precipitation percentage */
    private final int mChancePrecipitation;

    /** The humidity percentage */
    private final int mHumidity;

    /** The weather condition code */
    private final String mWeatherCondition;

    /**
     * Constructs a new WeatherDataCurrent with the provided statistics
     * @param theCity the city name
     * @param theTemp the temperature
     * @param theFeelsLike the feels like temperature
     * @param thePrecipChance the chance of precipitation
     * @param theHumidity the humidity percentage
     * @param theCond the weather condition code
     */
    public WeatherDataCurrent(final String theCity, final int theTemp, final int theFeelsLike,
                              final int thePrecipChance, final int theHumidity,
                              final String theCond) {
        mCity = theCity;
        mTemperature = theTemp;
        mFeelsLike = theFeelsLike;
        mChancePrecipitation = thePrecipChance;
        mHumidity = theHumidity;
        mWeatherCondition = theCond;
    }

    /**
     * Provides the city name
     *
     * @return the city name
     */
    public String getCity() {
        return mCity;
    }

    /**
     * Provides the temperature
     *
     * @return the temperature
     */
    public int getTemperature() {
        return mTemperature;
    }

    /**
     * Provides the feels like temperature
     *
     * @return the feels like temperature
     */
    public int getFeelsLike() {
        return mFeelsLike;
    }

    /**
     * Provides the precipitation percentage
     *
     * @return the precipitation percentage
     */
    public int getPrecipPercentage() {
        return mChancePrecipitation;
    }

    /**
     * Provides the humidity percentage
     *
     * @return the humidity percentage
     */
    public int getHumidity() {
        return mHumidity;
    }

    /**
     * Provides the weather condition code
     *
     * @return the weather condition code
     */
    public String getWeatherCondition() {
        return mWeatherCondition;
    }
}
