package edu.uw.tcss450.group1project.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentContactsInfoBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * @author Chris Ding
 * @version Fall 2021
 */
public class ContactsInfoFragment extends Fragment {


    public ContactsInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Local access to the ViewBinding object. No need to create as Instance Var as it is only
        //used here.
        FragmentContactsInfoBinding binding = FragmentContactsInfoBinding.bind(getView());
        //UserInfoViewModel model = new ViewModelProvider(getActivity())
        //       .get(UserInfoViewModel.class);

        //binding.textEmail.setText("Welcome to contact information page " + model.getEmail() + "!");
    }
}