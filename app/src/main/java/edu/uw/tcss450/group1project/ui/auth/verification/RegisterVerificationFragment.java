/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.auth.verification;

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

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.group1project.databinding.FragmentVerificationBinding;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.ui.auth.register.RegisterFragmentDirections;
import edu.uw.tcss450.group1project.ui.auth.register.RegisterViewModel;
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
    private PasswordValidator mCodeValidator = PasswordValidator.checkPwdLength(5)
            .and(PasswordValidator.checkExcludeWhiteSpace());



    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mRegisterVerificationModel = new ViewModelProvider(getActivity())
                .get(RegisterVerificationViewModel.class);

    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        mBinding = FragmentVerificationBinding.inflate(theInflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);

        mBinding.buttonVerification.setOnClickListener(this::attemptVerification);
        mRegisterVerificationModel.addResponseObserver(getViewLifecycleOwner(),
                this::observeResponse);
    }


    /**
     * Starts the chain validations that attempts to validate
     * the verification code, then sends a request to the server.
     *
     * @param theButton the button that was pressed to initiate the verification.
     */
    private void attemptVerification(View theButton) {
        navigateToHomePage();
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
        UserInfoViewModel userInfoModel = new ViewModelProvider(getActivity())
                .get(UserInfoViewModel.class);
        final String userEmail = userInfoModel.getEmail();

        mRegisterVerificationModel.connect(userEmail,
                mBinding.editVerificationCode.getText().toString().trim());
        // Above call is async, don't add code below.
    }



    // TODO: NEED TO TEST THIS METHOD
    /**
     * Observes the HTTP Response from the web server. This observer should be
     * attached to RegisterVerificationViewModel.
     *
     * @param theResponse the Response from the server
     */
    private void observeResponse(JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                mBinding.editVerificationCode.setError("Verification failed.");
            } else {

                navigateToHomePage();
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }

    /**
     * Navigates the app to the home/landing page.
     */
    private void navigateToHomePage() {
        // TODO: NAVIGATE TO THE HOME/LANDING PAGE.
        // SEE THE LOGIN PAGE FOR TUTORIAL...?
        Navigation.findNavController(getView()).navigate(
                RegisterVerificationFragmentDirections.actionRegisterVerificationFragmentToSignInFragment());
    }



}