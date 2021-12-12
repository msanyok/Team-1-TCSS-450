/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.auth.verification;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONObject;

import edu.uw.tcss450.group1project.databinding.FragmentVerificationBinding;
import edu.uw.tcss450.group1project.utils.PasswordValidator;

/**
 * A {@link Fragment} subclass that handles input and output
 * when the user is attempting to verify their account using
 * a verification code.
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public class RegisterVerificationFragment extends Fragment {

    /** ViewBinding reference to the Verification Fragment UI */
    private FragmentVerificationBinding mBinding;

    /** ViewModel for the verification */
    private RegisterVerificationViewModel mRegisterVerificationModel;

    /**
     * A {@link PasswordValidator} dedicated to validating the user's inputted verification
     * code.
     *
     * The verification code will be valid if the code entered is at least 6 characters
     * and contains no whitespace.
     */
    private final PasswordValidator mCodeValidator = PasswordValidator.checkPwdLength(5)
            .and(PasswordValidator.checkExcludeWhiteSpace());


    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mRegisterVerificationModel = new ViewModelProvider(getActivity())
                .get(RegisterVerificationViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater theInflater,
                             final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        mBinding = FragmentVerificationBinding.inflate(theInflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);

        mBinding.buttonVerification.setOnClickListener(this::attemptVerification);
        mBinding.buttonResendCode.setOnClickListener(this::sendNewVerificationCode);
        mRegisterVerificationModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }

    /**
     * Starts the process of asking the server to send a new verification code
     * to the client so they can enter it on this fragment and update their account
     * to verified.
     *
     * @param theView the button that was pressed to initiate the code resend
     */
    private void sendNewVerificationCode(final View theView) {
        final RegisterVerificationFragmentArgs args =
                RegisterVerificationFragmentArgs.fromBundle(getArguments());
        mRegisterVerificationModel.sendResendCodeRequest(args.getEmail());
    }

    /**
     * Starts the chain validations that attempts to validate
     * the verification code, then sends a request to the server.
     *
     * @param theButton the button that was pressed to initiate the verification.
     */
    private void attemptVerification(final View theButton) {
        validateVerificationText();
    }

    /**
     * Attempts to validate the text inputted for the verification code.
     *
     * If the validation succeeds, initiates a request to the server.
     * Else, sets an error text on the validation field asking for a valid code.
     */
    private void validateVerificationText() {
        mCodeValidator.processResult(
                mCodeValidator.apply(mBinding.editVerificationCode.getText().toString().trim()),
                this::verifyCodeWithServer,
                result -> mBinding.editVerificationCode.setError("Enter a valid code"));
    }

    /**
     * Asynchronously attempts to verify the account in the server with the verification
     * code entered on the verification fragment.
     */
    private void verifyCodeWithServer() {
        final RegisterVerificationFragmentArgs args =
                RegisterVerificationFragmentArgs.fromBundle(getArguments());

        mRegisterVerificationModel.connect(args.getEmail(),
                mBinding.editVerificationCode.getText().toString().trim());
    }

    /**
     * Observes the HTTP Response from the web server. This observer should be
     * attached to RegisterVerificationViewModel.
     *
     * @param theResponse the Response from the server
     */
    private void observeResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                mBinding.editVerificationCode.setError("Verification failed.");
            } else {
                navigateToSignIn();
            }
        } else {
            Log.d("Registration Verification JSON Response", "No Response: "
                    + theResponse.toString());
        }
    }

    /**
     * Navigates the app to the home/landing page.
     */
    private void navigateToSignIn() {
        final RegisterVerificationFragmentArgs args =
                RegisterVerificationFragmentArgs.fromBundle(getArguments());

        RegisterVerificationFragmentDirections
                .ActionRegisterVerificationFragmentToSignInFragment directions =
                    RegisterVerificationFragmentDirections
                        .actionRegisterVerificationFragmentToSignInFragment();

        directions.setEmail(args.getEmail());
        directions.setPassword(args.getPassword());

        Navigation.findNavController(getView()).navigate(directions);

        // Clear the data stored in the view model to set up for
        // when the user comes back to this page.
        mRegisterVerificationModel.removeData();
    }
}