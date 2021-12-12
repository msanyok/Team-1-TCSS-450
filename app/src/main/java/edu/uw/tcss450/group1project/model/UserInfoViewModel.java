/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.auth0.android.jwt.JWT;

import java.util.Objects;

/**
 * ViewModel class for a user's information.
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @author Steven Omegna
 * @version Fall 2021
 */
public class UserInfoViewModel extends ViewModel {

    /** The user's JWT */
    private final String mJwt;

    /** The user's email */
    private final String mEmail;

    /** The user's nickname */
    private final String mNickname;

    /** The user's first name */
    private final String mFirstName;

    /** The user's last name */
    private final String mLastName;

    /**
     * Private constructor that creates a new UserInfoViewModel with the given JWT.
     *
     * @param theJwt the Jwt token that corresponds to the user
     * @throws NullPointerException if theJwt is null
     */
    private UserInfoViewModel(@NonNull final String theJwt) {
        mJwt = Objects.requireNonNull(theJwt, "theJwt can not be null");

        // assumes that the jwt has already been checked to make sure it is still valid
        final JWT jwt = new JWT(theJwt);
        mEmail = jwt.getClaim("email").asString();
        mNickname = jwt.getClaim("nickname").asString();
        mFirstName = jwt.getClaim("firstname").asString();
        mLastName = jwt.getClaim("lastname").asString();
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
     * Provides the user's nickname
     *
     * @return the nickname string
     */
    public String getNickname() {
        return mNickname;
    }

    /**
     * Provides the user's first name
     *
     * @return the first name
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Provides the user's last name
     *
     * @return the last name
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * A factory class that creates {@link UserInfoViewModel} instances.
     *
     * @author Charles Bryan
     * @author Austn Attaway
     * @version Fall 2021
     */
    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {

        /** The user's JWT */
        private final String mJwt;

        /**
         * Constructor that creates a new UserInfoViewModelFactory with the user's
         * jwt token
         *
         * @param theJwt the user's JWT
         * @throws NullPointerException if theEmail is null
         * @throws NullPointerException if theJwt is null
         */
        public UserInfoViewModelFactory(@NonNull final String theJwt) {
            this.mJwt = Objects.requireNonNull(theJwt, "theJwt can not be null");
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull final Class<T> theModelClass) {
            if (theModelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(mJwt);
            }
            throw new IllegalArgumentException(
                    "Argument must be: " + UserInfoViewModel.class);
        }
    }
}