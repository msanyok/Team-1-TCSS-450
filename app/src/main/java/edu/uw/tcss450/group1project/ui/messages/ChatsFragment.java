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
import androidx.navigation.Navigation;

import org.json.JSONObject;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentChatRoomsBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

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
        mChatListsModel.getChatListData(userInfo.getmJwt());

        mBinding.chatRoomStartButton.setOnClickListener(button -> {
            Navigation.findNavController(theView).
                    navigate(R.id.action_navigation_chats_to_createChatroomFragment);
        });
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
                // TODO: Handle UI change when the chat list is not received properly, potentailly show DialogBox?

            } else {
                // the data was retrieved properly, so get the formatted data from the view model
                // (will be up to date by the time this method is called from the observer)
                mBinding.listRoot.setAdapter(new MessagesRecyclerAdapter(mChatListsModel.getChatList()));

            }
        } else {
            // no response from the request
            Log.d("Chats List JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }
}
