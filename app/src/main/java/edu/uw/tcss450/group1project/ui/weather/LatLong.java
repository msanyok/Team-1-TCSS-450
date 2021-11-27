/*
 * TCSS450 Mobile Applications
 */

package edu.uw.tcss450.group1project.ui.weather;

/**
 * LatLong is a simple class for storing latitudes and longitudes.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class LatLong {

    /** The latitude */
    private final double mLat;

    /** The longitude */
    private final double mLong;

    /**
     * Constructs a new LatLong with the provided latitude and longitude
     *
     * @param theLat the latitude
     * @param theLong the longitude
     */
    public LatLong(final double theLat, final double theLong) {
        mLat = theLat;
        mLong = theLong;
    }

    /**
     * Provides the latitude
     *
     * @return the latitude
     */
    public double getLat() {
        return mLat;
    }

    /**
     * Provides the longitude
     *
     * @return the longitude
     */
    public double getLong() {
        return mLong;
    }
}
