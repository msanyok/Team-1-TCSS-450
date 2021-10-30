/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.auth.register;

import static edu.uw.tcss450.group1project.utils.PasswordValidator.*;
import static edu.uw.tcss450.group1project.utils.PasswordValidator.checkClientPredicate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.group1project.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.group1project.utils.PasswordValidator;


/**
 * A {@link Fragment} subclass that handles input and output
 * when the user is attempting to register for the app.
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @version Fall 2021
 */
public class RegisterFragment extends Fragment {

    /** ViewBinding reference to the Register Fragment UI */
    private FragmentRegisterBinding mBinding;

    /** ViewModel for registration */
    private RegisterViewModel mRegisterModel;

    /**
     * A {@link PasswordValidator} dedicated to validating the user's inputted name texts.
     * Used for the first name, last name, and nickname.
     *
     * The name texts will be considered valid if the length of the given
     * text is greater than one.
     */
    private PasswordValidator mNameValidator = checkPwdLength(1);

    /**
     * A {@link PasswordValidator} dedicated to validating the user's inputted email text.
     *
     * The email text will be considered valid if the length of the given
     * text is greater than 2, does not include whitespace, and has the '@' symbol.
     */
    private PasswordValidator mEmailValidator = checkPwdLength(2)
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
    private PasswordValidator mPassWordValidator =
            checkClientPredicate(pwd -> pwd.equals(mBinding.editPassword2.getText().toString()))
                    .and(checkPwdLength(7))
                    .and(checkPwdSpecialChar())
                    .and(checkExcludeWhiteSpace())
                    .and(checkPwdDigit())
                    .and(checkPwdLowerCase().or(checkPwdUpperCase()));

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mRegisterModel = new ViewModelProvider(getActivity())
                .get(RegisterViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        mBinding = FragmentRegisterBinding.inflate(theInflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);

        mBinding.buttonRegister.setOnClickListener(this::attemptRegister);
        mRegisterModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    /**
     * Starts the chain of text field validation that attempts to validate
     * all registration text input fields.
     *
     * @param theButton the Button that was pressed to invoke this method.
     */
    private void attemptRegister(final View theButton) {
        validateFirst();
    }

    /**
     * Attempts to validate the text inputted for the user's first name.
     *
     * If the validation succeeds, attempts to validate the last name.
     * Else, sets an error text on the first name field that requests they enter a first name.
     */
    private void validateFirst() {
        mNameValidator.processResult(
                mNameValidator.apply(mBinding.editFirst.getText().toString().trim()),
                this::validateLast,
                result -> mBinding.editFirst.setError("Please enter a first name."));
    }

    /**
     * Attempts to validate the text inputted for the user's last name.
     *
     * If the validation succeeds, attempts to validate the nickname.
     * Else, sets an error text on the last name field that requests they enter a last name.
     */
    private void validateLast() {
        mNameValidator.processResult(
                mNameValidator.apply(mBinding.editLast.getText().toString().trim()),
                this::validateNickname,
                result -> mBinding.editLast.setError("Please enter a last name."));
    }

    /**
     * Attempts to validate the text inputted for the user's nickname.
     *
     * If the validation succeeds, attempts to validate the email.
     * Else, sets an error text on the nickname field that requests they enter a valid nickname.
     */
    private void validateNickname() {
        mNameValidator.processResult(
                mNameValidator.apply(mBinding.editNickname.getText().toString().trim()),
                this::validateEmail,
                result -> mBinding.editNickname.setError("Please enter a valid nickname."));
    }

    /**
     * Attempts to validate the text inputted for the user's email.
     *
     * If the validation succeeds, attempts to validate the passwords.
     * Else, sets an error text on the email field that requests they enter a valid email.
     */
    private void validateEmail() {
        mEmailValidator.processResult(
                mEmailValidator.apply(mBinding.editEmail.getText().toString().trim()),
                this::validatePasswordsMatch,
                result -> mBinding.editEmail.setError("Please enter a valid Email address."));
    }

    /**
     * Attempts to validate that the inputted passwords are the same.
     *
     * If the validation succeeds, attempts to validate the password itself.
     * Else, sets an error on the first password field telling the user the passwords must match.
     */
    private void validatePasswordsMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(mBinding.editPassword2.getText().toString().trim()));

        mEmailValidator.processResult(
                matchValidator.apply(mBinding.editPassword1.getText().toString().trim()),
                this::validatePassword,
                result -> mBinding.editPassword1.setError("Passwords must match."));
    }

    /**
     * Attempts to validate that the inputted password is valid.
     *
     * If the validation succeeds, verify all of the credentials with the server.
     * Else, sets an error on the first password field asking the user to input a valid password.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(mBinding.editPassword1.getText().toString()),
                this::verifyAuthWithServer,
                result -> mBinding.editPassword1.setError("Please enter a valid Password."));
    }

    /**
     * Asynchronously attempts to register a new account in the server with the information
     * given in the registration text fields.
     */
    private void verifyAuthWithServer() {
        mRegisterModel.connect(
                mBinding.editFirst.getText().toString(),
                mBinding.editLast.getText().toString(),
                mBinding.editNickname.getText().toString(),
                mBinding.editEmail.getText().toString(),
                mBinding.editPassword1.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().

    }

    /**
     * Navigates to the login page.
     */
    private void navigateToLogin() {
        RegisterFragmentDirections.ActionRegisterFragmentToLoginFragment directions =
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();

        directions.setEmail(mBinding.editEmail.getText().toString());
        directions.setPassword(mBinding.editPassword1.getText().toString());

        Navigation.findNavController(getView()).navigate(directions);

    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to SignInViewModel.
     *
     * @param theResponse the Response from the server
     */
    private void observeResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                try {
                    mBinding.editEmail.setError(
                            "Error Authenticating: " +
                                    theResponse.getJSONObject("data").getString("message"));
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            } else {
                navigateToLogin();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

}
