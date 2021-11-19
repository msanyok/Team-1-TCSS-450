package edu.uw.tcss450.group1project.ui.messages;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Set;

import edu.uw.tcss450.group1project.ui.contacts.Contact;

public class ChatRoomParticipantViewModel extends ViewModel {

    private final MutableLiveData<Set<Contact>> mParticipants;

    public ChatRoomParticipantViewModel() {
        mParticipants = new MutableLiveData<>();
        mParticipants.setValue(new HashSet<>());
    }

    public void clearParticipants() {
        mParticipants.setValue(new HashSet<>());
    }

    public void addParticipant(final Contact theContact) {
        mParticipants.getValue().add(theContact);
    }

    public void removeParticipant(final Contact theContact) {
        mParticipants.getValue().remove(theContact);
    }

    public boolean containsParticipant(final Contact theContact) {
        return mParticipants.getValue().contains(theContact);
    }
}
