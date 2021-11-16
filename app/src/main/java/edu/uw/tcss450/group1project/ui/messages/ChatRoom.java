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

//    /** The contacts participating in this chat room */
//    private final List<Contact> mContacts;

    private String mChatRoomName;
    private String mChatRoomID;
    private String mChatRoomMessage;

    /**
     * Creates a new chat room with the provided name, id, and most recent message.
     *
     * @param theName
     * @param theName
     * @param theName
     */
    public ChatRoom(final String theName,
                    final String theID,
                    final String theMostRecentMessage) {

        mChatRoomName = theName;
        mChatRoomID = theID;
        mChatRoomMessage = theMostRecentMessage;

    }

    public String getChatName() {
        return mChatRoomName;
    }

    public String getChatID() {
        return mChatRoomID;
    }

    public String getMostRecentMessage() {
        return mChatRoomMessage;
    }

//    /**
//     * Returns a defensive copy of the contacts participating in this chat room
//     *
//     * @return the list of contacts
//     */
//    public List<Contact> getParticipants() {
//        List<Contact> copy = new ArrayList<>();
//        for (final Contact c : mContacts) {
//            copy.add(new Contact(c.getFirst(), c.getLast(), c.getNickname()));
//        }
//        return copy;
//    }

//    /**
//     * Adds a new participant to the chat room
//     *
//     * @param theContact the participant to be added
//     */
//    public void addParticipant(final Contact theContact) {
//        mContacts.add(theContact);
//    }
}
