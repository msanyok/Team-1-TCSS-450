/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentChatroomBinding;
import edu.uw.tcss450.group1project.model.IsTypingViewModel;
import edu.uw.tcss450.group1project.model.LocalStorageUtils;
import edu.uw.tcss450.group1project.model.NewMessageCountViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;

/**
 * The Fragment that stores the chat threads for a particular chat
 *
 * @author Chris Ding
 * @author Austn Attaway
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChatRoomFragment extends Fragment {

    /** The View Model used to send messages */
    private ChatSendViewModel mSendModel;

    /** The View Model used to store messages */
    private ChatViewModel mChatModel;

    /** The View Model that stores information about the user */
    private UserInfoViewModel mUserModel;

    /** The View Model that handles iw typing  */
    private IsTypingViewModel mTypingModel;

    /** The unique ChatId for this particular chat */
    private int mChatId;

    /** The chat room's name */
    private String mRoomName;

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
        mRoomName = args.getChatRoomName();
        mChatId = Integer.valueOf(args.getChatRoomId());

        // set up the view models for this fragment
        final ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(ChatViewModel.class);
        mTypingModel = provider.get(IsTypingViewModel.class);

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
        ChatRecyclerViewAdapter adapter = new ChatRecyclerViewAdapter(
                mChatModel.getMessageListByChatId(mChatId),
                mUserModel.getNickname());
        messagesRecyclerView.setAdapter(adapter);

        // moves the bottom of the chat recycler view up and down when the keyboard is open/closed
        // defaults to the bottom of the recycler view
        messagesRecyclerView.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft,
                                                        oldTop, oldRight, oldBottom) -> {
            messagesRecyclerView.scrollToPosition(
                    messagesRecyclerView.getAdapter().getItemCount() - 1);
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
                    adapter.constructCornerMapping();
                    messagesRecyclerView.getAdapter().notifyDataSetChanged();
                    messagesRecyclerView.scrollToPosition(newScrollPosition);
                    binding.swipeContainer.setRefreshing(false);

                    // set the scroll position to 0 to make sure new sent/received messages
                    // make the view go to the bottom
                    mScrollPosition = 0;

                    // We allow both view model and local message stores of new chats
                    // while the app is running. We do so because we want to make
                    // sure the notification data stays if the user exits the app or get a
                    // notification from a different chat.
                    // As a result, when new messages come in for this particular chat
                    // we must make sure to delete these.
                    LocalStorageUtils.removeNewMessageStore(this.getContext(), mChatId);
                    new ViewModelProvider(this.getActivity()).
                            get(NewMessageCountViewModel.class).clearNewMessages(mChatId);
                });

        //Send button was clicked. Send the message via the SendViewModel
        binding.buttonSend.setOnClickListener(button -> {
            // tell the server that this user is done typing
            mTypingModel.sendTypingNotification(mChatId, mUserModel.getJwt(), false);

            mSendModel.sendMessage(mChatId,
                    mUserModel.getJwt(),
                    binding.editMessage.getText().toString());
        });

        binding.buttonInfo.setOnClickListener(button -> {
            ChatRoomFragmentDirections.ActionNavigationChatRoomToChatRoomSettingsFragment action =
                    ChatRoomFragmentDirections
                            .actionNavigationChatRoomToChatRoomSettingsFragment(
                                    mRoomName, String.valueOf(mChatId));
            Navigation.findNavController(theView).navigate(action);
        });

        // when we get the response back from the server, clear the edit text
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->
                binding.editMessage.setText(""));

        // add a typing listener that sends notification that the user is typing
        binding.editMessage.addTextChangedListener(new TextWatcher() {

            /** The amount of time required between sending isTyping notifications */
            private static final int TYPING_TIMER_TIMEOUT = 5000; // 5 seconds

            /** The timer that handles setting the canType field to true once the time runs out */
            private Timer mTypingTimer = new Timer();

            /** Whether or not we can send a typing notification when the user types */
            private boolean mCanType = true;

            @Override
            public void beforeTextChanged(final CharSequence theCharSequence,
                                          final int theI,
                                          final int theI2,
                                          final int theCounter) {
                // unused
            }

            @Override
            public void onTextChanged(final CharSequence theCharSequence,
                                      final int theStart,
                                      final int theBefore,
                                      final int theCount) {
                // only send typing notifications if the timer allows us to and we
                // actually added a character (no notifs from deletes)
                if (mCanType && theCount != 0) {

                    // set canType to false, the timer will set
                    // it back to true once enough time passes
                    mCanType = false;
                    mTypingTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mCanType = true;
                        }
                    }, TYPING_TIMER_TIMEOUT);

                    // send the notification that this user is typing
                    mTypingModel.sendTypingNotification(mChatId, mUserModel.getJwt(), true);

                } else if (!mCanType && theCharSequence.length() == 0) {

                    // if the textfield is empty from deletions, notify that user is not typing
                    mTypingModel.sendTypingNotification(mChatId, mUserModel.getJwt(), false);

                    // we also want to be able to notify people if we start typing again
                    // immediately (rather than having to wait for the timer to run out)
                    // todo:
                    mTypingTimer = new Timer();
                    mCanType = true;
                }

            }

            @Override
            public void afterTextChanged(final Editable theEditable) {
                // unused
            }
        });


        // add an observer to the typing timers map, when the dataset changes,
        // we should update the "user is typing... text"
        mTypingModel.addTimersObserver(getViewLifecycleOwner(), responseMap -> {

            // ensure the map contains this chatId and there is at least one
            // nickname that is typing in this chat
            if (responseMap.containsKey(mChatId) && responseMap.get(mChatId).size() > 0) {
                final Map<String, Timer> nicknameMap = responseMap.get(mChatId);
                final TreeSet<String> nicknames = new TreeSet<>(nicknameMap.keySet());

                String notifText = "";
                if (nicknames.size() == 1) {
                    notifText = nicknames.pollFirst() + " is typing...";
                } else if (nicknames.size() == 2) {
                    notifText = nicknames.pollFirst() + " and " + nicknames.pollFirst() +
                           " are typing...";
                } else if (nicknames.size() == 3) {
                   notifText = nicknames.pollFirst() + ", " + nicknames.pollFirst() +
                           ", and 1 other are typing...";
                } else {
                    // number of people typing is > 3
                    notifText = nicknames.pollFirst() + ", " + nicknames.pollFirst() + ", and " +
                           (nicknames.size() - 2) + " others are typing...";
                }

                binding.userTypingText.setText(notifText);
                binding.userTypingText.setVisibility(View.VISIBLE);

            } else {
                // no typers in the chat
                binding.userTypingText.setText("");
                binding.userTypingText.setVisibility(View.GONE);
            }

        });
    }

}