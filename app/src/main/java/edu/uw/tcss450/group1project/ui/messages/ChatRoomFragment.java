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
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentChatroomBinding;
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

        //When the user scrolls to the top of the RV, the swiper list will "refresh"
        //The user is out of messages, go out to the service and get more
        binding.swipeContainer.setOnRefreshListener(() -> {
            mChatModel.getNextMessages(mChatId, mUserModel.getJwt());
        });

        mChatModel.addMessageObserver(mChatId, getViewLifecycleOwner(),
                list -> {
                    /* TODO
                     * This solution needs work on the scroll position. As a group,
                     * you will need to come up with some solution to manage the
                     * recyclerview scroll position. You also should consider a
                     * solution for when the keyboard is on the screen.
                     */
                    //inform the RV that the underlying list has (possibly) changed
                    messagesRecyclerView.getAdapter().notifyDataSetChanged();
                    messagesRecyclerView.scrollToPosition(
                            messagesRecyclerView.getAdapter().getItemCount() - 1);
                    binding.swipeContainer.setRefreshing(false);
                });

        //Send button was clicked. Send the message via the SendViewModel
        binding.buttonSend.setOnClickListener(button -> {
            mSendModel.sendMessage(mChatId,
                    mUserModel.getJwt(),
                    binding.editMessage.getText().toString());
        });

        // when we get the response back from the server, clear the edit text
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->
                binding.editMessage.setText(""));
    }

}