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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentChatroomBinding;
import edu.uw.tcss450.group1project.model.LocalStorageUtils;
import edu.uw.tcss450.group1project.model.NewMessageCountViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * The Fragment that stores the chat threads for a particular chat
 *
 * @author Chris Ding
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ChatRoomFragment extends Fragment {

    /** The View Model used to send messages */
    private ChatSendViewModel mSendModel;

    /** The View Model used to store messages */
    private ChatViewModel mChatModel;

    /** The View Model that stores information about the user */
    private UserInfoViewModel mUserModel;

    /** The unique ChatId for this particular chat */
    private int mChatId;

    /** The position the recycler view should be in */
    private int mScrollPosition;

    /**
     * The number of messages that are in the recycler view before a refresh, sent,
     *  or received message.
     */
    private int mPreviousMessageCount;

    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);

        // set the chat ID for this fragment. This determines which chat to show!
        final ChatRoomFragmentArgs args =
                ChatRoomFragmentArgs.fromBundle(getArguments());
        mChatId = Integer.valueOf(args.getChatRoomId());

        // set up the view models for this fragment
        final ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(ChatViewModel.class);

        // update the new message counts now that we have navigated to a chat room.
        // if this chat room had new messages, the view model will remove the counts.
        provider.get(NewMessageCountViewModel.class).clearNewMessages(mChatId);

        // remove the locally stored new message counts if they exist for this chat id
        LocalStorageUtils.removeNewMessageStore(this.getContext(), mChatId);

        // get the most recent messages for this chat
        mChatModel.getFirstMessages(mChatId, mUserModel.getJwt());

        mSendModel = provider.get(ChatSendViewModel.class);
    }


    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_chatroom, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);

        FragmentChatroomBinding binding = FragmentChatroomBinding.bind(getView());

        // SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);

        final RecyclerView messagesRecyclerView = binding.recyclerMessages;

        // Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel
        // holds.
        messagesRecyclerView.setAdapter(new ChatRecyclerViewAdapter(
                mChatModel.getMessageListByChatId(mChatId),
                mUserModel.getEmail()));

        // moves the bottom of the chat recycler view up and down when the keyboard is open/closed
        // defaults to the bottom of the recycler view
        messagesRecyclerView.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft,
                                                        oldTop, oldRight, oldBottom) -> {
            messagesRecyclerView.scrollToPosition(messagesRecyclerView.getAdapter().getItemCount() - 1);
        });

        // When the user scrolls to the top of the RV, the swiper list will "refresh"
        // The user is out of messages, go out to the service and get more
        binding.swipeContainer.setOnRefreshListener(() -> {
            // set the scroll position so we can maintain our location
            // with the loaded old messages at the top of the screen.
            mScrollPosition = 25;

            mPreviousMessageCount = messagesRecyclerView.getAdapter().getItemCount();
            mChatModel.getNextMessages(mChatId, mUserModel.getJwt());
        });

        // fires when a new message attempts to be added. Can occur when a message is sent,
        // received, or the swipe container is invoked.
        mChatModel.addMessageObserver(mChatId, getViewLifecycleOwner(),
                list -> {
                    // we only update the position if the message count changed. We check this
                    // because we don't want to move anything if we are at the top of
                    // the message history
                    int newScrollPosition;
                    if (mPreviousMessageCount != messagesRecyclerView.getAdapter().getItemCount()) {
                        // there are new messages, either from a sent, recieved, or
                        newScrollPosition = mScrollPosition != 0 ? mScrollPosition :
                                messagesRecyclerView.getAdapter().getItemCount() - 1;
                    } else {
                        newScrollPosition = 0;
                    }


                    messagesRecyclerView.getAdapter().notifyDataSetChanged();
                    messagesRecyclerView.scrollToPosition(newScrollPosition);
                    binding.swipeContainer.setRefreshing(false);

                    // set the scroll position to 0 to make sure new sent/recieved messages
                    // make the view go to the bottom
                    mScrollPosition = 0;

                });

        //Send button was clicked. Send the message via the SendViewModel
        binding.buttonSend.setOnClickListener(button -> {
            mSendModel.sendMessage(mChatId,
                    mUserModel.getJwt(),
                    binding.editMessage.getText().toString());

            // set the scroll position to 0 so the recycler goes to the bottom

        });

        // when we get the response back from the server, clear the edit text
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->
                binding.editMessage.setText(""));
    }

}