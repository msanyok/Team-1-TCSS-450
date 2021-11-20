/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;


// NOTE: THIS CLASS WAS ADDED FROM LAB 5,
//       IT IS USED IN THE PUSH RECIEVER JAVA CLASS.
//       IT SHOULD BE USEFUL FOR STORING INDIVIDUAL CHAT MESSAGES.

/**
 * Stores data about a particular chat message
 */
public final class ChatMessage implements Serializable {

    /** The unique ID of the message */
    private final int mMessageId;

    /** The String representation of the message contents */
    private final String mMessage;

    /** The sender of the message */
    private final String mSender;

    /** The time stamp of when this message was sent */
    private final String mTimeStamp;

    /**
     * Creates a new Chat Message that contains the given data.
     *
     * @param theMessageId the unique ID of this message
     * @param theMessage the message's content
     * @param theSender the sender of the message
     * @param theTimeStamp when the message was sent by the sender
     * @throws NullPointerException if theMessage is null
     * @throws NullPointerException if theSender is null
     * @throws NullPointerException if theTimeStamp is null
     */
    public ChatMessage(final int theMessageId,
                       final String theMessage,
                       final String theSender,
                       final String theTimeStamp) {
        mMessageId = theMessageId;
        mMessage = Objects.requireNonNull(theMessage, "theMessage can not be null");
        mSender = Objects.requireNonNull(theSender, "theSender can not be null");
        mTimeStamp = Objects.requireNonNull(theTimeStamp, "theTimeStamp can not be null");
    }

    /**
     * Static factory method to turn a properly formatted JSON String into a
     * ChatMessage object.
     *
     * @param theMessageJson the String to be parsed into a ChatMessage Object.
     * @return a ChatMessage Object with the details contained in the JSON String.
     * @throws JSONException when cmAsString cannot be parsed into a ChatMessage.
     */
    public static ChatMessage createFromJsonString(final String theMessageJson)
            throws JSONException {
        final JSONObject msg = new JSONObject(theMessageJson);
        return new ChatMessage(msg.getInt("messageid"),
                msg.getString("message"),
                msg.getString("email"),
                msg.getString("timestamp"));
    }

    /**
     * Returns a String representation of the message's contents
     * @return a String representation of the message's contents
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Returns a String representation of who sent this message
     * @return a String representation of who sent this message
     */
    public String getSender() {
        return mSender;
    }

    /**
     * Returns a String representation of when this message was sent
     * @return a String representation of when this message was sent
     */
    public String getTimeStamp() {
        return mTimeStamp;
    }

    /**
     * Returns the ID of this chat message
     * @return the ID of this chat message
     */
    public int getMessageId() {
        return mMessageId;
    }

    /**
     * Provides equality solely based on MessageId.
     * @param theOther the other object to check for equality
     * @return true if other message ID matches this message ID, false otherwise
     */
    @Override
    public boolean equals(@Nullable final Object theOther) {
        boolean result = false;
        if (theOther instanceof ChatMessage) {
            result = mMessageId == ((ChatMessage) theOther).mMessageId;
        }
        return result;
    }
}
