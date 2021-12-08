/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentChatRoomsBinding;
import edu.uw.tcss450.group1project.model.NewMessageCountViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.ui.contacts.Contact;

/**
 * A {@link Fragment} subclass that is responsible for the showing a user's available chats.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ChatsFragment extends Fragment {

    /** View Binding to the Chat Rooms Fragment */
    private FragmentChatRoomsBinding mBinding;

    /** ChatListViewModel that contains the state of the viewmodel */
    private ChatsListViewModel mChatListsModel;

    /** The text watcher which listens to user chat room searches */
    private TextWatcher mTextWatcher;

    /** The list of chat rooms */
    private List<ChatRoom> mChatRoomList;

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public ChatsFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        mChatListsModel = new ViewModelProvider(getActivity()).get(ChatsListViewModel.class);
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_chat_rooms, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mBinding = FragmentChatRoomsBinding.bind(getView());

        mChatListsModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        // populate the chat list when the fragment view is created
        UserInfoViewModel userInfo = new ViewModelProvider(this.getActivity())
                .get(UserInfoViewModel.class);
        mChatListsModel.getChatListData(userInfo.getJwt());

        mBinding.chatRoomStartButton.setOnClickListener(button -> {
            Navigation.findNavController(theView).
                    navigate(R.id.action_navigation_chats_to_createChatroomFragment);
        });

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
                final String firstChars = theEnteredText.toString().toLowerCase();
                if (mChatRoomList != null) {
                    List<ChatRoom> originalList = mChatRoomList;
                    final List<ChatRoom> newList =
                            originalList.stream().filter(room ->
                                    room.getChatName()
                                            .toLowerCase(Locale.ROOT).startsWith(firstChars)
                            ).collect(Collectors.toList());
                    mBinding.listRoot.setAdapter(new MessagesRecyclerAdapter(newList));
                }
            }
        };
        mBinding.roomSearchText.addTextChangedListener(mTextWatcher);
    }

    /**
     * Observe the chat list view model data, does the desired actions when that data changes.
     * @param theResponse the response object sent back from the http get request
     */
    private void observeResponse(final JSONObject theResponse) {

        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                // a 400 error occurred, so log it.
                Log.e("CHATS LIST 400", theResponse.toString());
                // TODO: Handle UI change when the chat list is not received properly,
                //  potentailly show Dialog?

            } else {
                // the data was retrieved properly, so get the formatted data from the view model
                // (will be up to date by the time this method is called from the observer)
                parseAndSetChatList(theResponse);

            }
        } else {
            // no response from the request
            Log.d("Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }

    /**
     * Parses the given JSONObject that contains the chat list raw data for this user.
     *
     * @param theResponse the raw data response from the server
     */
    private void parseAndSetChatList(final JSONObject theResponse) {

        // parse the response and set mChatRoomList
        mChatRoomList = new ArrayList<>();
        NewMessageCountViewModel newMessageModel =
                new ViewModelProvider(getActivity()).get(NewMessageCountViewModel.class);

        try {
            JSONArray chats = theResponse.getJSONArray("data");

            for (int i = 0; i < chats.length(); i++) {
                // the names of the get(...) fields are determined
                // by the server and can be found in the documentation
                JSONObject chat = (JSONObject) chats.get(i);
                mChatRoomList.add(new ChatRoom(chat.get("chat_name").toString(),
                        chat.get("chatid").toString(),
                        chat.get("message").toString(),
                        chat.get("timestamp").toString(),
                        newMessageModel.getNumNewMessages(
                                Integer.valueOf(chat.get("chatid").toString()))));
            }

            // once the list has been repopulated, sort the chat rooms based on the timestamp
            // of the most recent message sent
            Collections.sort(mChatRoomList);

        } catch (JSONException exception) {
            // should we do something specific here if the json isn't parsed properly/
            exception.printStackTrace();
        }
        mBinding.listRoot.setAdapter(new MessagesRecyclerAdapter(mChatRoomList));
        mTextWatcher.onTextChanged(mBinding.roomSearchText.getText().toString(),
                0, 0, 0);
    }

}
