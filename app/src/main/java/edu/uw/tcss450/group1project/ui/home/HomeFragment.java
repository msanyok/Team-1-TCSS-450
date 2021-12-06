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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uw.tcss450.group1project.MainActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentHomeBinding;
import edu.uw.tcss450.group1project.ui.contacts.ContactRequestViewModel;
import edu.uw.tcss450.group1project.model.LocationViewModel;
import edu.uw.tcss450.group1project.model.NewMessageCountViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.ui.weather.WeatherDataViewModel;
import edu.uw.tcss450.group1project.ui.messages.ChatRoom;
import edu.uw.tcss450.group1project.ui.messages.ChatsListViewModel;
import edu.uw.tcss450.group1project.ui.weather.LatLong;
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

    /** View model that stores the list of chat rooms */
    private ChatsListViewModel mChatListModel;

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
        mChatListModel = new ViewModelProvider(getActivity()).get(ChatsListViewModel.class);
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
        locModel.addLocationObserver(getViewLifecycleOwner(), (loc) -> {
            if (loc != null) {
                mWeatherModel.connectGet(
                        mUserModel.getJwt(),
                        new LatLong(loc.getLatitude(), loc.getLongitude()).toString(), false);
            }
        });

        mChatListModel.addResponseObserver(
                getViewLifecycleOwner(), this::observerMissedChatsResponse);
        mChatListModel.getChatListData(mUserModel.getJwt());

        mWeatherModel.addResponseObserver(getViewLifecycleOwner(), this::observeWeatherResponse);

        mBinding = FragmentHomeBinding.bind(getView());
        mBinding.welcomeText.setText(String.format("Welcome, %s!", mUserModel.getNickname()));

        mBinding.listNewMessages.setAdapter(
                new MessagesNotificationsRecyclerAdapter(new ArrayList<>()));
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


    /**
     * Observe the response from getting the chat room list
     * @param theResponse the response from the endpoint
     */
    private void observerMissedChatsResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                // a 400 error occurred, so log it.
                Log.e("MISSED CHATS ERROR", theResponse.toString());

            } else if (theResponse.length() != 0) {
                setMissedMessagesComponents(theResponse);
            }
        } else {
            // no response from the request
            Log.d("Missed Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }

    /**
     * Parses and sets the data for missed message chat rooms
     * @param theResponse the raw data from the endpoint
     */
    private void setMissedMessagesComponents(final JSONObject theResponse) {
        // parse the response and turn it into a new ChatRoom list
        final List<ChatRoom> newMessagesChatList = new ArrayList<>();
        NewMessageCountViewModel newMessageModel =
                new ViewModelProvider(getActivity()).get(NewMessageCountViewModel.class);

        try {
            JSONArray chats = theResponse.getJSONArray("data");

            for (int i = 0; i < chats.length(); i++) {
                // the names of the get(...) fields are determined
                // by the server and can be found in the documentation

                JSONObject chat = (JSONObject) chats.get(i);
                int numNewMessages = newMessageModel.getNumNewMessages(
                        Integer.valueOf(chat.get("chatid").toString()));

                // only parse/add the chats that have new messages
                if (numNewMessages > 0) {
                    newMessagesChatList.add(new ChatRoom(chat.get("chat_name").toString(),
                            chat.get("chatid").toString(),
                            chat.get("message").toString(),
                            chat.get("timestamp").toString(),
                            newMessageModel.getNumNewMessages(
                                    Integer.valueOf(chat.get("chatid").toString()))));
                }

            }

            // once the list has been repopulated, sort the chat rooms based on the timestamp
            // of the most recent message sent
            Collections.sort(newMessagesChatList);

        } catch (JSONException exception) {
            // should we do something specific here if the json isn't parsed properly/
            exception.printStackTrace();
        }

        mBinding.listNewMessages.setAdapter(new MessagesNotificationsRecyclerAdapter(newMessagesChatList));
    }



}
