/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import edu.uw.tcss450.group1project.model.PushyTokenViewModel;
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

    @Override
    protected void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
//        applyTheme();

        setContentView(R.layout.activity_auth);
        Log.d("MAKE HERE?", "MAKE HERE");

        //If it is not already running, start the Pushy listening service
        Pushy.listen(this);

        initiatePushyTokenRequest();
    }

    /**
     * Requests the PushyViewModel to retrieve the Pushy token
     */
    private void initiatePushyTokenRequest() {
        new ViewModelProvider(this).get(PushyTokenViewModel.class).retrieveToken();
    }
}
