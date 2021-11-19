/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashSet;
import java.util.Set;

import edu.uw.tcss450.group1project.ui.contacts.Contact;

/**
 * ChatRoomParticipantViewModel is a fragment-level view model for storing added participants
 * to a new chat room.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChatRoomParticipantViewModel extends ViewModel {

    /** The set of added participants */
    private final MutableLiveData<Set<Contact>> mParticipants;

    /**
     * Creates a new ChatRoomParticipantViewModel
     */
    public ChatRoomParticipantViewModel() {
        mParticipants = new MutableLiveData<>();
        mParticipants.setValue(new HashSet<>());
    }

    /**
     * Adds a new participant to this view model
     *
     * @param theContact the participant to be added
     */
    public void addParticipant(final Contact theContact) {
        mParticipants.getValue().add(theContact);
    }

    /**
     * Removes a new participant to this view model
     *
     * @param theContact the participant to be removed
     */
    public void removeParticipant(final Contact theContact) {
        mParticipants.getValue().remove(theContact);
    }

    /**
     * Returns whether this view model contains a provided participant
     *
     * @param theContact the participant in question
     * @return true if the participant is found, false otherwise
     */
    public boolean containsParticipant(final Contact theContact) {
        return mParticipants.getValue().contains(theContact);
    }
}
