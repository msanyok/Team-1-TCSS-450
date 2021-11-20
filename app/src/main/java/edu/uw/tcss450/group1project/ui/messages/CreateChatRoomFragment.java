/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentCreateChatroomBinding;
import edu.uw.tcss450.group1project.ui.contacts.ContactGenerator;

/**
 * CreateChatroomFragment provides a new chatroom after clicking the create button.
 *
 * @author Chris Ding
 * @version Fall 2021
 */
public class CreateChatRoomFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        Log.d("TAG", "CREATING");
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_create_chatroom, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        FragmentCreateChatroomBinding binding = FragmentCreateChatroomBinding.bind(getView());
        ChatRoomParticipantViewModel model =
                new ViewModelProvider(this).get(ChatRoomParticipantViewModel.class);
        binding.listRoot.setAdapter(
                new ParticipantSelectorRecyclerAdapter(ContactGenerator.getContactList(), model));
    }
}