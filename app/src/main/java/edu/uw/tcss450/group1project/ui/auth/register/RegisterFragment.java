/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.auth.register;

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
import edu.uw.tcss450.group1project.utils.TextFieldHints;
import edu.uw.tcss450.group1project.utils.TextFieldValidators;

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
    public View onCreateView(@NonNull final LayoutInflater theInflater,
                             final ViewGroup theContainer,
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
        final String firstText = mBinding.editFirst.getText().toString().trim();
        TextFieldValidators.NAME_VALIDATOR.processResult(
                TextFieldValidators.NAME_VALIDATOR.apply(firstText),
                this::validateLast,
                result -> mBinding.editFirst.setError(TextFieldHints.getNameHint(firstText)));
    }

    /**
     * Attempts to validate the text inputted for the user's last name.
     *
     * If the validation succeeds, attempts to validate the nickname.
     * Else, sets an error text on the last name field that requests they enter a last name.
     */
    private void validateLast() {
        final String lastText = mBinding.editLast.getText().toString().trim();
        TextFieldValidators.NAME_VALIDATOR.processResult(
                TextFieldValidators.NAME_VALIDATOR.apply(lastText),
                this::validateNickname,
                result -> mBinding.editLast.setError(TextFieldHints.getNameHint(lastText)));
    }

    /**
     * Attempts to validate the text inputted for the user's nickname.
     *
     * If the validation succeeds, attempts to validate the email.
     * Else, sets an error text on the nickname field that requests they enter a valid nickname.
     */
    private void validateNickname() {
        final String nicknameText = mBinding.editNickname.getText().toString().trim();
        TextFieldValidators.NAME_VALIDATOR.processResult(
                TextFieldValidators.NAME_VALIDATOR.apply(nicknameText),
                this::validateEmail,
                result -> mBinding.editNickname.setError(TextFieldHints.getNameHint(nicknameText)));
    }

    /**
     * Attempts to validate the text inputted for the user's email.
     *
     * If the validation succeeds, attempts to validate the passwords.
     * Else, sets an error text on the email field that requests they enter a valid email.
     */
    private void validateEmail() {
        final String emailText = mBinding.editEmail.getText().toString().trim();
        TextFieldValidators.EMAIL_VALIDATOR.processResult(
                TextFieldValidators.EMAIL_VALIDATOR.apply(emailText),
                this::validatePasswordsMatch,
                result -> mBinding.editEmail.setError(TextFieldHints.getEmailHint(emailText)));
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

        TextFieldValidators.EMAIL_VALIDATOR.processResult(
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
        final String passwordText = mBinding.editPassword1.getText().toString();
        TextFieldValidators.PASSWORD_VALIDATOR.processResult(
                TextFieldValidators.PASSWORD_VALIDATOR.apply(passwordText),
                this::verifyAuthWithServer,
                result -> mBinding.editPassword1.setError(TextFieldHints.getPasswordHint(passwordText)));
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
        // Above call is asynchronous, do not write code below that relies on the result.
    }

    /**
     * Navigates to the registration verification page.
     */
    private void navigateToRegistrationVerification() {
        Navigation.findNavController(getView()).navigate(
                RegisterFragmentDirections.actionRegisterFragmentToRegisterVerificationFragment(
                        mBinding.editEmail.getText().toString(),
                        mBinding.editPassword1.getText().toString()
                ));

        // Remove the current JSON stored in the live data.
        // This prevents the fragment from chaining navigations
        // when they try to come back to this fragment
        mRegisterModel.removeData();
    }

    /**
     * Observes the HTTP Response from the web server. If an error occurred, notify the user
     * accordingly. If it was a success, navigate to the registration verification page.
     *
     * @param theResponse the Response from the server
     */
    private void observeResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                try {

                    final String message =
                            theResponse.getJSONObject("data").get("message").toString();

                    if (message.equals("Email exists")) {
                        mBinding.editEmail.setError(
                                "Error Authenticating: " +
                                        theResponse.getJSONObject("data")
                                                .getString("message"));
                    } else if (message.equals("Username exists")) {
                        mBinding.editNickname.setError("Error Authenticating: Nickname exists");
                    } else {
                        // a different registration error occurred that
                        // was not a duplicate email or nickname
                        mBinding.editEmail.setError("Other error. Check logs.");
                    }

                } catch (JSONException exception) {
                    Log.e("JSON Parse Error", exception.getMessage());
                }
            } else {
                navigateToRegistrationVerification();
            }
        } else {
            Log.d("Registration JSON Response", "No Response");
        }
    }

}
