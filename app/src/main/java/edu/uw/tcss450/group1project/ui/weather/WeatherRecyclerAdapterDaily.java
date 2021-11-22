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
import edu.uw.tcss450.group1project.databinding.FragmentWeather10dayCardBinding;
import edu.uw.tcss450.group1project.utils.WeatherUtils;

/**
 * WeatherRecyclerAdapter10Day provides an adapter for the WeatherFragment 10-day forecast
 * RecyclerView.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherRecyclerAdapterDaily
        extends RecyclerView.Adapter<WeatherRecyclerAdapterDaily.WeatherDayViewHolder> {

    /** The list of weather data to be displayed */
    private final List<WeatherData> mForecast;

    /**
     * Creates a new WeatherRecyclerAdapter10Day with a provided list of weather data
     *
     * @param theData the list of weather data
     */
    public WeatherRecyclerAdapterDaily(final List<WeatherData> theData) {
        mForecast = theData;
    }

    @NonNull
    @Override
    public WeatherDayViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                   final int theViewType) {
        return new WeatherDayViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_weather_10day_card, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final WeatherDayViewHolder theHolder,
                                 final int thePosition) {
        theHolder.setWeatherData(mForecast.get(thePosition));
    }

    @Override
    public int getItemCount() {
        return mForecast.size();
    }

    /**
     * WeatherDayViewHolder is a class defining an individual ViewHolder for the WeatherFragment
     * 10-day forecast RecyclerView.
     *
     * @author Parker Rosengreen
     * @version Fall 2021
     */
    public class WeatherDayViewHolder extends RecyclerView.ViewHolder {

        /** The assigned view */
        private final View mView;

        /** The ViewBinding corresponded to a daily forecast RecyclerView card */
        private final FragmentWeather10dayCardBinding mBinding;

        /** The weather data assigned to this ViewHolder */
        private WeatherData mData;

        /**
         * Creates a new WeatherDayViewHolder with the provided view
         *
         * @param theItemView the view to be assigned
         */
        public WeatherDayViewHolder(@NonNull View theItemView) {
            super(theItemView);
            mView = theItemView;
            mBinding = FragmentWeather10dayCardBinding.bind(theItemView);
        }

        /**
         * Assigns weather data to this view holder
         *
         * @param theData the weather data to be assigned
         */
        public void setWeatherData(final WeatherData theData) {
            mData = theData;
            display();
        }

        /** Displays all weather data and image views for a single 10-day forecast card */
        private void display() {
            mBinding.titleWeekday.setText(mData.getTimeDescriptor());
            mBinding.imageWeatherCondition.setImageResource(
                    WeatherUtils.getInstance().getIconResource(mData.getWeatherCondition())
            );
            mBinding.titleTemperature.setText(mData.getTemperature() + "\u2109");
        }
    }
}

