/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentHomeBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.ui.contacts.ContactGenerator;
import edu.uw.tcss450.group1project.ui.contacts.ContactsRecyclerAdapter;
import edu.uw.tcss450.group1project.ui.messages.ChatRoomGenerator;
import edu.uw.tcss450.group1project.ui.messages.MessagesRecyclerAdapter;

/**
 * A {@link Fragment} subclass that is responsible for the home page.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class HomeFragment extends Fragment {

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_home, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView, @Nullable Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);

        FragmentHomeBinding binding = FragmentHomeBinding.bind(getView());
        binding.weatherImage.setImageResource(R.drawable.ic_sun_yellow_24dp);
        binding.welcomeText.setText(String.format("Welcome, %s!", model.getEmail()));
        binding.weatherText.setText(String
                           .format("It is %d degrees with a %d percent chance of rain.", 50, 10));
        binding.listNewMessages.setAdapter(
                new MessagesNotificationsRecyclerAdapter(ChatRoomGenerator.getChatRooms()));
        binding.listContactRequests.setAdapter(
                new ContactRequestRecyclerAdapter(ContactGenerator.getContactList()));
    }
}
