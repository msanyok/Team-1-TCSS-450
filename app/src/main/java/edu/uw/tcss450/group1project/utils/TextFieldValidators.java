package edu.uw.tcss450.group1project.utils;

import static edu.uw.tcss450.group1project.utils.PasswordValidator.checkExcludeWhiteSpace;
import static edu.uw.tcss450.group1project.utils.PasswordValidator.checkPwdDigit;
import static edu.uw.tcss450.group1project.utils.PasswordValidator.checkPwdLength;
import static edu.uw.tcss450.group1project.utils.PasswordValidator.checkPwdLowerCase;
import static edu.uw.tcss450.group1project.utils.PasswordValidator.checkPwdSpecialChar;
import static edu.uw.tcss450.group1project.utils.PasswordValidator.checkPwdUpperCase;

/**
 * Utility class that provides text field validators for registration and authentication.
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public class TextFieldValidators {

    /** The minimum length for a name */
    public static final int NAME_MIN_LENGTH = 2;

    /** The minimum length for an email */
    public static final int EMAIL_MIN_LENGTH = 3;

    /** The minimum length for a password */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /**
     * A {@link PasswordValidator} dedicated to validating the user's inputted name texts.
     * Used for the first name, last name, and nickname.
     *
     * The name texts will be considered valid if the length of the given
     * text is greater than one.
     */
    public static final PasswordValidator NAME_VALIDATOR = checkPwdLength(NAME_MIN_LENGTH - 1);


    /**
     * A {@link PasswordValidator} dedicated to validating the user's inputted email text.
     *
     * The email text will be considered valid if the length of the given
     * text is greater than 2, does not include whitespace, and has the '@' symbol.
     */
    public static final PasswordValidator EMAIL_VALIDATOR = checkPwdLength(EMAIL_MIN_LENGTH - 1)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /**
     * A {@link PasswordValidator} dedicated to validating the user's inputted password text.
     *
     * The password text will be considered valid if the length of the given
     * text is greater than 7, has at least one special character,
     * does not include whitespace, includes at least one digit, and contains at least one
     * uppercase or lowercase letter.
     */
    public static final PasswordValidator PASSWORD_VALIDATOR = checkPwdLength(PASSWORD_MIN_LENGTH - 1)
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

}
