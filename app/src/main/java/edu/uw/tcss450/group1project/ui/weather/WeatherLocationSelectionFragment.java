package edu.uw.tcss450.group1project.ui.weather;

import android.Manifest;
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
import edu.uw.tcss450.group1project.model.WeatherDataViewModel;

public class WeatherLocationSelectionFragment
        extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;

    private Marker mMarker;

    private boolean mLocationSet;

    private WeatherDataViewModel mWeatherModel;

    private WeatherLocationListViewModel mLocationListModel;

    private UserInfoViewModel mUserModel;

    private FragmentWeatherLocationSelectionBinding mBinding;

    public WeatherLocationSelectionFragment() {
        // Required empty public constructor
        mLocationSet = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather_location_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);
        NavBackStackEntry backStackEntry =
                navController.getBackStackEntry(R.id.navigation_weather_location_selection);
        mWeatherModel = new ViewModelProvider(backStackEntry).get(WeatherDataViewModel.class);
        mWeatherModel.clearResponse();
        backStackEntry = navController.getBackStackEntry(R.id.navigation_weather_parent);
        mLocationListModel = new ViewModelProvider(backStackEntry).get(
                WeatherLocationListViewModel.class);
        mLocationListModel.clearAdditionResponse();
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        mBinding = FragmentWeatherLocationSelectionBinding.bind(view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //add this fragment as the OnMapReadyCallback -> See onMapReady()
        mapFragment.getMapAsync(this);
        mBinding.searchButton.setOnClickListener(button -> initiateSearchRequest());
        mBinding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBinding.searchText.setError(null);
                if (!mBinding.searchText.getText().toString().equals("Dropped pin")) {
                    if (mMarker != null) {
                        mMarker.remove();
                        mMarker = null;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationViewModel model = new ViewModelProvider(getActivity())
                .get(LocationViewModel.class);
        model.addLocationObserver(getViewLifecycleOwner(), location -> {
            if (location != null) {
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
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
    public void onMapClick(@NonNull LatLng latLng) {
        if (mMarker != null) {
            mMarker.remove();
        }
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("New Marker"));
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        latLng, mMap.getCameraPosition().zoom));
        mBinding.searchText.setText("Dropped pin");
    }

    private void initiateSearchRequest() {
        if (mMarker != null) {
            String latLong = new LatLong(mMarker.getPosition().latitude,
                    mMarker.getPosition().longitude).toString();
            mWeatherModel.addResponseObserver(
                    getViewLifecycleOwner(),
                    response -> observeSearchAttemptResponse(
                            response, mBinding.saveCheckbox.isChecked(), latLong));
            mWeatherModel.connectGet(mUserModel.getJwt(), latLong, true);
        } else {
            boolean valid = false;
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
                String zip = String.valueOf(zipCode);
                mWeatherModel.addResponseObserver(
                        getViewLifecycleOwner(),
                        response -> observeSearchAttemptResponse(
                                response, mBinding.saveCheckbox.isChecked(), zip));
                mWeatherModel.connectGet(mUserModel.getJwt(), zip, true);
            } else {
                mBinding.searchText.setError("Zip code must be a positive, 5 digit value!");
            }
        }
    }

    private void observeSearchAttemptResponse(final JSONObject theResponse,
                                              final boolean theChecked, final String theLocation) {
        if (theResponse.has("code")) {
            Log.e("SERVER FAILURE TO SUPPORT LOCATION", theResponse.toString());
            displayErrorDialog("The supplied location " +
                    "is not currently supported. Please try again.");
            mWeatherModel.clearResponse();
        } else if (theResponse.length() != 0) {
            Runnable navRun = () -> {
                String city = mWeatherModel.getCurrentData().getCity().split(",")[0];
                WeatherLocationSelectionFragmentDirections.
                        ActionNavigationWeatherLocationSelectionToNavigationWeatherTeaser action =
                        WeatherLocationSelectionFragmentDirections.
                                actionNavigationWeatherLocationSelectionToNavigationWeatherTeaser(
                                        "Weather for " + city);
                Navigation.findNavController(getView()).navigate(action);
            };
            if (theChecked) {
                mLocationListModel.addAdditionResponseObserver(
                        getViewLifecycleOwner(),
                        response -> observeLocationAdditionResponse(response, navRun));
                mLocationListModel.connectPost(mUserModel.getJwt(), theLocation);
            } else {
                navRun.run();
            }
        }
    }

    private void observeLocationAdditionResponse(final JSONObject theResponse,
                                                 final Runnable theNavRun) {
        if (theResponse.has("code")) { // error or list size limit met
            System.out.println("error!");
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
            displayErrorDialog(message);
            mLocationListModel.clearAdditionResponse();
        }
        if (theResponse.length() != 0) {
            Toast.makeText(getContext(),"Location saved", Toast.LENGTH_SHORT).show();
            theNavRun.run();
        }
    }

    private void displayErrorDialog(final String theMessage) {
        ((MainActivity) getActivity()).displayErrorDialog(theMessage);
    }
}