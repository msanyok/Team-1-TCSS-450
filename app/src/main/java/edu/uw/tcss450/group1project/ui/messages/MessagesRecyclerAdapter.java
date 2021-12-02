/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentChatRoomCardBinding;

// todo: this class to be refactoed to 'ChatRoomRecyclerAdapter'
/**
 * ContactRecyclerAdapter provides an adapter for the ContactsFragment RecyclerView.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class MessagesRecyclerAdapter
        extends RecyclerView.Adapter<MessagesRecyclerAdapter.MessagesViewHolder> {

    /** The list of contacts to be displayed */
    private final List<ChatRoom> mChatRooms;

    /**
     * Creates a new ContactsRecyclerAdapter with a provided list of contacts
     *
     * @param theRooms the list of contacts
     */
    public MessagesRecyclerAdapter(final List<ChatRoom> theRooms) {
        mChatRooms = theRooms;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                 final int theViewType) {
        return new MessagesViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_chat_room_card, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder theHolder,
                                 final int thePosition) {
        theHolder.setChatRoom(mChatRooms.get(thePosition));
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
        private final FragmentChatRoomCardBinding mBinding;

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
            mBinding = FragmentChatRoomCardBinding.bind(theItemView);

            // navigate to a chat room fragment when the chat room card is pressed.
            // send the chat name and the chat ID through action params, important state the
            // fragment needs.
            mBinding.chatroomNavigation.setOnClickListener(button -> {
                ChatsFragmentDirections.ActionNavigationChatsToChatroomFragment action =
                        ChatsFragmentDirections
                                .actionNavigationChatsToChatroomFragment(mRoom.getChatName(),
                                        mRoom.getChatID());
                Navigation.findNavController(theItemView).navigate(action);
            });

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
            mBinding.chatName.setText(mRoom.getChatName());
            mBinding.currentMessage.setText(mRoom.getMostRecentMessage());

            // set the text on the chat if there are new messages in the chat
            if (mRoom.getMissedMessageCount() > 0) {
                // TODO: STYLE ROOM CARD FOR NEW MESSAGE
                mBinding.participantImage.setImageAlpha(150);
            }

        }
    }
}
