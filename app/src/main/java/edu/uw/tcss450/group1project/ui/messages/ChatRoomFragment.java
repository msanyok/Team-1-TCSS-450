/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.group1project.R;

/**
 * ChatroomFragment navigate to rhe chatroom after clicking the massage card.
 *
 * @author Chris Ding
 * @version Fall 2021
 */
public class ChatRoomFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_chatroom, theContainer, false);
    }
}