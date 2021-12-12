/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentChatRoomCardBinding;
import edu.uw.tcss450.group1project.ui.messages.ChatRoom;

/**
 * ChatRoomNotificationsRecyclerAdapter provides an adapter for the HomeFragment
 * new messages RecyclerView.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChatRoomNotificationsRecyclerAdapter extends
        RecyclerView.Adapter<ChatRoomNotificationsRecyclerAdapter.ChatRoomNotificationsViewHolder> {

    /** The list of relevant chat rooms to be displayed */
    private final List<ChatRoom> mChatRooms;

    /**
     * Creates a new ChatRoomNotificationsRecyclerAdapter with a provided list of chat rooms
     *
     * @param theRooms the list of chat rooms
     */
    public ChatRoomNotificationsRecyclerAdapter(final List<ChatRoom> theRooms) {
        mChatRooms = theRooms;
    }

    @NonNull
    @Override
    public ChatRoomNotificationsViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                              final int theViewType) {
        return new ChatRoomNotificationsViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_chat_room_card,
                         theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatRoomNotificationsViewHolder holder,
                                 final int position) {
        holder.setChatRoom(mChatRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }

    /**
     * ChatRoomNotificationsViewHolder is a class defining an individual ViewHolder for
     * HomeFragment new messages RecyclerView.
     *
     * @author Parker Rosengreen
     * @version Fall 2021
     */
    public class ChatRoomNotificationsViewHolder extends RecyclerView.ViewHolder {

        /** The assigned view */
        private final View mView;

        /** The ViewBinding corresponded to a new message RecyclerView card */
        private final FragmentChatRoomCardBinding mBinding;

        /** The chat room assigned to this ViewHolder */
        private ChatRoom mRoom;

        /**
         * Creates a new ChatRoomNotificationsViewHolder with the provided view
         *
         * @param theItemView the view to be assigned
         */
        public ChatRoomNotificationsViewHolder(@NonNull final View theItemView) {
            super(theItemView);
            mView = theItemView;
            mBinding = FragmentChatRoomCardBinding.bind(theItemView);

            // navigate to a chat room fragment when the chat room card is pressed.
            // send the chat name and the chat ID through action params, important state the
            // fragment needs.
            mBinding.chatroomNavigation.setOnClickListener(button -> {

                HomeFragmentDirections.ActionNavigationHomeChatsToNavigationChatRoom action =
                        HomeFragmentDirections
                                .actionNavigationHomeChatsToNavigationChatRoom(mRoom.getChatName(),
                                        mRoom.getChatID());
                Navigation.findNavController(theItemView).navigate(action);
            });
        }

        /**
         * Assigns a chat room to this view holder
         *
         * @param theRoom the contact to be assigned
         */
        public void setChatRoom(final ChatRoom theRoom) {
            mRoom = theRoom;
            display();
        }

        /** Displays all chat room data and image views for a single new message card */
        private void display() {
            mBinding.chatName.setText(mRoom.getChatName());
            mBinding.currentMessage.setText(mRoom.getMostRecentMessage());
            mBinding.participantImage.setImageResource(R.drawable.ic_messages_black_24dp);
        }
    }
}