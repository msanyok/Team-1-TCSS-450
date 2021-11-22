/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

/**
 * ViewModel class for a user's information.
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @version Fall 2021
 */
public class UserInfoViewModel extends ViewModel {

    /** The user's email */
    private final String mEmail;

    /** The user's JWT */
    private final String mJwt;

    /**
     * Private constructor that creates a new UserInfoViewModel with the given email and JWT.
     *
     * @param theEmail the email that corresponds to the user.
     * @param theJwt the Jwt token that corresponds to the user
     * @throws NullPointerException if theEmail is null
     * @throws NullPointerException if theJwt is null
     */
    private UserInfoViewModel(@NonNull final String theEmail,
                              @NonNull final String theJwt) {
        mEmail = Objects.requireNonNull(theEmail, "theEmail can not be null");
        mJwt = Objects.requireNonNull(theJwt, "theJwt can not be null");
    }

    /**
     * Returns the user's email
     *
     * @return the user's email
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Provides access to the jwt
     *
     * @return the jwt string
     */
    public String getJwt() {
        return mJwt;
    }

    /**
     * A factory class that creates {@link UserInfoViewModel} instances.
     *
     * @author Charles Bryan
     * @author Austn Attaway
     * @version Fall 2021
     */
    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {

        /** The user's email */
        private final String mEmail;

        /** The user's JWT */
        private final String mJwt;

        /**
         * Constructor that creates a new UserInfoViewModelFactory with the user's
         * given email and password.
         *
         * @param theEmail the user's email
         * @param theJwt the user's JWT
         * @throws NullPointerException if theEmail is null
         * @throws NullPointerException if theJwt is null
         */
        public UserInfoViewModelFactory(@NonNull final String theEmail,
                                        @NonNull final String theJwt) {
            this.mEmail = Objects.requireNonNull(theEmail, "theEmail can not be null");
            this.mJwt = Objects.requireNonNull(theJwt, "theJwt can not be null");
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull final Class<T> theModelClass) {
            if (theModelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(mEmail, mJwt);
            }
            throw new IllegalArgumentException(
                    "Argument must be: " + UserInfoViewModel.class);
        }
    }

}

