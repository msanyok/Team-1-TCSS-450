/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */
package edu.uw.tcss450.group1project.ui.settings;

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
import android.widget.Toast;

import org.json.JSONObject;

import edu.uw.tcss450.group1project.databinding.FragmentPasswordChangeBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.utils.PasswordValidator;
import edu.uw.tcss450.group1project.utils.TextFieldHints;
import edu.uw.tcss450.group1project.utils.TextFieldValidators;

/**
 * An {@link Fragment} child class that handles the data allow user to enter
 * the existing password to set a new one inside app (on setting).
 *
 * @author Chris Ding
 * @author Austn Attaway
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ChangePasswordFragment extends Fragment {

    /** ViewBinding reference to the change password Fragment UI */
    private FragmentPasswordChangeBinding mBinding;

    /** ViewModel used for Password Reset */
    private ChangePasswordViewModel mChangePasswordModel;

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);

    }


    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             Bundle theSavedInstanceState) {

        mBinding = FragmentPasswordChangeBinding.inflate(theInflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mChangePasswordModel =
                new ViewModelProvider(this).get(ChangePasswordViewModel.class);

        mBinding.buttonChangePassword.setOnClickListener(this::changePassword);
        mChangePasswordModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    /** start the chain for validating text field for existed and new passwords*/
    private void changePassword(final View theButton) { validateExistPassword(); }

    /**
     * Attempts to validate that the existing password is valid.
     *
     * If the validation succeeds, attempts to validate the new password user created.
     * Else, sets an error on the old password field asking the user to input a valid password.
     */
    public void validateExistPassword() {
        final String passwordText = mBinding.oldPassword.getText().toString();
        TextFieldValidators.PASSWORD_VALIDATOR.processResult(
                TextFieldValidators.PASSWORD_VALIDATOR.apply(passwordText),
                this::validateNewPassword,
                result -> mBinding.oldPassword.setError(
                        TextFieldHints.getPasswordHint(passwordText)));
    }

    /**
     * Attempts to validate that the inputted new password is valid.
     *
     * If the validation succeeds, attempts to validate the password match.
     * Else, sets an error on the old password field asking the user to input
     * a valid password.
     */
    public void validateNewPassword() {
        final String passwordText = mBinding.newPassword.getText().toString();
        TextFieldValidators.PASSWORD_VALIDATOR.processResult(
                TextFieldValidators.PASSWORD_VALIDATOR.apply(passwordText),
                this::newPasswordMatch,
                result -> mBinding.newPassword.setError(
                        TextFieldHints.getPasswordHint(passwordText)));

    }

    /**
     * Attempts to validate that the inputted passwords are the same.
     *
     * If the validation succeeds, make the request.
     * Else, sets an error on the first password field telling the user the passwords must match.
     */
    public void newPasswordMatch(){
        PasswordValidator matchValidator =
                checkClientPredicate(
                        pwd -> pwd.equals(
                                mBinding.retypeNewPassword.getText().toString().trim()));

        TextFieldValidators.PASSWORD_VALIDATOR.processResult(
                matchValidator.apply(mBinding.newPassword.getText().toString().trim()),
                this::verifyPasswordWithServer,
                result -> mBinding.newPassword.setError("Passwords must match."));
    }

    /**
     * Asynchronously attempts to verify the account with password that user provided
     * and reset the password with the data on the server.
     */
    private void verifyPasswordWithServer() {
        mChangePasswordModel.connect(
                mBinding.oldPassword.getText().toString(),
                mBinding.newPassword.getText().toString(),
                new ViewModelProvider(this.getActivity()).get(UserInfoViewModel.class).getJwt());

    }

    /**
     * Navigates back to the setting page after the process completed.
     */
    private void navigateToSetting() {
        Navigation.findNavController(getView())
                .navigate(ChangePasswordFragmentDirections
                        .actionNavigationPasswordResetToNavigationSettings());
    }

    /**
     * Observes the HTTP Response from the web server. This observer should be
     * attached to PasswordResetToolbarViewModel.
     *
     * @param theResponse the Response from the server
     */
    private void observeResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                mBinding.oldPassword.setError("Verification failed.");
            } else {
                Toast.makeText(getContext(),
                        "Password changed successfully.", Toast.LENGTH_SHORT).show();
                navigateToSetting();
            }
        } else {
            Log.d("Registration Verification JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }
}