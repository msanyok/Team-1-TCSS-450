/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.group1project.ui.contacts.Contact;

/**
 * ChatRoom is a class to manage conversations between the user and a list of contact participants.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChatRoom {

    /** The contacts participating in this chat room */
    private final List<Contact> mContacts;

    // IDEA : possibly a List of type Message (message would store String message and Contact sender?

    /**
     * Creates a new chat room with the provided list of contacts
     *
     * @param theContacts the list of contacts
     */
    public ChatRoom(final List<Contact> theContacts) {
        mContacts = theContacts;
    }

    /**
     * Returns a defensive copy of the contacts participating in this chat room
     *
     * @return the list of contacts
     */
    public List<Contact> getParticipants() {
        List<Contact> copy = new ArrayList<>();
        for (final Contact c : mContacts) {
            copy.add(new Contact(c.getFirst(), c.getLast(), c.getNickname()));
        }
        return copy;
    }

    /**
     * Adds a new participant to the chat room
     *
     * @param theContact the participant to be added
     */
    public void addParticipant(final Contact theContact) {
        mContacts.add(theContact);
    }
}
