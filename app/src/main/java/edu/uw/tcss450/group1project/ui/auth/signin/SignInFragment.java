/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.auth.signin;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.databinding.FragmentSignInBinding;
import edu.uw.tcss450.group1project.model.PushyTokenViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
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

    /** The view model that stores the Pushy token */
    private PushyTokenViewModel mPushyTokenViewModel;

    /** The view model that stores the user's information */
    private UserInfoViewModel mUserViewModel;


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
        mPushyTokenViewModel = new ViewModelProvider(getActivity())
                .get(PushyTokenViewModel.class);
    }

    @Override
    public View onCreateView(final LayoutInflater theInflater, final ViewGroup theContainer,
                             final Bundle theSavedInstanceState) {
        mBinding = FragmentSignInBinding.inflate(theInflater);

        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View theView,
                              @Nullable final Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);

        mBinding.buttonToRegister.setOnClickListener(button ->
            Navigation.findNavController(getView()).navigate(
                    SignInFragmentDirections.actionLoginFragmentToRegisterFragment()
            ));

        mBinding.buttonResetPassword.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        SignInFragmentDirections.actionSignInFragmentToEnterEmailFragment(
                        mBinding.editEmail.getText().toString()
                        )));


        mBinding.buttonSignIn.setOnClickListener(this::attemptSignIn);

        mSignInModel.clearResponse();
        mSignInModel.addResponseObserver(getViewLifecycleOwner(), this::observeResponse);

        SignInFragmentArgs args = SignInFragmentArgs.fromBundle(getArguments());
        mBinding.editEmail.setText(args.getEmail().equals("default") ? "" : args.getEmail());
        mBinding.editPassword.setText(
                args.getPassword().equals("default") ? "" : args.getPassword());

        // do not allow a sign in until the pushy token has been retrieved
        mPushyTokenViewModel.addTokenObserver(getViewLifecycleOwner(), token ->
                mBinding.buttonSignIn.setEnabled(!token.isEmpty()));

        // add observer to the pushy model so when a pushy token is available,
        // we observe that token's live data and use it
        mPushyTokenViewModel.addResponseObserver(
                getViewLifecycleOwner(),
                this::observePushyPutResponse);
    }

    /**
     * Check for the JWT and email in shared preferences.
     * If they exist, check to see if the JWT is still valid.
     * If it is, skip sign in with the server and navigate directly to MainActivity.
     */
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.signIn_keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (prefs.contains(getString(R.string.signIn_keys_prefs_jwt))) {
            String token = prefs.getString(getString(R.string.signIn_keys_prefs_jwt), "");
            JWT jwt = new JWT(token);
            // Check to see if the web token is still valid or not. To make a JWT expire after a
            // longer or shorter time period, change the expiration time when the JWT is
            // created on the web service.
            if(!jwt.isExpired(0)) {
                String email = jwt.getClaim("email").asString();
                navigateToSuccess(email, token);
                return;
            }
        }
    }

    /**
     * Helper to abstract the request to send the pushy token to the web service.
     * Called after the webservice has returned a successful verification of credentials
     */
    private void sendPushyToken() {
        mPushyTokenViewModel.sendTokenToWebservice(mUserViewModel.getJwt());
    }

    /**
     * An observer on the HTTP Response from the web server. This observer should be
     * attached to PushyTokenViewModel.
     *
     * @param theResponse the Response from the server
     */
    private void observePushyPutResponse(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (theResponse.has("code")) {
                //this error cannot be fixed by the user changing credentials...
                mBinding.editEmail.setError(
                        "Error Authenticating on Push Token. Please contact support");
            } else {
                navigateToSuccess(
                        mBinding.editEmail.getText().toString(),
                        mUserViewModel.getJwt()
                );
            }
        }
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
                result -> mBinding.editPassword.setError(
                        TextFieldHints.getPasswordHint(passwordText)));
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
     * Helper to abstract the navigation to the Activity past Authentication,
     * and save the JWT to Shared Preferences on successful sign in if switch is selected.
     *
     * @param theEmail the user's email
     * @param theJwt the JSON Web Token supplied by the server
     */
    private void navigateToSuccess(final String theEmail, final String theJwt) {
        //Save the JWT to Shared Preferences on successful sign in if switch is selected.
        if (mBinding.switchSignin.isChecked()) {
            final SharedPreferences prefs =
                    getActivity().getSharedPreferences(
                            getString(R.string.signIn_keys_shared_prefs),
                            Context.MODE_PRIVATE);
            //Store the credentials in SharedPrefs
            prefs.edit().putString(getString(R.string.signIn_keys_prefs_jwt), theJwt).apply();
        }

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
    private void navigateToRegistrationVerification(final String theEmail,
                                                    final String thePassword) {
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

                    final String message =
                            theResponse.getJSONObject("data").getString("message");
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
                    // successfully signed into the app. This is where we should
                    // create a new user info view model, send a new pushy token to the server,
                    // and navigate to the home page.
                    mUserViewModel = new ViewModelProvider(getActivity(),
                            new UserInfoViewModel.UserInfoViewModelFactory(
                                    mBinding.editEmail.getText().toString(),
                                    theResponse.getString("token")
                            )).get(UserInfoViewModel.class);

                    this.sendPushyToken();

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
