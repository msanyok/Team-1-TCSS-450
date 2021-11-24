/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentHomeMessageNotificationCardBinding;
import edu.uw.tcss450.group1project.ui.messages.ChatRoom;

/**
 * MessagesNotificationsRecyclerAdapter provides an adapter for the HomeFragment
 * new messages RecyclerView.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class MessagesNotificationsRecyclerAdapter extends
        RecyclerView.Adapter<MessagesNotificationsRecyclerAdapter.MessagesNotificationsViewHolder> {

    /** The list of relevant chat rooms to be displayed */
    private final List<ChatRoom> mChatRooms;

    /**
     * Creates a new MessagesNotificationsRecyclerAdapter with a provided list of chat rooms
     *
     * @param theRooms the list of chat rooms
     */
    public MessagesNotificationsRecyclerAdapter(final List<ChatRoom> theRooms) {
        mChatRooms = theRooms;
    }

    @NonNull
    @Override
    public MessagesNotificationsViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                              final int theViewType) {
        return new MessagesNotificationsViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_home_message_notification_card,
                         theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesNotificationsViewHolder holder,
                                 final int position) {
        holder.setChatRoom(mChatRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }

    /**
     * MessagesNotificationsViewHolder is a class defining an individual ViewHolder for
     * HomeFragment new messages RecyclerView.
     *
     * @author Parker Rosengreen
     * @version Fall 2021
     */
    public class MessagesNotificationsViewHolder extends RecyclerView.ViewHolder {

        /** The assigned view */
        private final View mView;

        /** The ViewBinding corresponded to a new message RecyclerView card */
        private final FragmentHomeMessageNotificationCardBinding mBinding;

        /** The chat room assigned to this ViewHolder */
        private ChatRoom mRoom;

        /**
         * Creates a new MessagesNotificationsViewHolder with the provided view
         *
         * @param theItemView the view to be assigned
         */
        public MessagesNotificationsViewHolder(@NonNull final View theItemView) {
            super(theItemView);
            mView = theItemView;
            mBinding = FragmentHomeMessageNotificationCardBinding.bind(theItemView);
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
            // TODO implement code for live chat room data
        }
    }
}
