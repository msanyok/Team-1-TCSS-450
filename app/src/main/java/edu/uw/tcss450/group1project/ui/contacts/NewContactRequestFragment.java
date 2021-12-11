/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentNewContactRequestBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;


/**
 * NewContactRequestFragment is a class for searching new TalkBox members to add as contacts.
 *
 * @author Parker Rosengreen
 * @author Steven Omegna
 * @version Fall 2021
 */
public class NewContactRequestFragment extends Fragment {

    /** ViewBinding reference to the Contact Fragment UI */
    private FragmentNewContactRequestBinding mBinding;

    /** ViewModel for contacts */
    private NewContactsRequestViewModel mContactsRequestModel;

    /** User View Model for Jwt*/
    private UserInfoViewModel mUserModel;

    /**
     * TextWatcher class for text changes
     */
    private TextWatcher mTextWatcher;

    /**
     * Boolean to determine if a new watcher needs to be created
     */
    private boolean mWatcherAssigned;

    /**
     * Required empty constructor
     */
    public NewContactRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mContactsRequestModel =
                new ViewModelProvider(getActivity()).get(NewContactsRequestViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_new_contact_request, theContainer,
                false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mWatcherAssigned = false;
        mBinding = FragmentNewContactRequestBinding.bind(getView());

        // set the observer for the contact search list
        mContactsRequestModel.addContactSearchListObserver(getViewLifecycleOwner(),
                this::observeContactSearchListResponse);

        // set the observer for the new contact request
        mContactsRequestModel.addContactAddObserver(getViewLifecycleOwner(),
                this::observeContactResponse);

        Spinner spinner = (Spinner) getView().findViewById(R.id.request_search_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.request_search_array, R.layout.fragment_contacts_spinner);
        adapter.setDropDownViewResource(R.layout.fragment_contacts_spinner_dropdown);
        spinner.setAdapter(adapter);

        //set the observer for the contact request button
        mBinding.contactRequestButton.setOnClickListener(this::requestToBeSent);

        mTextWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence theString, int start, int before, int count) {
                if(theString.toString().isEmpty()) {
                    mBinding.listRoot.setAdapter(null);
                } else {
                    updateContactsSearch();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        // Tells the spinner what to do when selection is changed
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> theParentView,
                                       final View theSelectedItemView,
                                       final int thePosition,
                                       final long theId) {
                // the user just changed the type of identifier the user wants to use
                if (thePosition != 3) {
                    if (!mWatcherAssigned) {
                        mWatcherAssigned = true;
                        mBinding.addContactText.addTextChangedListener(mTextWatcher);
                    }
                    mBinding.contactRequestButton.setVisibility(View.GONE);
                    mBinding.listRoot.setVisibility(View.VISIBLE);
                    if (!mBinding.addContactText.getText().toString().isEmpty()) {
                        updateContactsSearch();
                    }
                } else {
                    mBinding.contactRequestButton.setVisibility(View.VISIBLE);
                    mBinding.listRoot.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(final AdapterView<?> theParentView) {
                // unused
            }

        });

    }

    /**
     * Calls the HTTP method to update the current adapter for a new contact search
     */
    private void updateContactsSearch() {
        int thePosition = mBinding.requestSearchSpinner.getSelectedItemPosition();
        String theString = mBinding.addContactText.getText().toString();
        if(thePosition == 0) {
            mContactsRequestModel.requestConnect("nickname", theString.toString(),
                    mUserModel.getJwt());
        } else if (thePosition == 1) { ;
            mContactsRequestModel.requestConnect("firstname", theString.toString(),
                    mUserModel.getJwt());
        } else if (thePosition == 2) {
            mContactsRequestModel.requestConnect("lastname", theString.toString(),
                    mUserModel.getJwt());
        }
    }


    /**
     * Sends the request for a contact request for the request button
     * @param theView the view
     */
    private void requestToBeSent(View theView) {
        mContactsRequestModel.contactRequestConnect(mBinding.addContactText.getText().toString(),
                "email", mUserModel.getJwt());
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, set the contact request list.
     *
     * @param theResponse response from the server
     */
    private void observeContactSearchListResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                // a 400 error occurred, so log it.
                Log.e("REQUEST ERROR", theResponse.toString());
            } else if (theResponse.length() != 0) {

                if (!mBinding.addContactText.getText().toString().isEmpty()) {
                    mBinding.listRoot.setAdapter(new NewContactRequestRecyclerAdapter(
                        mContactsRequestModel.getContactList(), this::setRequests));
                }
            }
        } else {
            // no response from the request
            Log.d("Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, set the contact request list.
     *
     * @param theResponse response from the server
     */
    private void observeContactResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                try {
                    final String message =
                            theResponse.getJSONObject("data").get("message").toString();

                    if (message.equals("Can not create contact with oneself")) {
                        mBinding.addContactText.setError("Cannot be friends with yourself");
                    } else if (message.equals("Members are already contacts")) {
                        mBinding.addContactText.setError("Members are already contacts");
                    } else if (message.equals("Contact request already exists")) {
                        mBinding.addContactText.setError("Contact request already exists");
                    } else {
                        mBinding.addContactText.setError("Other error. Check logs.");
                    }

                } catch (JSONException exception) {
                    Log.e("JSON Parse Error", exception.getMessage());
                }
                Log.e("REQUEST ERROR", theResponse.toString());
            } else {
                String theString = mBinding.addContactText.getText().toString();
                int thePosition = mBinding.requestSearchSpinner.getSelectedItemPosition();
                if(thePosition== 0) {
                    mContactsRequestModel.requestConnect("nickname", theString,
                            mUserModel.getJwt());
                } else if (thePosition == 1) { ;
                    mContactsRequestModel.requestConnect("firstname", theString,
                            mUserModel.getJwt());
                } else if (thePosition == 2) {
                    mContactsRequestModel.requestConnect("lastname", theString,
                            mUserModel.getJwt());
                }
                //toast for contact
                Toast toast = new Toast(getContext());
                toast.makeText(getContext(),"A contact request has been sent",
                        Toast.LENGTH_SHORT).show();
                mContactsRequestModel.removeData();
            }
        } else {
            // no response from the request
            Log.d("Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }

    }

    /**
     * Sends the request for a contact request from a search
     *
     */
    private void setRequests(final Contact theContact) {
        mContactsRequestModel.contactRequestConnect(theContact.getNickname(), "nickname",
                mUserModel.getJwt());

    }

}