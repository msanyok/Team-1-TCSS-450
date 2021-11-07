/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

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

import edu.uw.tcss450.group1project.databinding.FragmentSignInBinding;
import edu.uw.tcss450.group1project.ui.auth.verification.RegisterVerificationFragmentDirections;
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
public class SignInFragment extends Fragment {

    /** ViewBinding reference to the Sign in Fragment UI */
    private FragmentSignInBinding mBinding;

    /** ViewModel used for sign in */
    private SignInViewModel mSignInModel;

    /**
     * Empty public constructor. Does not provide any functionality.
     */
    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mSignInModel = new ViewModelProvider(getActivity())
                .get(SignInViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle savedInstanceState) {
        mBinding = FragmentSignInBinding.inflate(theInflater);

        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(theView, savedInstanceState);

        mBinding.buttonToRegister.setOnClickListener(button ->
            Navigation.findNavController(getView()).navigate(
                    SignInFragmentDirections.actionLoginFragmentToRegisterFragment()
            ));

        mBinding.buttonSignIn.setOnClickListener(this::attemptSignIn);

        mSignInModel.clearResponse();
        mSignInModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        SignInFragmentArgs args = SignInFragmentArgs.fromBundle(getArguments());
        mBinding.editEmail.setText(args.getEmail().equals("default") ? "" : args.getEmail());
        mBinding.editPassword.setText(
                args.getPassword().equals("default") ? "" : args.getPassword());
    }

    /**
     * Starts the chain of text field validation that attempts to validate
     * all sign in text input fields.
     *
     * @param theButton the Button that was pressed to invoke this method.
     */
    private void attemptSignIn(final View theButton) {
        validateEmail();
    }

    /**
     * Attempts to validate the text inputted for the user's email.
     *
     * If the validation succeeds, attempts to validate the password.
     * Else, sets an error text on the email field that requests they enter a valid email.
     */
    private void validateEmail() {
        final String emailText = mBinding.editEmail.getText().toString().trim();
        TextFieldValidators.EMAIL_VALIDATOR.processResult(
                TextFieldValidators.EMAIL_VALIDATOR.apply(emailText),
                this::validatePassword,
                result -> mBinding.editEmail.setError(TextFieldHints.getEmailHint(emailText)));
    }

    /**
     * Attempts to validate that the inputted password is valid.
     *
     * If the validation succeeds, verify all of the credentials with the server.
     * Else, sets an error on the first password field asking the user to input a valid password.
     */
    private void validatePassword() {
        final String passwordText = mBinding.editPassword.getText().toString();
        TextFieldValidators.PASSWORD_VALIDATOR.processResult(
                TextFieldValidators.PASSWORD_VALIDATOR.apply(passwordText),
                this::verifyAuthWithServer,
                result -> mBinding.editPassword.setError(TextFieldHints.getPasswordHint(passwordText)));
    }

    /**
     * Asynchronously attempts to sign into the app on the server with the information
     * given in the sign in text fields.
     */
    private void verifyAuthWithServer() {
        mSignInModel.connect(
                mBinding.editEmail.getText().toString(),
                mBinding.editPassword.getText().toString());
        //This is an Asynchronous call. No statements after should rely on the
        //result of connect().
    }

    /**
     * Helper to abstract the navigation to the Activity past Authentication.
     *
     * @param theEmail the user's email
     * @param theJwt the JSON Web Token supplied by the server
     */
    private void navigateToSuccess(final String theEmail, final String theJwt) {
        Navigation.findNavController(getView())
                        .navigate(SignInFragmentDirections
                        .actionLoginFragmentToMainActivity(theEmail, theJwt));
        getActivity().finish();
    }

    /**
     * Helper to abstract the navigation to the registration verification fragment.
     *
     * @param theEmail the user's email
     * @param thePassword the user's password
     */
    private void navigateToRegistrationVerification(final String theEmail, final String thePassword) {
        Navigation.findNavController(getView())
                .navigate(SignInFragmentDirections
                .actionSignInFragmentToRegisterVerificationFragment(theEmail, thePassword));
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

                    final String message = theResponse.getJSONObject("data").getString("message");
                    if (message.equals("Email is not verified")) {
                        // received message that the given account exists,
                        // but is not registered yet. Therefore navigate to the
                        // verification code page.
                        navigateToRegistrationVerification(
                                mBinding.editEmail.getText().toString(),
                                mBinding.editPassword.getText().toString());

                    } else {
                        // other error occurred unrelated to an invalid account
                        mBinding.editEmail.setError(
                                "Error Authenticating: " +
                                        theResponse.getJSONObject("data").getString("message"));
                    }
                } catch (JSONException exception) {
                    Log.e("JSON Parse Error", exception.getMessage());
                }
            } else {
                try {
                    navigateToSuccess(
                            mBinding.editEmail.getText().toString(),
                            theResponse.getString("token")
                    );
                } catch (JSONException e) {
                    Log.e("JSON Parse Error", e.getMessage());
                }
            }
        } else {
            Log.d("JSON Response", "No Response");
        }
    }
}
