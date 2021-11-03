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
import edu.uw.tcss450.group1project.databinding.FragmentHomeContactRequestCardBinding;
import edu.uw.tcss450.group1project.ui.contacts.Contact;

/**
 * ContactRequestRecyclerAdapter provides an adapter for the HomeFragment contact request
 * RecyclerView.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ContactRequestRecyclerAdapter
        extends RecyclerView.Adapter<ContactRequestRecyclerAdapter.ContactRequestViewHolder> {

    /** The list of potential contacts to be displayed */
    private final List<Contact> mContacts;

    /**
     * Creates a new ContactRequestRecyclerAdapter with a provided list of potential contacts
     *
     * @param theContacts the list of potential contacts
     */
    public ContactRequestRecyclerAdapter(final List<Contact> theContacts) {
        mContacts = theContacts;
    }

    @NonNull
    @Override
    public ContactRequestViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                       final int theViewType) {
        return new ContactRequestViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_home_contact_request_card, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactRequestViewHolder theHolder,
                                 final int thePosition) {
        theHolder.setContact(mContacts.get(thePosition));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * ContactRequestViewHolder is a class defining an individual ViewHolder for the
     * HomeFragment contact request RecyclerView.
     *
     * @author Parker Rosengreen
     * @version Fall 2021
     */
    public class ContactRequestViewHolder extends RecyclerView.ViewHolder {

        /** The assigned view */
        private final View mView;

        /** The ViewBinding corresponded to a contact request RecyclerView card */
        private final FragmentHomeContactRequestCardBinding mBinding;

        /** The potential contact assigned to this ViewHolder */
        private Contact mContact;

        /**
         * Creates a new ContactRequestViewHolder with the provided view
         *
         * @param theItemView the view to be assigned
         */
        public ContactRequestViewHolder(@NonNull final View theItemView) {
            super(theItemView);
            mView = theItemView;
            mBinding = FragmentHomeContactRequestCardBinding.bind(theItemView);
        }

        /**
         * Assigns a potential contact to this view holder
         *
         * @param theContact the contact to be assigned
         */
        public void setContact(final Contact theContact) {
            mContact = theContact;
            display();
        }

        /** Displays all potential contact data and image views for a single contact card */
        private void display() {
            mBinding.contactName.setText(String.format("%s %s", mContact.getFirst(),
                    mContact.getLast()));
            mBinding.contactNickname.setText(mContact.getNickname());
            mBinding.contactImage.setImageResource(R.drawable.ic__android__black_24dp);
            mBinding.denyButton.setImageResource(R.drawable.ic_deny_red_24dp);
            mBinding.acceptButton.setImageResource(R.drawable.ic_check_green_24dp);
        }
    }
}
