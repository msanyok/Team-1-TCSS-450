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
import edu.uw.tcss450.group1project.databinding.FragmentContactsCardBinding;
import edu.uw.tcss450.group1project.ui.contacts.Contact;

/**
 * ChatRoomParticipantRecyclerAdapter provides an adapter for the ChatRoomInfoFragment
 * current members recycler view.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChatRoomParticipantRecyclerAdapter
        extends RecyclerView.Adapter<ChatRoomParticipantRecyclerAdapter.ParticipantViewHolder> {

    /** The list of contacts to be displayed */
    private final List<Contact> mContacts;

    /**
     * Creates a new ChatRoomParticipantRecyclerAdapter with a provided list of contacts
     *
     * @param theContacts the list of contacts
     */
    public ChatRoomParticipantRecyclerAdapter(final List<Contact> theContacts) {
        mContacts = theContacts;

    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                    final int theViewType) {

        return new ParticipantViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_contacts_card,
                        theParent, false));

    }
    @Override
    public void onBindViewHolder(@NonNull final ParticipantViewHolder theHolder, final int thePos) {
        theHolder.setContact(mContacts.get(thePos));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * ParticipantViewHolder is a class defining an individual ViewHolder for the
     * ChatRoomInfoFragment current member recycler view.
     *
     * @author Parker Rosengreen
     * @version Fall 2021
     */
    public class ParticipantViewHolder extends RecyclerView.ViewHolder {

        /** The ViewBinding corresponded to a current member recycler view card */
        private final FragmentContactsCardBinding mBinding;

        /** The contact assigned to this ViewHolder */
        private Contact mContact;

        /**
         * Creates a new ParticipantViewHolder with the provided view
         *
         * @param theItemView the view to be assigned
         */
        public ParticipantViewHolder(@NonNull final View theItemView) {
            super(theItemView);
            mBinding = FragmentContactsCardBinding.bind(theItemView);
        }

        /**
         * Assigns a contact to this view holder
         *
         * @param theContact the contact to be assigned
         */
        public void setContact(final Contact theContact) {
            mContact = theContact;
            display();
        }

        /**
         * Displays all contact data and image views for a single current member card
         */
        private void display() {
            mBinding.contactName.setText(String.format("%s %s", mContact.getFirst(),
                    mContact.getLast()));
            mBinding.contactNickname.setText(mContact.getNickname());
            mBinding.buttonDelete.setVisibility(View.GONE);
        }
    }
}