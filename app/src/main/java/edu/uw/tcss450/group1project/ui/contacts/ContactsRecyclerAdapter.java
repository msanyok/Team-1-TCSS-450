package edu.uw.tcss450.group1project.ui.contacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentContactsCardBinding;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ContactsViewHolder> {

    private final List<Contact> mContacts;

    public ContactsRecyclerAdapter(List<Contact> theContacts) {
        mContacts = theContacts;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup theParent, int theViewType) {
        return new ContactsViewHolder(LayoutInflater
                                      .from(theParent.getContext())
                                      .inflate(R.layout.fragment_contacts_card, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private final FragmentContactsCardBinding mBinding;
        private Contact mContact;

        public ContactsViewHolder(@NonNull View theItemView) {
            super(theItemView);
            mView = theItemView;
            mBinding = FragmentContactsCardBinding.bind(theItemView);
        }

        public void setContact(final Contact theContact) {
            mContact = theContact;
            display();
        }

        private void display() {
            mBinding.contactName.setText(String.format("%s %s", mContact.getFirst(), mContact.getLast()));
            mBinding.contactNickname.setText(mContact.getNickname());
            mBinding.contactImage.setImageResource(R.drawable.ic__android__black_24dp);
            mBinding.arrowImage.setImageResource(R.drawable.ic_arrow_right__black_24dp);
        }
    }
}
