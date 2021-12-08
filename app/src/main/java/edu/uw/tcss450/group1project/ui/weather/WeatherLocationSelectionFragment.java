/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.group1project.MainActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentWeatherLocationSelectionBinding;
import edu.uw.tcss450.group1project.model.LocationViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * WeatherLocationSelectionFragment is a class for selecting and/or saving new weather locations.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class WeatherLocationSelectionFragment
        extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    /** The Google map */
    private GoogleMap mMap;

    /** The user's selected location marker */
    private Marker mMarker;

    /** Indicates whether or not the user's location has been determined */
    private boolean mLocationSet;

    /** The view model responsible for data retrieval */
    private WeatherDataViewModel mWeatherModel;

    /** The view model responsible for saved locations management */
    private WeatherLocationListViewModel mLocationListModel;

    /** The user info view model */
    private UserInfoViewModel mUserModel;

    /** The view binding */
    private FragmentWeatherLocationSelectionBinding mBinding;

    /** The user location input string-formatted */
    private String mLocationString;

    /** Indicates if the user would like to save the chosen location */
    private boolean mSaveChecked;

    /**
     * Required empty constructor
     */
    public WeatherLocationSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mLocationSet = false;
        mLocationString = "";
        mSaveChecked = false;
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(
                R.layout.fragment_weather_location_selection, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        NavController navController = Navigation.findNavController(theView);
        NavBackStackEntry backStackEntry =
                navController.getBackStackEntry(R.id.navigation_weather_location_selection);
        mWeatherModel = new ViewModelProvider(backStackEntry).get(WeatherDataViewModel.class);
        mWeatherModel.clearResponse();
        backStackEntry = navController.getBackStackEntry(R.id.navigation_weather_parent);
        mLocationListModel = new ViewModelProvider(backStackEntry).get(
                WeatherLocationListViewModel.class);
        mLocationListModel.clearAdditionResponse();
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        mBinding = FragmentWeatherLocationSelectionBinding.bind(theView);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //add this fragment as the OnMapReadyCallback -> See onMapReady()
        mapFragment.getMapAsync(this);
        mBinding.searchButton.setOnClickListener(button -> initiateSearchRequest());
        mBinding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    final CharSequence theSeq, final int theI, final int theI1, final int theI2) {}

            @Override
            public void onTextChanged(
                    final CharSequence theSeq, final int theI, final int theI1, final int theI2) {
                mBinding.searchText.setError(null);
                if (!mBinding.searchText.getText().toString().equals("Dropped pin")) {
                    if (mMarker != null) {
                        mMarker.remove();
                        mMarker = null;
                    }
                }
            }

            @Override
            public void afterTextChanged(final Editable theEditable) {}
        });
        mWeatherModel.addResponseObserver(
                getViewLifecycleOwner(), this::observeSearchAttemptResponse);
        mLocationListModel.addAdditionResponseObserver(
                            getViewLifecycleOwner(), this::observeLocationAdditionResponse);
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap theMap) {
        mMap = theMap;
        LocationViewModel model = new ViewModelProvider(getActivity())
                .get(LocationViewModel.class);
        model.addLocationObserver(getViewLifecycleOwner(), location -> {
            if (location != null) {
                theMap.getUiSettings().setZoomControlsEnabled(true);
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                theMap.setMyLocationEnabled(true);
                final LatLng c = new LatLng(location.getLatitude(), location.getLongitude());
                //Zoom levels are from 2.0f (zoomed out) to 21.f (zoomed in)
                if (!mLocationSet) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 15.0f));
                    mLocationSet = true;
                }
            }
        });
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull final LatLng theLatLong) {
        if (mMarker != null) {
            mMarker.remove();
        }
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(theLatLong)
                .title("New Marker"));
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        theLatLong, mMap.getCameraPosition().zoom));
        mBinding.searchText.setText("Dropped pin");
    }

    /**
     * Begins a weather location search request either by lat long or zip code.
     */
    private void initiateSearchRequest() {
        InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        }
        boolean valid = mLocationString.isEmpty();
        if (valid) {
            if (mMarker != null) {
                mLocationString = new LatLong(mMarker.getPosition().latitude,
                        mMarker.getPosition().longitude).toString();
                mSaveChecked = mBinding.saveCheckbox.isChecked();
            } else {
                valid = false;
                int zipCode = 0;
                String input = mBinding.searchText.getText().toString().trim();
                if (input.length() == 5) {
                    try {
                        zipCode = Integer.valueOf(input);
                        if (zipCode >= 0) {
                            valid = true;
                        }
                    } catch (NumberFormatException ex) {
                        // the user did not enter a number
                    }
                }
                if (valid) {
                    mLocationString = String.valueOf(zipCode);
                    mSaveChecked = mBinding.saveCheckbox.isChecked();
                } else {
                    mBinding.searchText.setError("Zip code must be a positive, 5 digit value!");
                }
            }
            if (valid) {
                mWeatherModel.connectGet(mUserModel.getJwt(), mLocationString, true);
            }
        }
    }

    /**
     * Observes a server response from a weather location data search
     *
     * @param theResponse the observed response
     */
    private void observeSearchAttemptResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            Log.e("SERVER FAILURE TO SUPPORT LOCATION", theResponse.toString());
            displayErrorDialog("The supplied location " +
                    "is not currently supported. Please try again.");
            mLocationString = "";
        } else if (theResponse.length() != 0) {
            if (mSaveChecked) {
                mLocationListModel.connectPost(mUserModel.getJwt(), mLocationString);
            } else {
                mLocationString = "";
                String city = mWeatherModel.getCurrentData().getCity().split(",")[0];
                WeatherLocationSelectionFragmentDirections.
                        ActionNavigationWeatherLocationSelectionToNavigationWeatherTeaser action =
                        WeatherLocationSelectionFragmentDirections.
                                actionNavigationWeatherLocationSelectionToNavigationWeatherTeaser(
                                        city);
                Navigation.findNavController(getView()).navigate(action);
            }
        }
    }

    /**
     * Observes a server response for adding a saved weather location
     *
     * @param theResponse the observed response
     */
    private void observeLocationAdditionResponse(final JSONObject theResponse) {
        boolean displayToast = true;
        if (theResponse.has("code")) { // error or list size limit met
            displayToast = false;
            String message = "An unexpected error occurred when adding to saved locations. " +
                "Please try again.";
            if (theResponse.has("data")) {
                try {
                    String issue = theResponse.getJSONObject("data").getString("message");
                    if (issue.equalsIgnoreCase("Location Storage Full")) {
                        message = "Unable to save location, maximum of 10 stored locations" +
                    " reached! Please delete a location and try again.";
                    } else if (issue.equalsIgnoreCase("ZIP to lat/lon API Error")) {
                        message = "The provided zip code is invalid and cannot be saved. Please " +
                                "try again.";
                    } else if (issue.equalsIgnoreCase("Location already exists")) {
                        message = "It looks like you've already saved this location. We didn't " +
                                "add it again.";
                    }
                } catch (JSONException ex) {
                    Log.e("JSON PARSE ERROR IN LOCATION ADD OBSERVER", ex.getMessage());
                }
            }
            mLocationString = "";
            displayErrorDialog(message);
        }
        if (theResponse.length() != 0) {
            mLocationString = "";
            if (displayToast) {
                Toast.makeText(getContext(),"Location saved", Toast.LENGTH_SHORT).show();
            }
            String city = mWeatherModel.getCurrentData().getCity().split(",")[0];
            WeatherLocationSelectionFragmentDirections.
                    ActionNavigationWeatherLocationSelectionToNavigationWeatherTeaser action =
                    WeatherLocationSelectionFragmentDirections.
                            actionNavigationWeatherLocationSelectionToNavigationWeatherTeaser(city);
            Navigation.findNavController(getView()).navigate(action);
        }
    }

    /**
     * Displays an error dialog to the user upon unsuccessful search or location addition
     *
     * @param theMessage the custom dialog message
     */
    private void displayErrorDialog(final String theMessage) {
        ((MainActivity) getActivity()).displayErrorDialog(theMessage);
    }
}