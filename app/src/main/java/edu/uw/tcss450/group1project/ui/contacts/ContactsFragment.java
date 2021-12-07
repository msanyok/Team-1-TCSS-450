/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;


import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentContactsBinding;
import edu.uw.tcss450.group1project.model.ContactTabNewCountViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * A {@link Fragment} subclass that is responsible for the contacts page.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @author Steven Omegna
 * @version Fall 2021
 */
public class ContactsFragment extends Fragment {

    /** ViewBinding reference to the Contact Fragment UI */
    private FragmentContactsBinding mBinding;

    /** ViewModel for contacts */
    private ContactsViewModel mContactsModel;

    /** The user info view model */
    private UserInfoViewModel mUserInfo;

    /** The TextWatcher that filters the contacts list */
    private TextWatcher mTextWatcher;

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
        mUserInfo = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentContactsBinding.inflate(theInflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mContactsModel.addContactListObserver(getViewLifecycleOwner(),
                this::observeContactResponse);
        mContactsModel.addContactDeleteObserver(getViewLifecycleOwner(),
                this::observeDeleteResponse);
        mContactsModel.contactsConnect(mUserInfo.getJwt());

        Spinner spinner = (Spinner) getView().findViewById(R.id.contact_search_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.contact_search_array, R.layout.fragment_contacts_spinner);
        adapter.setDropDownViewResource(R.layout.fragment_contacts_spinner_dropdown);


        spinner.setAdapter(adapter);

        // save instance of this class so we can use it inside
        final ContactsFragment thisFragment = this;

        // create a text watcher that will filter the contact list
        // when the user types into the filter text
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence theCharSequence,
                                          final int theI,
                                          final int theI1,
                                          final int theI2) {
                // not used
            }
            @Override
            public void afterTextChanged(final Editable theEditable) {
                // not used
            }

            @Override
            public void onTextChanged(final CharSequence theEnteredText,
                                      final int theStartIndex,
                                      final int theBefore,
                                      final int theCount) {

                final String identifierType = spinner.getSelectedItem().toString();
                final String firstChars = theEnteredText.toString().toLowerCase();

                final List<Contact> originalList = mContactsModel.getContactList();
                final List<Contact> newList =
                        originalList.stream().filter((contact) -> {

                            String identifier = "";
                            if (identifierType.equals("Nickname")) {
                                identifier = contact.getNickname();
                            } else if (identifierType.equals("First Name")) {
                                identifier = contact.getFirst();
                            } else if (identifierType.equals("Last Name")) {
                                identifier = contact.getLast();
                            }
                            identifier = identifier.toLowerCase(Locale.ROOT);


                            return identifier.startsWith(firstChars);
                        }).collect(Collectors.toList());

                mBinding.contactList.setAdapter(new ContactsRecyclerAdapter(newList,
                        thisFragment::showContactDeleteAlertDialog));
            }
        };

        // add the change listener that will filter contacts
        // when the state of the text field changes
        mBinding.addContactText.addTextChangedListener(mTextWatcher);

        // add actions that occur when the user changes the type of filtering
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> theParentView,
                                       final View theSelectedItemView,
                                       final int thePosition,
                                       final long theId) {
                // the user just changed the type of identifier the user wants to use
                // to filter contacts, so tell the text listener to refilter.
                mTextWatcher.onTextChanged(mBinding.addContactText.getText().toString(),
                        0, 0, 0);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> theParentView) {
                // unused
            }

        });


    }



    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, add contacts to recycler view
     *
     * @param theResponse the Response from the server
     */
    private void observeContactResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                // a 400 error occurred, so log it.
                Log.e("Contact List Error", theResponse.toString());
                // TODO: Handle UI change when the chat list is not received properly?
            } else {
                mBinding.contactList.setAdapter(new ContactsRecyclerAdapter(
                        mContactsModel.getContactList(), this::showContactDeleteAlertDialog));
                mContactsModel.removeData();
                mBinding.addContactText.setError(null);
                mTextWatcher.onTextChanged(mBinding.addContactText.getText().toString(),
                        0, 0, 0);
            }
        } else {
            // no response from the request
            Log.d("Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, delete contacts from recycler view
     *
     * @param theResponse the Response from the server
     */
    private void observeDeleteResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            Log.e("Contact List Error", theResponse.toString());
        } else if (theResponse.length() != 0){
            Toast.makeText(getContext(),"You have deleted a contact.",
                    Toast.LENGTH_SHORT).show();
            mContactsModel.removeData();
        }
    }

    /**
     * Function of warning for deleting a contact using alert dialog
     *
     * @param theContact theContact to be deleted
     */
    public void showContactDeleteAlertDialog(final Contact theContact) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>Deleting this contact " +
                "will be permanent. Are you sure?</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Delete</font>"),
                (dialog, which) -> {
                    mContactsModel.sendDeleteResponse(mUserInfo.getJwt(), theContact.getMemberId());
                });
        alertDialog.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }
}