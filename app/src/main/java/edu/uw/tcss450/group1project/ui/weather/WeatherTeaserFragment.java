/*
 * TCSS450 Mobile Applications
 */

package edu.uw.tcss450.group1project.ui.weather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentWeatherBinding;
import edu.uw.tcss450.group1project.utils.WeatherUtils;

/**
 * WeatherTeaserFragment is a fragment class for displaying weather data for a newly searched
 * location.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherTeaserFragment extends Fragment {

    /**
     * Required empty constructor
     */
    public WeatherTeaserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_weather, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        NavController navController = Navigation.findNavController(theView);
        NavBackStackEntry backStackEntry =
                navController.getBackStackEntry(R.id.navigation_weather_parent);
        WeatherDataViewModel model =
                new ViewModelProvider(backStackEntry).get(WeatherDataViewModel.class);
        FragmentWeatherBinding binding = FragmentWeatherBinding.bind(theView);
        binding.locationDeleteButton.setVisibility(View.INVISIBLE);
        binding.titleCity.setText(model.getCurrentData().getCity());
        binding.titleWeatherIcon.setImageResource(
                WeatherUtils.getInstance()
                        .getIconResource(model.getCurrentData().getWeatherCondition()));
        binding.titleTemperature
                .setText(String.valueOf(model.getCurrentData().getTemperature()) + "\u2109");
        binding.titleFeelsLike
                .setText("Feels like: " + model.getCurrentData().getFeelsLike() + "\u2109");
        binding.titleChanceRain
                .setText("Precipitation: " +
                        model.getCurrentData().getPrecipPercentage() + "%");
        binding.titleHumidity
                .setText("Humidity: " + model.getCurrentData().getHumidity() + "%");
        binding.listHourlyForecast
                .setAdapter(new WeatherRecyclerAdapterHourly(model.getHourlyData()));
        binding.listDailyForecast
                .setAdapter(new WeatherRecyclerAdapterDaily(model.getDailyData()));
    }
}