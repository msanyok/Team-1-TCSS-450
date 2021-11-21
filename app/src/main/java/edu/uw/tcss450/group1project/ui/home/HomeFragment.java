/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.home;

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
import edu.uw.tcss450.group1project.databinding.FragmentContactsBinding;
import edu.uw.tcss450.group1project.databinding.FragmentHomeBinding;
import edu.uw.tcss450.group1project.model.ContactRequestViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.model.WeatherDataViewModel;
import edu.uw.tcss450.group1project.ui.contacts.ContactGenerator;
import edu.uw.tcss450.group1project.ui.contacts.ContactsRecyclerAdapter;
import edu.uw.tcss450.group1project.ui.messages.ChatRoomGenerator;
import edu.uw.tcss450.group1project.ui.messages.MessagesRecyclerAdapter;
import edu.uw.tcss450.group1project.ui.weather.WeatherDataCurrent;
import edu.uw.tcss450.group1project.ui.weather.WeatherUtils;

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
        mRequestModel = new ViewModelProvider(getActivity()).get(ContactRequestViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        if (!mWeatherModel.containsReadableHomeData()) {
            mWeatherModel.connectGet(mUserModel.getmJwt(), true);
        }
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

        UserInfoViewModel userInfo = new ViewModelProvider(this.getActivity())
                .get(UserInfoViewModel.class);
        mRequestModel.allContactRequests(userInfo.getmJwt());

        mWeatherModel.addResponseObserver(getViewLifecycleOwner(), this::observeWeatherResponse);
        mRequestModel.addRequestObserver(getViewLifecycleOwner(), this::observeRequestResponse);

        FragmentHomeBinding binding = FragmentHomeBinding.bind(getView());
        binding.welcomeText.setText(String.format("Welcome, %s!", mUserModel.getEmail()));
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, set the weather view component.
     *
     * @param theResponse from the server
     */

    private void observeWeatherResponse(final JSONObject theResponse) {
        if (theResponse.has("error")) {
            try {
                displayWeatherErrorDialog(theResponse.get("error").toString());
            } catch (JSONException ex) {
                Log.e("ERROR", "Could not parse error JSON");
            }
            mWeatherModel.clearResponse();
        } else if (theResponse.length() != 0) {
            setWeatherViewComponents();
        }
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, set the contact request list.
     *
     * @param theResponse response from the server
     */
    private void observeRequestResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                // a 400 error occurred, so log it.
                Log.e("REQUEST ERROR", theResponse.toString());
            } else if (theResponse.length() != 0) {
                setContactListComponents();
            }
        } else {
            // no response from the request
            Log.d("Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }

    /**
     * Sets the adapter and added the contacts to the main page
     */
    private void setContactListComponents() {
        FragmentHomeBinding binding = FragmentHomeBinding.bind(getView());

        UserInfoViewModel userInfo = new ViewModelProvider(this.getActivity())
                .get(UserInfoViewModel.class);
        binding.listContactRequests.setAdapter(new ContactRequestRecyclerAdapter(
                mRequestModel.getContactList(), mRequestModel, userInfo));

    }

    /**
     * Binds the weather data to the homepage
     */
    private void setWeatherViewComponents() {
        FragmentHomeBinding binding = FragmentHomeBinding.bind(getView());
        WeatherDataCurrent weatherData = mWeatherModel.getCurrentDataHome();
        binding.weatherImage.setImageResource(
                WeatherUtils.getInstance().getIconResource(weatherData.getWeatherCondition()));
        binding.weatherText.setText(String
                .format("It is %d degrees with a %d percent chance of rain.",
                        weatherData.getTemperature(), weatherData.getPrecipPercentage()));
    }

    /**
     * Displays error dialog box when error occurs with weather
     * @param theError to be thrown
     */
    private void displayWeatherErrorDialog(final String theError) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>Unexpected " +
                theError + " when loading local weather." + " Please try again.</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Ok</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }
}
