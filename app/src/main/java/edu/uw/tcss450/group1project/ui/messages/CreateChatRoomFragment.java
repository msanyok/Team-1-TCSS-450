/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import edu.uw.tcss450.group1project.MainActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentCreateChatroomBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.ui.contacts.Contact;
import edu.uw.tcss450.group1project.ui.contacts.ContactsViewModel;

/**
 * CreateChatroomFragment provides a new chatroom after clicking the create button.
 *
 * @author Parker Rosengreen
 * @author Chris Ding
 * @version Fall 2021
 */
public class CreateChatRoomFragment extends Fragment {

    /** The chat room participant view model */
    private ChatRoomCreationViewModel mCreationModel;

    /** The contacts view model */
    private ContactsViewModel mContactsModel;

    /** The user info view model */
    private UserInfoViewModel mUserModel;

    /** The view binding */
    private FragmentCreateChatroomBinding mBinding;

    private List<Contact> mParticipantOptions;

    /** The set of added participants */
    private Set<Contact> mAdditions;

    /** The text watcher which listens to changes in user input in the contact search bar */
    private TextWatcher mTextWatcher;

    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mCreationModel =
                new ViewModelProvider(this).get(ChatRoomCreationViewModel.class);
        mContactsModel = new ViewModelProvider(getActivity()).get(ContactsViewModel.class);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        mAdditions = mCreationModel.getSelected();
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
        mCreationModel.addChatRoomCreationResponseObserver(
                getViewLifecycleOwner(), this::observeCreationResponse);
        mBinding.createButton.setOnClickListener(this::processCreateRequest);
        Spinner spinner = (Spinner) getView().findViewById(R.id.contact_search_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.contact_search_array, R.layout.fragment_contacts_spinner);
        adapter.setDropDownViewResource(R.layout.fragment_contacts_spinner_dropdown);
        spinner.setAdapter(adapter);

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

                // we only want to filter contacts if it is based the nickname,
                // first name, or last name
                if (mParticipantOptions != null) {
                    List<Contact> originalList = mParticipantOptions;
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

                    mBinding.listRoot.setAdapter(new ParticipantSelectorRecyclerAdapter(
                            newList, mAdditions));
                }
            }
        };

        // add the change listener that will filter contacts
        // when the state of the text field changes
        mBinding.contactSearchText.addTextChangedListener(mTextWatcher);

        // add actions that occur when the user changes the type of filtering
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> theParentView,
                                       final View theSelectedItemView,
                                       final int thePosition,
                                       final long theId) {
                // the user just changed the type of identifier the user wants to use
                // to filter contacts, so tell the text listener to refilter.
                mTextWatcher.onTextChanged(mBinding.contactSearchText.getText().toString(),
                        0, 0, 0);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> theParentView) {
                // unused
            }
        });
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
        boolean valid = !nameText.isEmpty();
        if (valid) {
            for (char c : nameText.toCharArray()) {
                valid = (Character.isLetter(c) || Character.isDigit(c) || c == ' ') && valid;
            }
            if (valid) {
                initiateChatRoomCreation(nameText);
            } else {
                mBinding.chatRoomNameText.setError(
                        "Room names must only contain letters or digits!");
            }
        } else {
            mBinding.chatRoomNameText.setError("Room names must be at least one character!");
        }
    }

    /**
     * Sends a request to the server to create a new chat room
     *
     * @param theRoomName the name of the chat room to be created
     */
    private void initiateChatRoomCreation(final String theRoomName) {
        mCreationModel.createChatRoom(mUserModel.getJwt(), mUserModel.getNickname(), theRoomName);
    }

    /**
     * Executed when observers of the JSONObject contact list response are fired
     *
     * @param theResponse the observed JSONObject response
     */
    private void observeContactResponse(final JSONObject theResponse) {
        if (theResponse.has("code") || theResponse.has("error")) {
            Log.e("CONTACT LIST REQUEST ERROR", theResponse.toString());
            displayErrorDialog("Unexpected error when loading contacts. Please try again.");
            mContactsModel.removeData();
        }
        if (mContactsModel.containsReadableContacts()) {
            mParticipantOptions = mContactsModel.getContactList();
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
            Log.e("CHAT ROOM CREATION REQUEST ERROR", theResponse.toString());
            displayErrorDialog("Unexpected error when creating chat room. Please try again.");
            mCreationModel.clearCreationResponse();
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
                mCreationModel.clearCreationResponse();
                displayErrorDialog(
                        "Unexpected error when creating chat room. Please try again.");
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
                        mParticipantOptions, mAdditions));
        mTextWatcher.onTextChanged(mBinding.contactSearchText.getText().toString(),
                0, 0, 0);
    }

    /**
     * Displays an error dialog to the user when a specific error occurs
     *
     * @param theMessage the custom message
     */
    private void displayErrorDialog(final String theMessage) {
        ((MainActivity) getActivity()).displayErrorDialog(theMessage);
    }
}