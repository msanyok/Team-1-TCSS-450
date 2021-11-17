package edu.uw.tcss450.group1project.ui.messages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.group1project.R;

/**
 * CreateChatroomFragment provides a new chatroom after clicking the create button.
 *
 * @author Chris Ding
 * @Version Fall 2021
 */
public class CreateChatroomFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_create_chatroom, theContainer, false);
    }
}