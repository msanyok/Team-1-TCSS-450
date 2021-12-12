/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.utils;

import java.util.Objects;

/**
 * A utility class that provides hints for invalid text field inputs
 * based on the type of text field and the current text in a text field.
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public final class TextFieldHints {

    /**
     * Returns an appropriate hint for the given invalid name text field string.
     * Note the given string MUST be invalid for this method to return the correct result.
     * A name is invalid if its length is less than 2.
     *
     * @param theName the name the text field contains
     * @return the hint to be displayed to the user
     * @throws NullPointerException if theName is null
     */
    public static String getNameHint(final String theName) {
        Objects.requireNonNull(theName, "theName can not be null");
        // the only invalid state names have is being < 2 characters.
        // we assume all strings passed to this method are invalid, so this is the only result
        return "Names must be at least two characters.";
    }

    /**
     * Returns an appropriate hint for the given invalid email text field string.
     * Note the given string MUST be invalid for this method to return the correct result.
     * An email is invalid if its length is less than 3, it includes whitespace,
     * or does not contain an '@' symbol.
     *
     * @param theEmail the email the email text field contains
     * @return the hint displayed to the user
     * @throws NullPointerException if theEmail is null
     */
    public static String getEmailHint(final String theEmail) {
        Objects.requireNonNull(theEmail, "theEmail can not be null");
        if (theEmail.length() < TextFieldValidators.EMAIL_MIN_LENGTH) {
            return "Emails must be at least 3 characters long.";
        } else if (checkContainsWhiteSpace(theEmail)) {
            return "Emails can not contain whitespace.";
        } else {
            // only other invalid possibility is the email does not contain '@'
            return "Emails must contain an \'@\' symbol";
        }
    }


    /**
     * Returns an appropriate hint for the given invalid password text field string.
     * Note the given string MUST be invalid for this method to return the correct result.
     *
     * A password is invalid if:
     *  - its length is less than 8
     *  - does not contain a special character
     *  - includes whitespace
     *  - does not include at least one digit
     *  - does not have at least one uppercase OR lowercase letter
     *
     * @param thePassword the password the password text field contains
     * @return the hint displayed to the user
     * @throws NullPointerException if thePassword is null
     */
    public static String getPasswordHint(final String thePassword) {
        Objects.requireNonNull(thePassword, "thePassword can not be null");
        if (thePassword.length() < TextFieldValidators.PASSWORD_MIN_LENGTH) {
            return "Passwords must be at least 8 characters.";
        } else if (!checkContainsSpecialChars(thePassword)) {
            return "Passwords must contain at least one special character.";
        } else if (checkContainsWhiteSpace(thePassword)) {
            return "Passwords can not include whitespace";
        } else if (!checkContainsDigits(thePassword)) {
            return "Passwords must include at least one digit";
        } else {
            // only invalid state unchecked is not containing an alphabetic character
            return "Passwords must include at least alphabetic character.";
        }
    }


    /**
     * Private helper method that returns true if the given string contains at least one digit.
     *
     * @param theText the text to check
     * @return whether or not theText contains a digit
     */
    private static boolean checkContainsDigits(final String theText) {
        for (int i = 0; i < theText.length(); i++) {
            if (Character.isDigit(theText.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Private helper method that returns true if the given string contains at
     * least one special character.
     *
     * @param theText the text to check
     * @return whether or not theText contains a special character
     */
    private static boolean checkContainsSpecialChars(final String theText) {
        for (int i = 0; i < theText.length(); i++) {
            if ("@#$%&*!?".contains(Character.toString(theText.charAt(i)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Private helper method that returns true if the given string contains any whitespace.
     *
     * @param theText the text to check
     * @return whether or not theText contains whitespace
     */
    private static boolean checkContainsWhiteSpace(final String theText) {
        for (int i = 0; i < theText.length(); i++) {
            if (Character.isWhitespace(theText.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}