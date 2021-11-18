/*
 * TCSS450 Mobile Applications
 * Fall 2021
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

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentWeatherBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.model.WeatherDataViewModel;

/**
 * A {@link Fragment} subclass that is responsible for the weather page.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class WeatherFragment extends Fragment {

    private WeatherDataViewModel mModel;

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public WeatherFragment() {
        // required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(WeatherDataViewModel.class);
        UserInfoViewModel userInfo = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
        mModel.connectGet(userInfo.getmJwt());
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


        mModel.addCurrentDataObserver(getViewLifecycleOwner(), currData -> {
            binding.titleCity.setText(currData.getCity());
            binding.titleWeatherIcon.setImageResource(
                    WeatherUtils.getInstance().getIconResource(currData.getWeatherCondition()));
            binding.titleTemperature.setText(String.valueOf(currData.getTemperature()) + "\u2109");
            binding.titleFeelsLike.setText("Feels like: " + currData.getFeelsLike() + "\u2109");
            binding.titleChanceRain.setText("Chance of rain: " + currData.getChanceRain() + "%");
            binding.titleHumidity.setText("Humidity: " + currData.getHumidity() + "%");
        });

        mModel.addHourlyDataObserver(getViewLifecycleOwner(), hourlyData -> {
            binding.listHourlyForecast.setAdapter(new WeatherRecyclerAdapterHourly(hourlyData));
        });

        mModel.addDailyDataObserver(getViewLifecycleOwner(), dailyData -> {
            binding.listDailyForecast.setAdapter(new WeatherRecyclerAdapterDaily(dailyData));
        });
    }
}
