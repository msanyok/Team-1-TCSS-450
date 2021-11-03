/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentMessagesCardBinding;
import edu.uw.tcss450.group1project.ui.contacts.Contact;

/**
 * ContactRecyclerAdapter provides an adapter for the ContactsFragment RecyclerView.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.MessagesViewHolder> {

    /** The list of contacts to be displayed */
    private final List<ChatRoom> mChatRooms;

    /**
     * Creates a new ContactsRecyclerAdapter with a provided list of contacts
     *
     * @param theRooms the list of contacts
     */
    public MessagesRecyclerAdapter(List<ChatRoom> theRooms) {
        mChatRooms = theRooms;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                 final int theViewType) {
        return new MessagesViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_messages_card, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, final int position) {
        holder.setChatRoom(mChatRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }

    /**
     * ContactsViewHolder is a class defining an individual ViewHolder for the ContactsFragment
     * RecyclerView.
     *
     * @author Parker Rosengreen
     * @version Fall 2021
     */
    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        /** The assigned view */
        private final View mView;

        /** The ViewBinding corresponded to a contact RecyclerView card */
        private final FragmentMessagesCardBinding mBinding;

        /** The contact assigned to this ViewHolder */
        private ChatRoom mRoom;

        /**
         * Creates a new ContactsViewHolder with the provided view
         *
         * @param theItemView the view to be assigned
         */
        public MessagesViewHolder(@NonNull final View theItemView) {
            super(theItemView);
            mView = theItemView;
            mBinding = FragmentMessagesCardBinding.bind(theItemView);
        }

        /**
         * Assigns a contact to this view holder
         *
         * @param theRoom the contact to be assigned
         */
        public void setChatRoom(final ChatRoom theRoom) {
            mRoom = theRoom;
            display();
        }

        /** Displays all contact data and image views for a single contact card */
        private void display() {
            StringBuilder builder = new StringBuilder();
            List<Contact> participants = mRoom.getParticipants();
            for (int i = 0; i < participants.size(); i++) {
                if (participants.size() > 1 && i == participants.size() - 1) {
                    builder.append(String.format("and %s", participants.get(i).getNickname()));
                } else if (participants.size() > 2) {
                    builder.append(String.format("%s, ", participants.get(i).getNickname()));
                } else {
                    builder.append(participants.get(i).getNickname());
                    if (participants.size() == 2) builder.append(" ");
                }
            }
            mBinding.participantNames.setText(builder.toString());
            mBinding.currentMessage.setText(MessageGenerator.getRandomMessage());
            mBinding.arrowImage.setImageResource(R.drawable.ic_arrow_right__black_24dp);
            mBinding.participantImage.setImageResource(R.drawable.ic_messages_black_24dp);
        }
    }
}
