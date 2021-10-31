/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

/**
 * Contact is a class that stores data for a registered contact.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class Contact {

    /** The first name */
    private String mFirst;

    /** The last name */
    private String mLast;

    /** The nickname */
    private String mNickname;

    /**
     * Creates a new contact with provided data values
     *
     * @param theFirst the first name
     * @param theLast the last name
     * @param theNickname the nickname
     */
    public Contact(final String theFirst, final String theLast, final String theNickname) {
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
     * Sets the contact's nickname
     *
     * @param theNickname the nickname to be assigned
     */
    public void setNickname(final String theNickname) {
        mNickname = theNickname;
    }

    /**
     * Sets the contact's first name
     *
     * @param theFirst the first name to be assigned
     */
    public void setFirstName(final String theFirst) {
        mFirst = theFirst;
    }

    /**
     * Sets the contact's last name
     *
     * @param theLast the last name to be assigned
     */
    public void setLastName(final String theLast) {
        mLast = theLast;
    }

    public String toString() {
        return mNickname;
    }
}
