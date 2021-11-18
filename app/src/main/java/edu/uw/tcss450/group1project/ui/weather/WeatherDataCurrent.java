package edu.uw.tcss450.group1project.ui.weather;

public class WeatherDataCurrent {

    private final String mCity;

    private final int mTemperature;

    private final int mFeelsLike;

    private final int mChanceRain;

    private final int mHumidity;

    private final String mWeatherCondition;

    public WeatherDataCurrent(final String theCity, final int theTemp, final int theFeelsLike,
                              final int theRainChance, final int theHumidity,
                              final String theCond) {
        mCity = theCity;
        mTemperature = theTemp;
        mFeelsLike = theFeelsLike;
        mChanceRain = theRainChance;
        mHumidity = theHumidity;
        mWeatherCondition = theCond;
    }

    public String getCity() {
        return mCity;
    }

    public int getTemperature() {
        return mTemperature;
    }

    public int getFeelsLike() {
        return mFeelsLike;
    }

    public int getChanceRain() {
        return mChanceRain;
    }

    public int getHumidity() {
        return mHumidity;
    }

    public String getWeatherCondition() {
        return mWeatherCondition;
    }
}
