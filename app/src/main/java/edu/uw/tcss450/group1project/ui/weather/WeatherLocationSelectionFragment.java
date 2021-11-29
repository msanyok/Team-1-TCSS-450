package edu.uw.tcss450.group1project.ui.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

    private boolean mPinDropped;

    private WeatherDataViewModel mWeatherModel;

    private UserInfoViewModel mUserModel;

    private FragmentWeatherLocationSelectionBinding mBinding;

    public WeatherLocationSelectionFragment() {
        // Required empty public constructor
        mPinDropped = false;
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
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationViewModel model = new ViewModelProvider(getActivity())
                .get(LocationViewModel.class);
        model.addLocationObserver(getViewLifecycleOwner(), location -> {
            if (location != null && !mPinDropped) {
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 15.0f));
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
        mPinDropped = true;
        mBinding.searchText.setText("Dropped pin");
    }

    private void initiateSearchRequest() {
        if (mMarker != null) {
            mWeatherModel.addResponseObserver(
                    getViewLifecycleOwner(), this::observeSearchAttemptResponse);
            mWeatherModel.connectGet(mUserModel.getJwt(),
                    new LatLong(mMarker.getPosition().latitude,
                            mMarker.getPosition().longitude).toString(), false);
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
                mWeatherModel.addResponseObserver(
                        getViewLifecycleOwner(), this::observeSearchAttemptResponse);
                mWeatherModel.connectGet(mUserModel.getJwt(), String.valueOf(zipCode), false);
            } else {
                mBinding.searchText.setError("Zip code must be a positive, 5 digit value!");
            }
        }
    }

    private void observeSearchAttemptResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            Log.e("SERVER FAILURE TO SUPPORT LOCATION", theResponse.toString());
            displayErrorDialog();
            mWeatherModel.clearResponse();
        } else if (theResponse.length() != 0) {
            String city = mWeatherModel.getCurrentData().getCity().split(",")[0];
            WeatherLocationSelectionFragmentDirections.
                    ActionNavigationWeatherLocationSelectionToNavigationWeatherTeaser action =
                    WeatherLocationSelectionFragmentDirections.
                            actionNavigationWeatherLocationSelectionToNavigationWeatherTeaser(
                                    "Weather for " + city);
            Navigation.findNavController(getView()).navigate(action);
        }
    }

    private void displayErrorDialog() {
        ((MainActivity) getActivity()).displayErrorDialog("The supplied location " +
                "is not currently supported. Please try again.");
    }
}