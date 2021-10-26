/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.auth.signin;

import static edu.uw.tcss450.group1project.utils.PasswordValidator.*;

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
import edu.uw.tcss450.group1project.utils.PasswordValidator;

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
    private FragmentSignInBinding binding;

    /** ViewModel used for sign in */
    private SignInViewModel mSignInModel;

    /**
     * A {@link PasswordValidator} dedicated to validating the user's inputted email
     * text on the sign in page.
     *
     * The email text will be considered valid if the length of the given text
     * is greater than 2, does not include whitespace, and includes the '@' character.
     */
    private PasswordValidator mEmailValidator = checkPwdLength(2)
            .and(checkExcludeWhiteSpace())
            .and(checkPwdSpecialChar("@"));

    /**
     * A {@link PasswordValidator} dedicated to validating the user's inputted password
     * text on the sign in page.
     *
     * The password is considered valid if the length of the given text
     * is greater than one does not include whitespace.
     */
    private PasswordValidator mPassWordValidator = checkPwdLength(1)
            .and(checkExcludeWhiteSpace());

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
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(theInflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(theView, savedInstanceState);

        binding.buttonToRegister.setOnClickListener(button ->
            Navigation.findNavController(getView()).navigate(
                    SignInFragmentDirections.actionLoginFragmentToRegisterFragment()
            ));

        binding.buttonSignIn.setOnClickListener(this::attemptSignIn);

        mSignInModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observeResponse);

        SignInFragmentArgs args = SignInFragmentArgs.fromBundle(getArguments());
        binding.editEmail.setText(args.getEmail().equals("default") ? "" : args.getEmail());
        binding.editPassword.setText(args.getPassword().equals("default") ? "" : args.getPassword());
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
        mEmailValidator.processResult(
                mEmailValidator.apply(binding.editEmail.getText().toString().trim()),
                this::validatePassword,
                result -> binding.editEmail.setError("Please enter a valid Email address."));
    }

    /**
     * Attempts to validate that the inputted password is valid.
     *
     * If the validation succeeds, verify all of the credentials with the server.
     * Else, sets an error on the first password field asking the user to input a valid password.
     */
    private void validatePassword() {
        mPassWordValidator.processResult(
                mPassWordValidator.apply(binding.editPassword.getText().toString()),
                this::verifyAuthWithServer,
                result -> binding.editPassword.setError("Please enter a valid Password."));
    }

    /**
     * Asynchronously attempts to sign into the app on the server with the information
     * given in the sign in text fields.
     */
    private void verifyAuthWithServer() {
        mSignInModel.connect(
                binding.editEmail.getText().toString(),
                binding.editPassword.getText().toString());
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
                    binding.editEmail.setError(
                            "Error Authenticating: " +
                                    theResponse.getJSONObject("data").getString("message"));
                } catch (JSONException exception) {
                    Log.e("JSON Parse Error", exception.getMessage());
                }
            } else {
                try {
                    navigateToSuccess(
                            binding.editEmail.getText().toString(),
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
