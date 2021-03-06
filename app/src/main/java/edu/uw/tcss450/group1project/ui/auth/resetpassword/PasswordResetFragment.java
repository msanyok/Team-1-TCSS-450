/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.auth.resetpassword;

import static edu.uw.tcss450.group1project.utils.PasswordValidator.checkClientPredicate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONObject;

import edu.uw.tcss450.group1project.databinding.FragmentPasswordResetBinding;
import edu.uw.tcss450.group1project.utils.PasswordValidator;
import edu.uw.tcss450.group1project.utils.TextFieldHints;
import edu.uw.tcss450.group1project.utils.TextFieldValidators;

/**
 * An {@link Fragment} child class that handles the data allow user to enter the verification code
 * and reset password.
 *
 * @author Chris Ding
 * @author Austn Attaway
 * @version Fall 2021
 */
public class PasswordResetFragment extends Fragment {

    /** ViewBinding reference to the reset password Fragment UI */
    private FragmentPasswordResetBinding mBinding;

    /** ViewModel used for Password Reset */
    private PasswordResetViewModel mPasswordResetModel;

    /** Space checker helper for code */
    private final PasswordValidator mCodeValidator = PasswordValidator
            .checkPwdLength(5).and(PasswordValidator.checkExcludeWhiteSpace());

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public PasswordResetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater theInflater,
                             final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        mBinding = FragmentPasswordResetBinding.inflate(theInflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mPasswordResetModel =
                new ViewModelProvider(this).get(PasswordResetViewModel.class);

        mBinding.buttonResetPasswordDone.setOnClickListener(button -> validateVerificationCode());
        mPasswordResetModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    /**
     * Helper method to validate Verification Code
     */
    public void validateVerificationCode() {
        mCodeValidator.processResult(
                mCodeValidator.apply(mBinding.resetComfirmationCode.getText().toString().trim()),
                this::validateNewPassword,
                result -> mBinding.resetComfirmationCode.setError("Enter a valid code"));
    }

    /**
     * Attempts to validate that the inputted password is valid.
     *
     * If the validation succeeds, attempts to validate the password match.
     * Else, sets an error on the first password field asking the user to input a valid password.
     */
    private void validateNewPassword() {
        final String passwordText = mBinding.newPasswordText.getText().toString();
        TextFieldValidators.PASSWORD_VALIDATOR.processResult(
                TextFieldValidators.PASSWORD_VALIDATOR.apply(passwordText),
                this::newPasswordMatch,
                result -> mBinding.newPasswordText.setError(
                        TextFieldHints.getPasswordHint(passwordText)));
    }

    /**
     * Attempts to validate that the inputted passwords are the same.
     *
     * If the validation succeeds, make the request.
     * Else, sets an error on the first password field telling the user the passwords must match.
     */
    private void newPasswordMatch() {
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(
                                mBinding.retypeNewPasswordText.getText().toString().trim()));

        TextFieldValidators.EMAIL_VALIDATOR.processResult(
                matchValidator.apply(mBinding.newPasswordText.getText().toString().trim()),
                this::verifyWithServer,
                result -> mBinding.newPasswordText.setError("Passwords must match."));
    }

    /**
     * Asynchronously attempts to verify the account
     * and reset the password with the data on the server.
     */
    private void verifyWithServer() {
        final PasswordResetFragmentArgs args =
                PasswordResetFragmentArgs.fromBundle(getArguments());

        mPasswordResetModel.connect(args.getEmail(),
                mBinding.newPasswordText.getText().toString(),
                mBinding.resetComfirmationCode.getText().toString().trim()
        );
    }

    /**
     * Navigates back to the sign in page with email and new password.
     */
    private void navigateToSignInFragment() {
        final PasswordResetFragmentArgs args =
                PasswordResetFragmentArgs.fromBundle(getArguments());

        PasswordResetFragmentDirections
                .ActionPasswordResetFragmentToSignInFragment directions =
                PasswordResetFragmentDirections
                        .actionPasswordResetFragmentToSignInFragment();

        directions.setEmail(args.getEmail());
        directions.setPassword(mBinding.newPasswordText.getText().toString());
        Toast.makeText(getContext(), "Password reset successfully.", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(getView()).navigate(directions);
    }

    /**
     * Observes the HTTP Response from the web server. This observer should be
     * attached to PasswordResetViewModel.
     *
     * @param theResponse the Response from the server
     */
    private void observeResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                mBinding.resetComfirmationCode.setError("Verification failed.");
            } else {
                navigateToSignInFragment();
            }
        } else {
            Log.d("Registration Verification JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }
}