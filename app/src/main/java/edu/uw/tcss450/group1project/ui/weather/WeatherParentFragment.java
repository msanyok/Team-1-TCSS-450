/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
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

    private ViewPager mPager;

    private WeatherFragmentPagerAdapter mAdapter;

    /** The index of the view being shown */
    private int mViewIndex;

    /**
     * Required empty constructor
     */
    public WeatherParentFragment() {
    }

    @Override
    public void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(null);
        mViewIndex = 0;
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_weather_parent, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        NavController navController = Navigation.findNavController(theView);
        NavBackStackEntry backStackEntry =
                navController.getBackStackEntry(R.id.navigation_weather_parent);
        mLocationModel =
                new ViewModelProvider(backStackEntry).get(WeatherLocationListViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
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
            displayErrorDialog("Unexpected error when loading saved locations. Please try again.");
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
                    new LatLong(loc.getLatitude(), loc.getLongitude()), false,
                    this::displayLocationDeleteDialog));
        }
        for (final LatLong ltlng : mLocationModel.getLocations()) {
            frags.add(new WeatherFragment(ltlng, true, this::displayLocationDeleteDialog));
        }
        mPager = getView().findViewById(R.id.view_pager);
        TabLayout tabs = getView().findViewById(R.id.tab_layout);
        mAdapter = new WeatherFragmentPagerAdapter(getChildFragmentManager(), frags);
        mPager.setAdapter(mAdapter);
        tabs.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mViewIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        mPager.setCurrentItem(mViewIndex);
    }

    /**
     * Displays an error dialog when an error occurs in retrieving weather location data
     *
     * @param theMessage the message to display
     */
    private void displayErrorDialog(final String theMessage) {
        ((MainActivity) getActivity()).displayErrorDialog(theMessage);
    }

    private void displayLocationDeleteDialog(final LatLong theLatLong) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>Are you sure " +
                "you want to delete this location?</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Delete</font>"),
                (dialog, which) -> {
                    mLocationModel.addDeletionResponseObserver(getViewLifecycleOwner(),
                            response -> observeDeleteResponse(response, theLatLong));
                    mLocationModel.connectDelete(mUserModel.getJwt(), theLatLong.toString());
                });
        alertDialog.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }

    private void observeDeleteResponse(final JSONObject theResponse, final LatLong theRemove) {
        if (theResponse.has("code")) {
            Log.e("LOCATION DELETE ERROR", theResponse.toString());
            displayErrorDialog("An unexpected error occurred when deleting location." +
                    " Please try again.");
            mLocationModel.clearDeletionResponse();
        } else if (theResponse.length() != 0) {
            mLocationModel.clearDeletionResponse();
            mLocationModel.connectGet(mUserModel.getJwt());
            Toast.makeText(getContext(), "Location deleted.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}