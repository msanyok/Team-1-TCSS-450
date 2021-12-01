/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentContactsBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.utils.TextFieldHints;
import edu.uw.tcss450.group1project.utils.TextFieldValidators;

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

    /** ViewModel for contacts */
    private ContactsViewModel mContactDeleteModel;

    /** The user info view model */
    private UserInfoViewModel mUserInfo;

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
        mBinding.contactRequestButton.setOnClickListener(this::requestToBeSent);
        mContactsModel.addContactRequestObserver(getViewLifecycleOwner(),
                this::observeResponse);
        mContactsModel.addContactDeleteObserver(getViewLifecycleOwner(),
                this::observeDeleteResponse);
        mContactsModel.contactsConnect(mUserInfo.getJwt());

        Spinner spinner = (Spinner) getView().findViewById(R.id.contact_search_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.contact_search_array, R.layout.fragment_contacts_spinner);
        adapter.setDropDownViewResource(R.layout.fragment_contacts_spinner_dropdown);
        spinner.setAdapter(adapter);
    }

    /**
     * Starts the attempt to validate the Nickname before sending the request.
     *
     * @param theButton the Button that was pressed to invoke this method.
     */
    private void requestToBeSent(final View theButton) {
        //hides keyboard
        InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        }
        validateNickname();
    }

    /**
     * Attempts to validate the text inputted for the user's nickname.
     *
     * If the validation succeeds, attempts send
     * Else, sets an error text on the nickname field that requests they enter a valid nickname.
     */
    private void validateNickname() {
        final String nickNameText = mBinding.addContactText.getText().toString().trim();
        TextFieldValidators.NAME_VALIDATOR.processResult(
                TextFieldValidators.NAME_VALIDATOR.apply(nickNameText),
                this::verifyNameWithServer,
                result -> mBinding.addContactText.setError(TextFieldHints.getNameHint(nickNameText)));
    }

    /**
     * Attempts to validate the Nickname and Jwt that is provided.
     *
     * If the validation succeeds, attempts send
     * Else, sets an error text on the nickname field that requests they enter a valid nickname.
     */
    private void verifyNameWithServer() {

        mContactsModel.requestConnect(
                mBinding.addContactText.getText().toString(), mUserInfo.getJwt());
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, minimize keyboard and send a toast notification.
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
                        mBinding.addContactText.setError("Nickname does not exist");
                    } else if (message.equals("Can not create contact with oneself")) {
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
            } else {
                    Toast.makeText(getContext(),"A contact request has been sent",
                            Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("Registration JSON Response", "No Response");
        }
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
                mBinding.listRoot.setAdapter(new ContactsRecyclerAdapter(
                        mContactsModel.getContactList(), this::showContactDeleteAlertDialog));
                mContactsModel.removeData();
                mBinding.addContactText.setError(null);
            }
        } else {
            // no response from the request
            Log.d("Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }

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