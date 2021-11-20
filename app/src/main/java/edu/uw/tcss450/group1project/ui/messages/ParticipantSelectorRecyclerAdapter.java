/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import android.graphics.drawable.Icon;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentCreateChatRoomContactCardBinding;
import edu.uw.tcss450.group1project.ui.contacts.Contact;

/**
 * ParticipantSelectorRecyclerAdapter is a class for displaying possible participants
 * when starting a new chat room.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ParticipantSelectorRecyclerAdapter
        extends RecyclerView.Adapter<ParticipantSelectorRecyclerAdapter.ParticipantViewHolder> {

    /** The list of contacts to be displayed */
    private final List<Contact> mContactList;

    /** The participant view model storing participant selections */
    private final ChatRoomParticipantViewModel mModel;

    /**
     * Creates a new ParticipantSelectorRecyclerAdapter with a provided contact list and
     * participant view model
     *
     * @param theContacts the list of contacts
     * @param theModel the participant view model
     */
    public ParticipantSelectorRecyclerAdapter(final List<Contact> theContacts,
                                              final ChatRoomParticipantViewModel theModel) {
        mContactList = theContacts;
        mModel = theModel;
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                    final int theViewType) {
        return new ParticipantViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_create_chat_room_contact_card, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ParticipantViewHolder theHolder,
                                 final int thePosition) {
        theHolder.setContact(mContactList.get(thePosition));
    }

    @Override
    public int getItemCount() {
        Log.d("SIZE", String.valueOf(mContactList.size()));
        return mContactList.size();
    }

    /**
     * ParticipantViewHolder is a class for displaying a single contact and their selection
     * status.
     *
     * @author Parker Rosengreen
     * @version Fall 2021
     */
    public class ParticipantViewHolder extends RecyclerView.ViewHolder {

        /** The assigned view */
        private final View mView;

        /** The corresponding view binding */
        private final FragmentCreateChatRoomContactCardBinding mBinding;

        /** The assigned contact */
        private Contact mContact;

        /**
         * Creates a new ParticipantViewHolder with a provided item view
         *
         * @param theItemView the view to be assigned
         */
        public ParticipantViewHolder(@NonNull final View theItemView) {
            super(theItemView);
            mView = theItemView;
            mBinding = FragmentCreateChatRoomContactCardBinding.bind(theItemView);
            mBinding.toggleButton.setOnClickListener(button -> {
                Log.d("TAG", "FIRED");
                if (mModel.containsParticipant(mContact)) {
                    mModel.removeParticipant(mContact);
                } else {
                    mModel.addParticipant(mContact);
                }
                mBinding.selectionImage.setImageIcon(!mModel.containsParticipant(mContact) ?
                        Icon.createWithResource(mView.getContext(),
                                R.drawable.ic_plus_black_24dp) :
                        Icon.createWithResource(mView.getContext(),
                                R.drawable.ic_remove_black_24dp));
            });
        }

        /**
         * Sets this view holder's assigned contact
         *
         * @param theContact the contact to be assigned
         */
        public void setContact(final Contact theContact) {
            mContact = theContact;
            display();
        }

        /**
         * Displays all components on this view holder
         */
        private void display() {
            mBinding.contactName.setText(String.format("%s %s", mContact.getFirst(),
                    mContact.getLast()));
            mBinding.contactNickname.setText(mContact.getNickname());
            mBinding.selectionImage.setImageResource(
                    !mModel.containsParticipant(mContact) ? R.drawable.ic_plus_black_24dp :
                    R.drawable.ic_remove_black_24dp);
        }
    }
}
