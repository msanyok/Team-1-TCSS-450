/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.uw.tcss450.group1project.MainActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentWeatherParentBinding;
import edu.uw.tcss450.group1project.model.LocationViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * WeatherParentFragment is a class for displaying child weather fragments using a view pager.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherParentFragment extends Fragment {

    /** The location list view model */
    private WeatherLocationListViewModel mLocationModel;

    /** The user info view model */
    private UserInfoViewModel mUserModel;

    /**
     * Required empty constructor
     */
    public WeatherParentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(null);
        mLocationModel = new ViewModelProvider(this).get(WeatherLocationListViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        System.out.println("creating weather parent view");
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_weather_parent, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mLocationModel.connectGet(mUserModel.getJwt());
        mLocationModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeLocationListResponse);
        FragmentWeatherParentBinding binding = FragmentWeatherParentBinding.bind(theView);
        binding.searchButton.setOnClickListener(button -> {
            Navigation.findNavController(theView).navigate(
                    R.id.action_navigation_weather_parent_to_navigation_weather_location_selection);
        });
    }

    /**
     * Handles responses observed from the weather location list view model
     *
     * @param theResponse the observed JSONObject response
     */
    private void observeLocationListResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            Log.e("WEATHER LOCATION LIST REQUEST ERROR", theResponse.toString());
            displayErrorDialog();
            mLocationModel.clearResponse();
        }
        if (mLocationModel.containsReadableData()) {
            setViewComponents();
        }
    }

    /**
     * Sets this fragment's view components which include its view pager and tab view
     */
    private void setViewComponents() {
        List<WeatherFragment> frags = new LinkedList<>();
        LocationViewModel locModel =
                new ViewModelProvider(getActivity()).get(LocationViewModel.class);
        Location loc = locModel.getCurrentLocation();
        if (loc != null) {
            frags.add(new WeatherFragment(
                    new LatLong(loc.getLatitude(), loc.getLongitude()), true));
        }
        for (final LatLong ltlng : mLocationModel.getLocations()) {
            frags.add(new WeatherFragment(ltlng, false));
        }
        ViewPager pager = getView().findViewById(R.id.view_pager);
        TabLayout tabs = getView().findViewById(R.id.tab_layout);
        WeatherFragmentPagerAdapter pagerAdapter =
                new WeatherFragmentPagerAdapter(getChildFragmentManager(), frags);
        pager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(pager);
    }

    /**
     * Displays an error dialog when an error occurs in retrieving weather location data
     */
    private void displayErrorDialog() {
        String message = "Unexpected error when loading saved locations. Please try again.";
        ((MainActivity) getActivity()).displayErrorDialog(message);
    }
}