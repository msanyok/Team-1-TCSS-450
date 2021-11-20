/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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

    /**
     * Creates a new ContactsRecyclerAdapter with a provided list of contacts
     *
     * @param theContacts the list of contacts
     */
    public ContactsRecyclerAdapter(final List<Contact> theContacts) {
        mContacts = theContacts;
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
            mBinding.buttonMore.setOnClickListener(this::expandContactCard);
        }

        /**
         * When the button is clicked in the more state, expand the card to display
         * the blog preview and switch the icon to the less state.  When the button
         * is clicked in the less state, shrink the card and switch the icon to the
         * more state.
         * @param theButton the button that was clicked
         */
        private void expandContactCard(final View theButton){
            displayContactCardPreview();
        }

        /**
         * Helper used to determine if the preview should be displayed or not.
         */
        private void displayContactCardPreview() {
            if (mBinding.buttonDelete.getVisibility() == View.GONE) {
                mBinding.buttonDelete.setVisibility(View.VISIBLE);
                mBinding.buttonMore.setImageIcon(
                        Icon.createWithResource(
                                mView.getContext(),
                                R.drawable.ic_less_grey_arrow_up_24));
            } else {
                mBinding.buttonDelete.setVisibility(View.GONE);
                mBinding.buttonMore.setImageIcon(
                        Icon.createWithResource(
                                mView.getContext(),
                                R.drawable.ic_more_grey_arrow_down_24dp));
            }
        }

        /**
         * Assigns a contact to this view holder
         *
         * @param theContact the contact to be assigned
         */
        void setContact(final Contact theContact) {
            mContact = theContact;
            displayContactCardPreview();
            display();
        }

        /** Displays all contact data and image views for a single contact card */
        private void display() {
            mBinding.contactName.setText(String.format("%s %s", mContact.getFirst(),
                                                                mContact.getLast()));
            mBinding.contactNickname.setText(mContact.getNickname());
        }
    }
}
