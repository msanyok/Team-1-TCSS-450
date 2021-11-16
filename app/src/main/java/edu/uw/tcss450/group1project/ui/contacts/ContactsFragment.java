/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentContactsBinding;
import edu.uw.tcss450.group1project.databinding.FragmentSignInBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.ui.auth.register.RegisterViewModel;
import edu.uw.tcss450.group1project.ui.auth.verification.RegisterVerificationViewModel;
import edu.uw.tcss450.group1project.utils.TextFieldHints;
import edu.uw.tcss450.group1project.utils.TextFieldValidators;

/**
 * A {@link Fragment} subclass that is responsible for the contacts page.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ContactsFragment extends Fragment {

    /** ViewBinding reference to the Sign in Fragment UI */
    private FragmentContactsBinding mBinding;

    private ContactsViewModel mContactsModel;

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public ContactsFragment() {
        // required empty constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mContactsModel = new ViewModelProvider(getActivity())
                .get(ContactsViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        //return theInflater.inflate(R.layout.fragment_contacts, theContainer, false);

        mBinding = FragmentContactsBinding.inflate(theInflater);

        // Inflate the layout for this fragment
        return mBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        UserInfoViewModel model = new ViewModelProvider(getActivity())
                                                        .get(UserInfoViewModel.class);

        FragmentContactsBinding binding = FragmentContactsBinding.bind(getView());
        binding.listRoot.setAdapter(new ContactsRecyclerAdapter(ContactGenerator.getContactList()));


        binding.contactRequestButton.setOnClickListener(this::requestToBeSent);
        mContactsModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);


    }

    private void requestToBeSent(final View theButton) {
        validateNickname();
    }

    private void validateNickname() {
        final String nickNameText = mBinding.addContactText.getText().toString().trim();
        TextFieldValidators.NAME_VALIDATOR.processResult(
                TextFieldValidators.NAME_VALIDATOR.apply(nickNameText),
                this::verifyNameWithServer,
                result -> mBinding.addContactText.setError(TextFieldHints.getNameHint(nickNameText)));
    }

    private void verifyNameWithServer() {
        //populate the chat list when the fragment is shown
        UserInfoViewModel userInfo = new ViewModelProvider(this.getActivity())
                .get(UserInfoViewModel.class);
        final String theJWT = userInfo.getmJwt();
        mContactsModel.connect(
                mBinding.addContactText.getText().toString(), theJWT);


        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, navigate to the registration verification page.
     *
     * @param theResponse the Response from the server
     */
    private void observeResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                try {

                    final String message =
                            theResponse.getJSONObject("data").get("message").toString();

                    if (message.equals("Nickname does not exist")) {
                        mBinding.addContactText.setError(
                                "Error requesting contact" +
                                        theResponse.getJSONObject("data")
                                                .getString("message"));
                    } else if (message.equals("Can not create contact with oneself")) {
                        mBinding.addContactText.setError("Cannot be friends with yourself");
                    } else {
                        // a different registration error occurred that
                        // was not a duplicate email or nickname
                        mBinding.addContactText.setError("Other error. Check logs.");
                    }

                } catch (JSONException exception) {
                    Log.e("JSON Parse Error", exception.getMessage());
                }
            } else {
                //send a success message
            }
        } else {
            Log.d("Registration JSON Response", "No Response");
        }
    }

}