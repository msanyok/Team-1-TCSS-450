/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentContactsCardBinding;

/**
 * ContactRecyclerAdapter provides an adapter for the ContactsFragment RecyclerView.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ContactsRecyclerAdapter
        extends RecyclerView.Adapter<ContactsRecyclerAdapter.ContactsViewHolder> {

    /** The list of contacts to be displayed */
    private final List<Contact> mContacts;

    /** The Consumer responsible for contact deletion*/
    private final Consumer<Contact> mConsumer;

    /**
     * Creates a new ContactsRecyclerAdapter with a provided list of contacts
     *
     * @param theContacts the list of contacts
     * @param theConsumer the method to be passed
     */
    public ContactsRecyclerAdapter(final List<Contact> theContacts,
                                   final Consumer<Contact> theConsumer) {
        mContacts = theContacts;
        mConsumer = theConsumer;

    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull final ViewGroup theParent,
                                                 final int theViewType) {

        return new ContactsViewHolder(LayoutInflater
                                      .from(theParent.getContext())
                                      .inflate(R.layout.fragment_contacts_card,
                                              theParent, false));

    }
    @Override
    public void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * ContactsViewHolder is a class defining an individual ViewHolder for the ContactsFragment
     * RecyclerView.
     *
     * @author Parker Rosengreen
     * @author Chris Ding
     * @author Steven Omegna
     * @version Fall 2021
     */
    public class ContactsViewHolder extends RecyclerView.ViewHolder {

        /** The assigned view */
        private final View mView;

        /** The ViewBinding corresponded to a contact RecyclerView card */
        private final FragmentContactsCardBinding mBinding;

        /** The contact assigned to this ViewHolder */
        private Contact mContact;

        /**
         * Creates a new ContactsViewHolder with the provided view
         *
         * @param theItemView the view to be assigned
         */
        public ContactsViewHolder(@NonNull final View theItemView) {
            super(theItemView);
            mView = theItemView;
            mBinding = FragmentContactsCardBinding.bind(theItemView);
            mBinding.buttonDelete.setOnClickListener(button -> mConsumer.accept(mContact));
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
         * Displays all contact data and image views for a single contact card
         */
        private void display() {
            mBinding.contactName.setText(String.format("%s %s", mContact.getFirst(),
                                                                mContact.getLast()));
            mBinding.contactNickname.setText(mContact.getNickname());
        }
    }
}
