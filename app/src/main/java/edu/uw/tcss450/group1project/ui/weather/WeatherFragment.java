/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

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

    /** The weather data view model */
    private WeatherDataViewModel mModel;

    private FragmentWeatherBinding mBinding;

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public WeatherFragment() {
        // required empty constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(WeatherDataViewModel.class);
        UserInfoViewModel userInfo = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
        mModel.connectGet(userInfo.getJwt(), false);
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
        mBinding = FragmentWeatherBinding.bind(getView());
        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        mModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);
    }

    /**
     * Observes changes in the JSONObject response within the WeatherDataViewModel
     *
     * @param theResponse the changed JSONObject
     */
    private void observeResponse(final JSONObject theResponse) {
        System.out.println(theResponse);
        if (theResponse.has("error")) {
            try {
                displayErrorDialog(theResponse.get("error").toString());
            } catch (JSONException ex) {
                Log.e("ERROR", "Could not parse error JSON");
            }
        }
        if (mModel.containsReadableData()) {
            setViewComponents();
        }
    }

    /**
     * Sets up this fragment's view components with data from the WeatherDataViewModel
     */
    private void setViewComponents() {
        mBinding.titleCity.setText(mModel.getCurrentData().getCity());
        mBinding.titleWeatherIcon.setImageResource(
                WeatherUtils.getInstance()
                        .getIconResource(mModel.getCurrentData().getWeatherCondition()));
        mBinding.titleTemperature
                .setText(String.valueOf(mModel.getCurrentData().getTemperature()) + "\u2109");
        mBinding.titleFeelsLike
                .setText("Feels like: " + mModel.getCurrentData().getFeelsLike() + "\u2109");
        mBinding.titleChanceRain
                .setText("Precipitation: " +
                        mModel.getCurrentData().getPrecipPercentage() + "%");
        mBinding.titleHumidity
                .setText("Humidity: " + mModel.getCurrentData().getHumidity() + "%");

        mBinding.listHourlyForecast
                .setAdapter(new WeatherRecyclerAdapterHourly(mModel.getHourlyData()));
        mBinding.listDailyForecast
                .setAdapter(new WeatherRecyclerAdapterDaily(mModel.getDailyData()));
    }

    private void displayErrorDialog(final String theError) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>Unexpected " +
                        theError + " when loading weather." + " Please try again.</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Ok</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }
}
