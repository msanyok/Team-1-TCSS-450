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

public class ParticipantSelectorRecyclerAdapter
        extends RecyclerView.Adapter<ParticipantSelectorRecyclerAdapter.ParticipantViewHolder> {

    private final List<Contact> mContactList;

    private final ChatRoomParticipantViewModel mModel;

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

    public class ParticipantViewHolder extends RecyclerView.ViewHolder {

        private final View mView;

        private final FragmentCreateChatRoomContactCardBinding mBinding;

        private Contact mContact;

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

        public void setContact(final Contact theContact) {
            mContact = theContact;
            display();
        }

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
