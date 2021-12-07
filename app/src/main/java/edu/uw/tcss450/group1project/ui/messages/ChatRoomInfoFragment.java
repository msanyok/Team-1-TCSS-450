/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

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
import androidx.navigation.Navigation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import edu.uw.tcss450.group1project.MainActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentChatroomInfoBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.ui.contacts.Contact;
import edu.uw.tcss450.group1project.ui.contacts.ContactsViewModel;

/**
 * ChatRoomInfoFragment is a class for displaying information data for a specific chat room.
 * Functionality includes adding new members, viewing current members, and leaving.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChatRoomInfoFragment extends Fragment {

    /** The participant view model */
    private ChatRoomParticipantViewModel mParticipantModel;

    /** The contacts view model */
    private ContactsViewModel mContactsModel;

    /** The user info view model */
    private UserInfoViewModel mUserModel;

    /** The view binding */
    private FragmentChatroomInfoBinding mBinding;

    /** The list of participants that can be added to this chat room */
    private List<Contact> mParticipantOptions;

    /** The set of contacts that are being added to this chat room */
    private Set<Contact> mAdditions;

    /** The text watcher which listens to changes in user contact searches */
    private TextWatcher mTextWatcher;

    /** The id of the chat room for which this fragment is displaying info for */
    private int mChatId;

    /**
     * Required empty public constructor
     */
    public ChatRoomInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        final ChatRoomInfoFragmentArgs args =
                ChatRoomInfoFragmentArgs.fromBundle(getArguments());
        mChatId = Integer.valueOf(args.getChatRoomId());
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_chatroom_info, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        mBinding = FragmentChatroomInfoBinding.bind(theView);

        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);

        mParticipantModel = new ViewModelProvider(this).get(ChatRoomParticipantViewModel.class);
        mParticipantModel.addGetParticipantsResponseObserver(
                getViewLifecycleOwner(), this::observeCurrentParticipantResponse);
        mAdditions = mParticipantModel.getSelected();

        mContactsModel = new ViewModelProvider(getActivity()).get(ContactsViewModel.class);
        mContactsModel.contactsConnect(mUserModel.getJwt());
        mContactsModel.addContactListObserver(
                getViewLifecycleOwner(), this::observeContactsResponse);

        Spinner spinner = (Spinner) getView().findViewById(R.id.contact_search_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.contact_search_array, R.layout.fragment_contacts_spinner);
        adapter.setDropDownViewResource(R.layout.fragment_contacts_spinner_dropdown);
        spinner.setAdapter(adapter);
        mBinding.addButton.setOnClickListener(this::initializeParticipantAddition);
        mParticipantModel.addParticipantAdditionResponseObserver(
                getViewLifecycleOwner(), this::observeParticipantAdditionResponse);
        mParticipantModel.addLeaveRoomResponseObserver(
                getViewLifecycleOwner(), this::observeLeaveResponse);
        mBinding.leaveButton.setOnClickListener(button -> displayLeaveDialog());

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

                    mBinding.listNewParticipants.setAdapter(new ParticipantSelectorRecyclerAdapter(
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
        mBinding.contactSearchText.setError(null);
    }

    /**
     * Initializes a participant addition request to the server
     *
     * @param theButton the pressed "add" button
     */
    private void initializeParticipantAddition(final View theButton) {
        if (mAdditions.isEmpty()) {
            mBinding.contactSearchText.setError("Please select at least 1 participant.");
        } else {
            mParticipantModel.connectAddParticipants(
                    mUserModel.getJwt(), mUserModel.getNickname(), mChatId);
        }
    }

    /**
     * Displays a dialog to the user confirming that they would like to leave the chat room
     */
    private void displayLeaveDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>Are you sure " +
                "you want to leave the chat room?</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Leave</font>"),
                (dialog, which) -> {
                    mParticipantModel.connectLeaveRoom(mUserModel.getJwt(),
                            mChatId, mUserModel.getEmail(), mUserModel.getNickname());
                });
        alertDialog.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }

    /**
     * Observes responses from the server corresponding to leave requests
     *
     * @param theResponse the observed response
     */
    private void observeLeaveResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            Log.e("LEAVE ROOM ERROR", theResponse.toString());
            displayErrorDialog(
                    "An unexpected error occured when leaving the room. Please try again.");
            mParticipantModel.clearLeaveResponse();
        } else if (theResponse.length() != 0) {
            mParticipantModel.clearLeaveResponse();
            Toast.makeText(getContext(), "You have left the chat room.", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(getView()).navigate(
                    R.id.action_navigation_chat_room_info_to_navigation_messages);
        }
    }

    /**
     * Observes responses from the server corresponding to contact "gets"
     *
     * @param theResponse the observed response
     */
    private void observeContactsResponse(final JSONObject theResponse) {
        if (theResponse.has("code") || theResponse.has("error")) {
            Log.e("CONTACT LIST REQUEST ERROR", theResponse.toString());
            displayErrorDialog("An unexpected error occurred when loading current " +
                    "contacts. Please try again.");
            mContactsModel.removeData();
        }
        if (mContactsModel.containsReadableContacts()) {
            mParticipantModel.connectGetParticipants(mUserModel.getJwt(), mChatId);
        }
    }

    /**
     * Observes responses from the server corresponding to chat room participant "gets"
     *
     * @param theResponse the observed response
     */
    private void observeCurrentParticipantResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            Log.e("PARTICIPANT LIST REQUEST ERROR", theResponse.toString());
            displayErrorDialog("An unexpected error occurred when loading chat room " +
                    "participants. Please try again.");
            mParticipantModel.clearGetResponse();
        }
        if (mParticipantModel.containsReadableParticipants()) {
            setViewComponents();
        }
    }

    /**
     * Observes responses from the server corresponding to chat room participant additions
     *
     * @param theResponse the observed response
     */
    private void observeParticipantAdditionResponse(final JSONObject theResponse) {
        if (theResponse.has("code")) {
            Log.e("PARTICIPANT ADDITION ERROR TO EXISTING CHAT ROOM", theResponse.toString());
            displayErrorDialog("Unexpected error when adding members. Please try again.");
            mParticipantModel.clearAddResponse();
        } else if (theResponse.length() != 0) {
            mParticipantModel.clearAddResponse();
            Toast.makeText(getContext(), "Members added successfully.", Toast.LENGTH_SHORT).show();
            mContactsModel.contactsConnect(mUserModel.getJwt());
        }
    }

    /**
     * Displays an error dialog to the user with a supplied custom message
     *
     * @param theMessage the custom message
     */
    private void displayErrorDialog(final String theMessage) {
        ((MainActivity) getActivity()).displayErrorDialog(theMessage);
    }

    /**
     * Sets the view components of this fragment, filtering current contacts against
     * existing chat room members
     */
    private void setViewComponents() {
        List<Contact> allContacts = mContactsModel.getContactList();
        List<Contact> currentParticipants = mParticipantModel.getParticipants();
        allContacts.removeIf(currentParticipants::contains);
        mAdditions.removeIf(contact -> !allContacts.contains(contact));
        mParticipantOptions = allContacts;
        mBinding.listNewParticipants.setAdapter(
                new ParticipantSelectorRecyclerAdapter(allContacts, mAdditions));
        mBinding.listCurrentMembers.setAdapter(
                new ChatRoomParticipantRecyclerAdapter(currentParticipants));
        mTextWatcher.onTextChanged(mBinding.contactSearchText.getText().toString(),
                0, 0, 0);
    }
}