/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentMessagesBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * A {@link Fragment} subclass that is responsible for the chats page.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ChatsFragment extends Fragment {

    private FragmentMessagesBinding mBinding;

    private ChatsListViewModel mChatListsModel;

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
        return theInflater.inflate(R.layout.fragment_messages, theContainer, false);

    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);

        mBinding = FragmentMessagesBinding.bind(getView());
//        mBinding.listRoot.setAdapter(new MessagesRecyclerAdapter(ChatRoomGenerator.getChatRooms()));

        mChatListsModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        // populate the chat list when the fragment is shown
        UserInfoViewModel userInfo = new ViewModelProvider(this.getActivity())
                .get(UserInfoViewModel.class);
        mChatListsModel.getChatListData(userInfo.getJwt());

    }

    /**
     * Observse the chat list view model data, does the desired actions when that data changes.
     * @param theResponse the response object sent back from the http get request
     */
    private void observeResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                // there is a 400 error, so check it and respond accordingly
                Log.e("CHATS LIST 400", theResponse.toString());
            } else {
                // the data was retrieved properly, so parse it
                Log.e("PARSE CHAT LISTT", theResponse.toString());
                parseChatListData(theResponse);
            }
        } else {
            // no response from the request
            Log.d("Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }

    private void parseChatListData(JSONObject theResponse) {
        // here we need to parse the response data and put it into the
        // chat list recycler view on the UI
        List<ChatRoom> formattedChatList = new ArrayList<>();

        try {
            JSONArray chats = theResponse.getJSONArray("data");
            for (int i = 0; i < chats.length(); i++) {
                JSONObject chat = (JSONObject) chats.get(i);
                formattedChatList.add(new ChatRoom(chat.get("name").toString(),
                        chat.get("chatId").toString(),
                        "Most recent message"));
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
        }

        mBinding.listRoot.setAdapter(
                new MessagesRecyclerAdapter(formattedChatList));
    }
}
