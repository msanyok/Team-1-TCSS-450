/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import java.util.Objects;

/**
 * Stores data about a particular chat room
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class ChatRoom {

    /** The name of this chat room */
    private String mChatRoomName;

    /** The unique id for this chat room */
    private String mChatRoomID;

    /** The most recent message sent in this chat room */
    private String mChatRoomMessage;

    /** The timestamp that corresponds to when the most recent message was sent in this chat room */
    private String mTimestamp;

    /**
     * Creates a new chat room with the provided name, id, and most recent message.
     *
     * @param theName the name of the chat
     * @param theID the unique ID of the chat
     * @param theMostRecentMessage the most recent message text sent in this chat.
     * @param theTimestamp the timestamp that corresponds to when
     *                     the most recent message in this chat was sent.
     * @throws NullPointerException if theName is null
     * @throws NullPointerException if theID is null
     * @throws NullPointerException if theMostRecentMessage is null
     * @throws NullPointerException if theTimestamp is null
     */
    public ChatRoom(final String theName,
                    final String theID,
                    final String theMostRecentMessage,
                    final String theTimestamp) {

        mChatRoomName = Objects.requireNonNull(theName, "theName can not be null");
        mChatRoomID = Objects.requireNonNull(theID, "theID can not be null");
        mChatRoomMessage = Objects.requireNonNull(theMostRecentMessage,
                "theMostRecentMessage can not be null");
        mTimestamp = Objects.requireNonNull(theTimestamp, "theTimestamp can not be null");
    }

    /**
     * Returns this chat's name
     *
     * @return the chat name
     */
    public String getChatName() {
        return mChatRoomName;
    }

    /**
     * Returns this chat's unique ID
     *
     * @return the unique id
     */
    public String getChatID() {
        return mChatRoomID;
    }

    /**
     * Returns the most recent message sent in this chat
     *
     * @return the most recent message
     */
    public String getMostRecentMessage() {
        return mChatRoomMessage;
    }

    /**
     * Returns the timestamp corresponding to
     * when the most recent message in this chat was sent
     *
     * @return the timestamp
     */
    public String getTimestamp() {
        return mTimestamp;
    }

}
