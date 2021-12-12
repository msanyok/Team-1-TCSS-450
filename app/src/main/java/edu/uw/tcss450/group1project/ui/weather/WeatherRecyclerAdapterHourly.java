/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentWeather24hrCardBinding;
import edu.uw.tcss450.group1project.utils.WeatherUtils;

/**
 * WeatherRecyclerAdapterHourly provides an adapter for the WeatherFragment
 * 24-hour forecast RecyclerView.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherRecyclerAdapterHourly
        extends RecyclerView.Adapter<WeatherRecyclerAdapterHourly.WeatherHourViewHolder> {

    /** The list of weather data to be displayed */
    private final List<WeatherData> mForecast;

    /**
     * Creates a new WeatherRecyclerAdapterHourly with a provided list of weather data
     *
     * @param theData the list of weather data
     */
    public WeatherRecyclerAdapterHourly(final List<WeatherData> theData) {
        mForecast = theData;
    }

    @NonNull
    @Override
    public WeatherHourViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                    final int theViewType) {
        return new WeatherHourViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_weather_24hr_card, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final WeatherHourViewHolder theHolder,
                                 final int thePosition) {
        theHolder.setWeatherData(mForecast.get(thePosition));
    }

    @Override
    public int getItemCount() {
        return mForecast.size();
    }

    /**
     * WeatherHourViewHolder is a class defining an individual ViewHolder for the WeatherFragment
     * 24-hour forecast RecyclerView.
     *
     * @author Parker Rosengreen
     * @version Fall 2021
     */
    public class WeatherHourViewHolder extends RecyclerView.ViewHolder {

        /** The ViewBinding corresponded to an hourly weather RecyclerView card */
        private final FragmentWeather24hrCardBinding mBinding;

        /** The weather data assigned to this ViewHolder */
        private WeatherData mData;

        /**
         * Creates a new WeatherHourViewHolder with the provided view
         *
         * @param theItemView the view to be assigned
         */
        public WeatherHourViewHolder(@NonNull final View theItemView) {
            super(theItemView);
            mBinding = FragmentWeather24hrCardBinding.bind(theItemView);
        }

        /**
         * Assigns weather data to this view holder
         *
         * @param theData the contact to be assigned
         */
        public void setWeatherData(final WeatherData theData) {
            mData = theData;
            display();
        }

        /** Displays all weather data and image views for a single hourly weather card */
        private void display() {
            mBinding.titleHour.setText(mData.getTimeDescriptor());
            mBinding.imageWeatherCondition.setImageResource(
                    WeatherUtils.getInstance().getIconResource(mData.getWeatherCondition())
            );
            mBinding.titleTemperature.setText(mData.getTemperature() + "\u2109");
        }
    }
}