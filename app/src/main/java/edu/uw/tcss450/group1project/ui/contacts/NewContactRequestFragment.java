/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.group1project.R;

/**
 * NewContactRequestFragment is a class for searching new TalkBox members to add as contacts.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class NewContactRequestFragment extends Fragment {

    /**
     * Required empty constructor
     */
    public NewContactRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_new_contact_request, theContainer,
                false);
    }
}