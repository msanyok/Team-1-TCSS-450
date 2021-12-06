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
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONObject;

import edu.uw.tcss450.group1project.MainActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentWeatherBinding;
import edu.uw.tcss450.group1project.model.LocationViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.utils.WeatherUtils;

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

    /** The location list view model */
    private WeatherLocationListViewModel mLocationsModel;

    /** The user info view model */
    private UserInfoViewModel mUserModel;

    /** The view binding */
    private FragmentWeatherBinding mBinding;

    /** The latitude and longitude object */
    private LatLong mLatLong;

    /** Indicates whether or not this fragment is deletable from saved weather locations */
    private boolean mDeletable;

    /**
     * Required empty constructor
     */
    public WeatherFragment() {
        // required empty constructor
    }

    /**
     * Creates and returns a new WeatherFragment assigned with the provided data
     *
     * @param theLatLong the lat long to be assigned
     * @param theDeletable indicates whether this fragment is deletable from saved locations
     * @return the weather fragment instance
     */
    public static WeatherFragment newInstance(final LatLong theLatLong,
                                              final boolean theDeletable) {
        WeatherFragment newFrag = new WeatherFragment();
        newFrag.mLatLong = theLatLong;
        newFrag.mDeletable = theDeletable;
        return newFrag;
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle theBundle) {
        theBundle.putSerializable("mLatLong", mLatLong);
        theBundle.putBoolean("mDeletable", mDeletable);
        super.onSaveInstanceState(theBundle);
    }

    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        if (theSavedInstanceState != null) {
            mLatLong = (LatLong) theSavedInstanceState.getSerializable("mLatLong");
            mDeletable = theSavedInstanceState.getBoolean("mDeletable");
        }
        mModel = !mDeletable ?
                new ViewModelProvider(getActivity()).get(WeatherDataViewModel.class) :
                new ViewModelProvider(this).get(WeatherDataViewModel.class);
        mUserModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
        mModel.connectGet(mUserModel.getJwt(), mLatLong.toString(), mDeletable);
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

        Fragment parentFrag = getParentFragment();
        NavController navController = Navigation.findNavController(parentFrag.getView());
        NavBackStackEntry backStackEntry =
                navController.getBackStackEntry(R.id.navigation_weather_parent);
        mLocationsModel =
                new ViewModelProvider(backStackEntry).get(WeatherLocationListViewModel.class);
        mBinding = FragmentWeatherBinding.bind(getView());
        mModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);
        if (!mDeletable) {
            mBinding.locationDeleteButton.setVisibility(View.GONE);
            LocationViewModel locModel =
                    new ViewModelProvider(getActivity()).get(LocationViewModel.class);
            locModel.addLocationObserver(getViewLifecycleOwner(), location -> {
                if (location != null) {
                    mLatLong = new LatLong(location.getLatitude(), location.getLongitude());
                    mModel.connectGet(mUserModel.getJwt(), mLatLong.toString(), mDeletable);
                }
            });
        } else {
            mBinding.locationDeleteButton.setOnClickListener(this::displayLocationDeleteDialog);
        }
    }

    private void displayLocationDeleteDialog(final View theButton) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>Are you sure " +
                "you want to delete this location?</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Delete</font>"),
                (dialog, which) -> {
                    mLocationsModel.connectDelete(mUserModel.getJwt(), mLatLong.toString());
                });
        alertDialog.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }

    /**
     * Observes changes in the JSONObject response within the WeatherDataViewModel
     *
     * @param theResponse the changed JSONObject
     */
    private void observeResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            Log.e("WEATHER REQUEST ERROR", theResponse.toString());
            displayErrorDialog();
            mModel.clearResponse();
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

    /**
     * Displays an error dialog when an error occurs in retrieving weather data
     */
    private void displayErrorDialog() {
        String message = "Unexpected error when loading weather. Please try again.";
        ((MainActivity) getActivity()).displayErrorDialog(message);
    }
}
