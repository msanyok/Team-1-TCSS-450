/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentMessagesBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * A {@link Fragment} subclass that is responsible for the messages page.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class MessagesFragment extends Fragment {

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public MessagesFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_messages, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        UserInfoViewModel model = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
        FragmentMessagesBinding binding = FragmentMessagesBinding.bind(getView());
        binding.listRoot.setAdapter(new MessagesRecyclerAdapter(ChatRoomGenerator.getChatRooms()));
        binding.chatRoomStartButton.setOnClickListener(button -> {
            Navigation.findNavController(theView).navigate(R.id.action_navigation_messages_to_createChatroomFragment);

        });
    }
}
