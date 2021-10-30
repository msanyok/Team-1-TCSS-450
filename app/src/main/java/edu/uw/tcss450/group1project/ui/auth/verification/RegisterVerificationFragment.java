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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentRegisterBinding;
import edu.uw.tcss450.group1project.databinding.FragmentVerificationBinding;
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
//        mRegisterModel.connect(
//                mBinding.editFirst.getText().toString(),
//                mBinding.editLast.getText().toString(),
//                mBinding.editNickname.getText().toString(),
//                mBinding.editEmail.getText().toString(),
//                mBinding.editPassword1.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    // HELLO AUSTN START WORKING ON MODEL CODE TO ACCESS SERVER...

    private void observeResponse(JSONObject jsonObject) {

    }







}