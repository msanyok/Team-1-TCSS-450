/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */
package edu.uw.tcss450.group1project.ui.auth.resetpassword;

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

import org.json.JSONObject;

import edu.uw.tcss450.group1project.databinding.FragmentEnterEmailBinding;
import edu.uw.tcss450.group1project.utils.TextFieldHints;
import edu.uw.tcss450.group1project.utils.TextFieldValidators;

/**
 * A {@link Fragment} subclass that handles input and output
 * when the user is attempting to reset password by input email.
 *
 * @author Chris Ding
 * @author Austn Attaway
 * @version Fall 2021
 */
public class EnterEmailFragment extends Fragment {

    /** ViewBinding reference to the email entry Fragment UI */
    private FragmentEnterEmailBinding mBinding;

    /** ViewModel for the password reset */
    private PasswordResetEmailViewModel mEnterEmailModel;

    public EnterEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        mBinding = FragmentEnterEmailBinding.inflate(theInflater);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mBinding.buttonSentEmail.setOnClickListener(this::attemptSentEmail);
        mEnterEmailModel =
                new ViewModelProvider(this).get(PasswordResetEmailViewModel.class);
        mEnterEmailModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);
    }

    /**
     * Starts the chain of text field validation that attempts to validate email input field.
     *
     * @param theButton the Button that was pressed to invoke this method.
     */
    private void attemptSentEmail(final View theButton){validateResetEmail();}

    /**
     * Attempts to validate the text inputted for the user's email.
     *
     * If the validation succeeds, verify the credentials with the server
     * Else, sets an error text on the email field that requests they enter a vaild email.
     */
    private void validateResetEmail() {
        final String emailText = mBinding.editResetEmail.getText().toString().trim();
        TextFieldValidators.EMAIL_VALIDATOR.processResult(
                TextFieldValidators.EMAIL_VALIDATOR.apply(emailText),
                this::sendResetPasswordCode,
                result -> mBinding.editResetEmail.setError(TextFieldHints.getEmailHint(emailText)));
    }

    /**
     * Ask for a verify code.
     */
    private void sendResetPasswordCode(){
        mEnterEmailModel.connect(mBinding.editResetEmail.getText().toString());
    }

    /**
     * Navigates to the password reset page.
     */
    private void navigateToPasswordReset() {
        Navigation.findNavController(getView()).navigate(
                EnterEmailFragmentDirections.actionEnterEmailFragmentToPasswordResetFragment(
                        mBinding.editResetEmail.getText().toString()
                ));
        mEnterEmailModel.removeData();
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
                mBinding.editResetEmail.setError("Verification failed.");
            } else {
                navigateToPasswordReset();
            }
        } else {
            Log.d("Email Code JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }

}