/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import edu.uw.tcss450.group1project.MainActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentCreateChatroomBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.ui.contacts.Contact;
import edu.uw.tcss450.group1project.ui.contacts.ContactsViewModel;
import edu.uw.tcss450.group1project.utils.TextFieldHints;
import edu.uw.tcss450.group1project.utils.TextFieldValidators;

/**
 * CreateChatroomFragment provides a new chatroom after clicking the create button.
 *
 * @author Parker Rosengreen
 * @author Chris Ding
 * @version Fall 2021
 */
public class CreateChatRoomFragment extends Fragment {

    /** The chat room participant view model */
    private ChatRoomParticipantViewModel mParticipantsModel;

    /** The contacts view model */
    private ContactsViewModel mContactsModel;

    /** The user info view model */
    private UserInfoViewModel mUserModel;

    /** The set of added participants */
    private Set<Contact> mParticipants;

    /** The view binding */
    private FragmentCreateChatroomBinding mBinding;

    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mParticipants = new HashSet<>();
        mParticipantsModel =
                new ViewModelProvider(this).get(ChatRoomParticipantViewModel.class);
        mContactsModel = new ViewModelProvider(getActivity()).get(ContactsViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        mContactsModel.contactsConnect(mUserModel.getJwt());
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_create_chatroom, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mBinding = FragmentCreateChatroomBinding.bind(getView());
        mContactsModel.addContactListObserver(
                getViewLifecycleOwner(), this::observeContactResponse);
        mParticipantsModel.addChatRoomCreationResponseObserver(
                getViewLifecycleOwner(), this::observeCreationResponse);
        mBinding.createButton.setOnClickListener(this::processCreateRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.chatRoomNameText.setError(null);
    }

    /**
     * Starts processing a request to create a chat room, beginning with chat room name
     * checking
     *
     * @param theButton the create button which was clicked
     */
    private void processCreateRequest(final View theButton) {
        //hides keyboard
        InputMethodManager imm = (InputMethodManager)getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        }
        validateChatRoomName();
    }

    /**
     * Validates an entered chat room name
     */
    private void validateChatRoomName() {
        final String nameText = mBinding.chatRoomNameText.getText().toString().trim();
        TextFieldValidators.NAME_VALIDATOR.processResult(
                TextFieldValidators.NAME_VALIDATOR.apply(nameText),
                () -> initiateChatRoomCreation(nameText),
                result -> mBinding.chatRoomNameText.setError(TextFieldHints.getNameHint(nameText)));
    }

    /**
     * Sends a request to the server to create a new chat room
     *
     * @param theRoomName the name of the chat room to be created
     */
    private void initiateChatRoomCreation(final String theRoomName) {
        mParticipantsModel.createChatRoom(mUserModel.getJwt(), mUserModel.getEmail(),
                theRoomName, mParticipants);
    }

    /**
     * Executed when observers of the JSONObject contact list response are fired
     *
     * @param theResponse the observed JSONObject response
     */
    private void observeContactResponse(final JSONObject theResponse) {
        if (theResponse.has("code") || theResponse.has("error")) {
            displayContactLoadErrorDialog();
        }
        if (mContactsModel.containsReadableContacts()) {
            displayParticipantOptions();
        }
    }

    /**
     * Executed when observers of the JSONObject chat room creation response are fired
     *
     * @param theResponse the observed JSONObject response
     */
    private void observeCreationResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            displayChatRoomCreationErrorDialog();
        } else if (theResponse.length() != 0) {
            try {
                int roomId = theResponse.getInt("chatID");
                String roomName = theResponse.getString("chatName");
                CreateChatRoomFragmentDirections
                        .ActionNavigationCreateChatRoomToNavigationChatRoom action =
                        CreateChatRoomFragmentDirections
                                .actionNavigationCreateChatRoomToNavigationChatRoom(roomName,
                                        String.valueOf(roomId));
                Navigation.findNavController(getView()).navigate(action);
            } catch (JSONException ex) {
                displayChatRoomCreationErrorDialog();
                ex.printStackTrace();
            }
        }
    }

    /**
     * Displays contacts/optional participants to be added to a new chat room
     */
    private void displayParticipantOptions() {
        mBinding.listRoot.setAdapter(
                new ParticipantSelectorRecyclerAdapter(
                        mContactsModel.getContactList(), mParticipants));
    }

    /**
     * Displays an error dialog to the user when contact loading is unsuccessful
     */
    private void displayContactLoadErrorDialog() {
        String message = "Unexpected error when loading contacts. Please try again.";
        ((MainActivity) getActivity()).displayErrorDialog(message);
    }

    /**
     * Displays an error dialog to the user when chat room creation is unsuccessful
     */
    private void displayChatRoomCreationErrorDialog() {
        String message = "Unexpected error when creating chat room. Please try again.";
        ((MainActivity) getActivity()).displayErrorDialog(message);
    }
}