/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Contact is a class that stores data for a registered contact.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class Contact implements Serializable {

    /** The Member ID */
    private final String mMemberId;

    /** The first name */
    private final String mFirst;

    /** The last name */
    private final String mLast;

    /** The nickname */
    private final String mNickname;

    /**
     * Creates a new contact with provided data values
     *
     * @param theFirst the first name
     * @param theLast the last name
     * @param theNickname the nickname
     * @param theID the member ID
     */
    public Contact(final String theFirst, final String theLast,
                   final String theNickname, final String theID) {
        mMemberId = theID;
        mFirst = theFirst;
        mLast = theLast;
        mNickname = theNickname;
    }

    /**
     * Provides the contact's first name
     *
     * @return the first name
     */
    public String getFirst() {
        return mFirst;
    }

    /**
     * Provides the contact's last name
     *
     * @return the last name
     */
    public String getLast() {
        return mLast;
    }

    /**
     * Provides the contact's nickname
     *
     * @return the nickname
     */
    public String getNickname() {
        return mNickname;
    }

    /**
     * Provides the contact's nickname
     *
     * @return the nickname
     */
    public String getMemberId() { return mMemberId; }

    @NonNull
    @Override
    public String toString() {
        return mNickname;
    }

    @Override
    public boolean equals(final Object theOther) {
        boolean result = false;
        if (theOther instanceof Contact) {
            result = mMemberId.equals(((Contact) theOther).getMemberId());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mMemberId);
    }
}
