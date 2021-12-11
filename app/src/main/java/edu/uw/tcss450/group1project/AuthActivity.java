/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.auth0.android.jwt.JWT;

import edu.uw.tcss450.group1project.model.PushyTokenViewModel;
import edu.uw.tcss450.group1project.ui.auth.signin.SignInFragmentDirections;
import me.pushy.sdk.Pushy;

/**
 * A {@link AppCompatActivity} subclass that is responsible
 * for actions regarding authorization including registration, sign
 * in, and account validation.
 *
 * @author Parker Rosengreen
 * @author Austn Attaway
 * @version Fall 2021
 */
public class AuthActivity extends ThemedActivity {
    /**
     * To keep track of the application bar configuration
     */
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        applyTheme();   // todo: does this change anything?

        setContentView(R.layout.activity_auth);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_sign_in).build();
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController( this, navController, mAppBarConfiguration);

        //If it is not already running, start the Pushy listening service
        Pushy.listen(this);
        initiatePushyTokenRequest();
        onNewIntent(getIntent());


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Requests the PushyViewModel to retrieve the Pushy token
     */
    private void initiatePushyTokenRequest() {
        new ViewModelProvider(this).get(PushyTokenViewModel.class).retrieveToken();
    }


    @Override
    protected void onNewIntent(final Intent theIntent) {
        super.onNewIntent(theIntent);
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.signIn_keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (prefs.contains(getString(R.string.signIn_keys_prefs_jwt))) {
            String token = prefs.getString(getString(R.string.signIn_keys_prefs_jwt), "");
            JWT jwt = new JWT(token);
            // Check to see if the web token is still valid or not. To make a JWT expire after a
            // longer or shorter time period, change the expiration time when the JWT is
            // created on the web service.
            if (!jwt.isExpired(0)) {
                //Create the new intent to be sent to MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                // Jwt to check if they are verified
                intent.putExtra("jwt", token);
                //checks what type of notification intent we received
                if (theIntent.hasExtra("newContact")) {
                    intent.putExtras(theIntent.getExtras());
                } else if (theIntent.hasExtra("chatId")) {
                    intent.putExtras(theIntent.getExtras());
                }
                startActivity(intent);
                return;
            }
        }

    }

}
