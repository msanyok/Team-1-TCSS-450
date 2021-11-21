package edu.uw.tcss450.group1project.ui.auth.signin;

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

import java.util.Objects;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentEnterEmailBinding;
import edu.uw.tcss450.group1project.databinding.FragmentSignInBinding;
import edu.uw.tcss450.group1project.ui.auth.register.RegisterFragmentDirections;
import edu.uw.tcss450.group1project.ui.auth.register.RegisterViewModel;
import edu.uw.tcss450.group1project.utils.TextFieldHints;
import edu.uw.tcss450.group1project.utils.TextFieldValidators;

/**
 *
 */
public class EnterEmailFragment extends Fragment {

    private FragmentEnterEmailBinding mBinding;
    private EnterEmailViewModel mEnterEmailModel;

    public EnterEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        mBinding = FragmentEnterEmailBinding.inflate(theInflater);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        mBinding.buttonSentEmail.setOnClickListener(this::attemptSentEmail);
        mEnterEmailModel =
                new ViewModelProvider(this).get(EnterEmailViewModel.class);
        mEnterEmailModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);
    }

    private void attemptSentEmail(final View theButton){validateResetEmail();}

    private void validateResetEmail() {
        final String emailText = mBinding.editResetEmail.getText().toString().trim();
        TextFieldValidators.EMAIL_VALIDATOR.processResult(
                TextFieldValidators.EMAIL_VALIDATOR.apply(emailText),
                this::verifyResetEmailWithServer,
                result -> mBinding.editResetEmail.setError(TextFieldHints.getEmailHint(emailText)));
    }

    private void verifyResetEmailWithServer(){
        mEnterEmailModel.connect(mBinding.editResetEmail.getText().toString());
    }


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