/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONObject;

import edu.uw.tcss450.group1project.MainActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentHomeBinding;
import edu.uw.tcss450.group1project.ui.contacts.ContactRequestViewModel;
import edu.uw.tcss450.group1project.model.LocationViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.model.WeatherDataViewModel;
import edu.uw.tcss450.group1project.ui.weather.WeatherDataCurrent;
import edu.uw.tcss450.group1project.utils.WeatherUtils;

/**
 * A {@link Fragment} subclass that is responsible for the home page.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @author Steven Omegna
 * @version Fall 2021
 */
public class HomeFragment extends Fragment {

    /** Weather View Model */
    private WeatherDataViewModel mWeatherModel;

    /** Contact Requests view Model*/
    private ContactRequestViewModel mRequestModel;

    /** User View Model for Jwt*/
    private UserInfoViewModel mUserModel;

    /** The view binding */
    private FragmentHomeBinding mBinding;

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mWeatherModel = new ViewModelProvider(getActivity()).get(WeatherDataViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
//        LocationViewModel locModel =
//                new ViewModelProvider(getActivity()).get(LocationViewModel.class);
//        locModel.addLocationObserver(getViewLifecycleOwner(), (location) -> {
//            if (location != null) {
//                mWeatherModel.connectGet(
//                        mUserModel.getJwt(), location.getLatitude(), location.getLongitude());
//            }
//        });
//        Location currLoc = locModel.getCurrentLocation();
//        mWeatherModel.connectGet(
//                mUserModel.getJwt(), currLoc.getLatitude(), currLoc.getLongitude());
    }


    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_home, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        LocationViewModel locModel =
                new ViewModelProvider(getActivity()).get(LocationViewModel.class);
        locModel.addLocationObserver(getViewLifecycleOwner(), (location) -> {
                mWeatherModel.connectGet(
                        mUserModel.getJwt(), location.getLatitude(), location.getLongitude(), true);
        });

        mWeatherModel.addResponseObserver(getViewLifecycleOwner(), this::observeWeatherResponse);

        mBinding = FragmentHomeBinding.bind(getView());
        mBinding.welcomeText.setText(String.format("Welcome, %s!", mUserModel.getNickname()));
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, set the weather view component.
     *
     * @param theResponse from the server
     */
    private void observeWeatherResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            Log.e("WEATHER REQUEST ERROR", theResponse.toString());
            displayWeatherErrorDialog();
            mWeatherModel.clearResponse();
        }
        if (mWeatherModel.containsReadableData()) {
            setWeatherViewComponents();
        }
    }

    /**
     * Binds the weather data to the homepage
     */
    private void setWeatherViewComponents() {
        WeatherDataCurrent weatherData = mWeatherModel.getCurrentData();
        mBinding.weatherImage.setImageResource(
                WeatherUtils.getInstance().getIconResource(weatherData.getWeatherCondition()));
        mBinding.weatherText.setText(String
                .format("It is %d degrees with a %d percent chance of rain.",
                        weatherData.getTemperature(), weatherData.getPrecipPercentage()));
    }

    /**
     * Displays error dialog box when error occurs with weather
     */
    private void displayWeatherErrorDialog() {
        String message = "Unexpected error when loading local weather. Please try again.";
        ((MainActivity) getActivity()).displayErrorDialog(message);
    }
}
