/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentContactRequestsBinding;
import edu.uw.tcss450.group1project.model.ContactNotificationViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * ContactRequestsFragment is a class for displaying incoming and outgoing contact requests.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ContactRequestsFragment extends Fragment {

    /** Contact Requests view Model*/
    private ContactRequestViewModel mRequestModel;

    /** User View Model for Jwt*/
    private UserInfoViewModel mUserModel;

    /** The view binding */
    private FragmentContactRequestsBinding mBinding;

    /**
     * Required empty constructor
     */
    public ContactRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mRequestModel = new ViewModelProvider(getActivity()).get(ContactRequestViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_contact_requests, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mBinding = FragmentContactRequestsBinding.bind(getView());
        mRequestModel.addRequestResponseObserver(getViewLifecycleOwner(),
                this::observeRequestResponse);
        mRequestModel.allContactRequests(mUserModel.getJwt());
    }

    @Override
    public void onResume() {
        super.onResume();
        // update the contact request list so if we got a request while onPause,
        // the list will update
        mRequestModel.allContactRequests(mUserModel.getJwt());

        // remove the notifications from this tab if there are any
        new ViewModelProvider(this.getActivity()).get(ContactNotificationViewModel.class).
                removeTabNotifications(ContactsParentFragment.REQUESTS);
    }

    /**
     * Sets the adapter and added the contacts requests to the contact request fragment
     */
    private void setContactListComponents() {
        mBinding.listContactRequests.setAdapter(new ContactRequestRecyclerAdapter(
                mRequestModel.getContactList(), mRequestModel, mUserModel));
        mBinding.listOutgoingContactRequests.setAdapter(
                new OutgoingRequestRecyclerAdapter(
                        mRequestModel.getOutgoingRequestList(), this::deleteSentRequest));
    }

    /**
     * Function of warning for deleting a contact using alert dialog
     *
     * @param theContact the contact request to be deleted.
     */
    private void deleteSentRequest(final Contact theContact) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>Are you sure you want "+
                "to delete this contact?</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Delete</font>"),
                (dialog, which) -> {
                    mRequestModel.sendDeleteResponse(mUserModel.getJwt(), theContact.getMemberId());

                });
        alertDialog.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, set the contact request list.
     *
     * @param theResponse response from the server
     */
    private void observeRequestResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                // a 400 error occurred, so log it.
                Log.e("REQUEST ERROR", theResponse.toString());
            } else if (theResponse.length() != 0) {
                setContactListComponents();

            }
        } else {
            // no response from the request
            Log.d("Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }
}