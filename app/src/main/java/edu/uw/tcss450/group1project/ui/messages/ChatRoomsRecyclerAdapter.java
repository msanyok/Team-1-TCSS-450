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

/**
 * ChatRoomsRecyclerAdapter provides an adapter for the ChatsFragment RecyclerView.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChatRoomsRecyclerAdapter
        extends RecyclerView.Adapter<ChatRoomsRecyclerAdapter.ChatRoomViewHolder> {

    /** The list of contacts to be displayed */
    private final List<ChatRoom> mChatRooms;

    /**
     * Creates a new ChatRoomsRecyclerAdapter with a provided list of contacts
     *
     * @param theRooms the list of contacts
     */
    public ChatRoomsRecyclerAdapter(final List<ChatRoom> theRooms) {
        mChatRooms = theRooms;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                 final int theViewType) {
        return new ChatRoomViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_chat_room_card, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatRoomViewHolder theHolder,
                                 final int thePosition) {
        theHolder.setChatRoom(mChatRooms.get(thePosition));
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }

    /**
     * ChatRoomViewHolder is a class defining an individual ViewHolder for the ChatsFragment
     * RecyclerView.
     *
     * @author Parker Rosengreen
     * @version Fall 2021
     */
    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {

        /** The ViewBinding corresponded to a chat room RecyclerView card */
        private final FragmentChatRoomCardBinding mBinding;

        /** The chat room assigned to this ViewHolder */
        private ChatRoom mRoom;

        /**
         * Creates a new ChatRoomViewHolder with the provided view
         *
         * @param theItemView the view to be assigned
         */
        public ChatRoomViewHolder(@NonNull final View theItemView) {
            super(theItemView);
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
         * Assigns a chat room to this view holder
         *
         * @param theRoom the contact to be assigned
         */
        public void setChatRoom(final ChatRoom theRoom) {
            mRoom = theRoom;
            display();
        }

        /** Displays all chat room data and image views for a single chat room card */
        private void display() {
            mBinding.chatName.setText(mRoom.getChatName());
            mBinding.currentMessage.setText(mRoom.getMostRecentMessage());

            // set the text on the chat if there are new messages in the chat
            if (mRoom.getMissedMessageCount() > 0) {
                mBinding.participantImage.setImageResource(R.drawable.ic_messages_black_24dp);
            }
        }
    }
}