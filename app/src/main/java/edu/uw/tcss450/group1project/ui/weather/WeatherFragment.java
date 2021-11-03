/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentWeatherBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * A {@link Fragment} subclass that is responsible for the weather page.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class WeatherFragment extends Fragment {

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public WeatherFragment() {
        // required empty constructor
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
        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        FragmentWeatherBinding binding = FragmentWeatherBinding.bind(getView());
        binding.titleWeatherIcon.setImageResource(R.drawable.ic_sun_yellow_24dp);
        binding.titleTemperature.setText(String.valueOf(50) + "\u2109");
        binding.titleFeelsLike.setText(binding.titleFeelsLike.getText() + " "
                + String.valueOf(45) + "\u2109");
        binding.titleChanceRain.setText(binding.titleChanceRain.getText() + " 10%");
        binding.titleHumidity.setText(binding.titleHumidity.getText() + " 80%");
        binding.list24hrForecast.setAdapter(
                new WeatherRecyclerAdapter24Hour(WeatherDataGenerator.get24HrForecast()));
        binding.list10dayForecast.setAdapter(
                new WeatherRecyclerAdapter10Day(WeatherDataGenerator.get10DayForecast()));
    }
}
