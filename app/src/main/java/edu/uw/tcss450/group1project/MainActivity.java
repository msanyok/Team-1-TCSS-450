/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.group1project.databinding.ActivityMainBinding;
import edu.uw.tcss450.group1project.model.NewMessageCountViewModel;
import edu.uw.tcss450.group1project.model.PushyTokenViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.services.PushReceiver;
import edu.uw.tcss450.group1project.ui.messages.ChatMessage;
import edu.uw.tcss450.group1project.ui.messages.ChatViewModel;

/**
 * A {@link AppCompatActivity} subclass that is responsible
 * for the main activities of the app.
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @author Chris Ding
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class MainActivity extends ThemedActivity {

    /** Used to receive push notifications from PUSHY */
    private MainPushMessageReceiver mPushMessageReceiver;

    /** Keeps track of the new messages */
    private NewMessageCountViewModel mNewMessageModel;

// todo: might need for navigation badges
//    private ActivityMainBinding mBinding;

    /**
     * The configuration for the bottom navigation displayed
     * on fragments in this activity
     */
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);

        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);


// todo: may need this for navigation badges
//        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(mBinding.getRoot());

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());
        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt()))
                        .get(UserInfoViewModel.class);

        applyTheme();
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_contacts,
                R.id.navigation_messages, R.id.navigation_weather)
                .build();
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(
                this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // handles the destination changes that occur in the app and what
        // should happen when it occurs
// todo: need to modify the if statement body so it works for different chat rooms.
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_chat_room) {
                mNewMessageModel.reset();
            }
        });

// todo: need to research and see if it will work with our theme
        // Handles the notification badge drawing
        mNewMessageModel.addMessageCountObserver(this, count -> {
Log.d("NEW MESSAGE CHANGE", "Count is: " + count);
//            BadgeDrawable badge = mBinding.navView.getOrCreateBadge(R.id.navigation_chat);
//            badge.setMaxCharacterCount(2);
//            if (count > 0) {
//                //new messages! update and show the notification badge.
//                badge.setNumber(count);
//                badge.setVisible(true);
//            } else {
//                //user did some action to clear the new messages, remove the badge
//                badge.clearNumber();
//                badge.setVisible(false);
//            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu theMenu) {
        getMenuInflater().inflate(R.menu.toolbar, theMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem theItem) {
        int id = theItem.getItemId();
        if (id == R.id.action_settings) {
            Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.navigation_settings);
            return true;
        }
        if (id == R.id.action_sign_out){
            signOut();
        }
        return super.onOptionsItemSelected(theItem);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Function of warning for deleting a contact using alert dialog
     *
     * @param theView the view to be assigned
     */
    public void showContactDeleteAlertDialog(final View theView) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>Deleting this contact " +
                "will be permanent. Are you sure?</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Delete</font>"),
                (dialog, which) -> {
                    //TODO add delete function in it
                    Toast.makeText(getApplicationContext(),"You have deleted this contact.",
                            Toast.LENGTH_SHORT).show();
        });
        alertDialog.setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReceiver, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
    }

    /**
     * A helper method for signout function.
     */
    private void signOut() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.signIn_keys_shared_prefs),
                        Context.MODE_PRIVATE);
        prefs.edit().remove(getString(R.string.signIn_keys_prefs_jwt)).apply();
        //End the app completely

        PushyTokenViewModel model = new ViewModelProvider(this)
                .get(PushyTokenViewModel.class);
        //when we hear back from the web service quit
        model.addResponseObserver(this, result -> finishAndRemoveTask());
        model.deleteTokenFromWebservice(
                new ViewModelProvider(this)
                        .get(UserInfoViewModel.class)
                        .getmJwt()
        );
    }


    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver
     *
     * @author Charles Bryan
     * @version Fall 2021
     */
    private class MainPushMessageReceiver extends BroadcastReceiver {

        /** View model that contains data about chat messages */
        private ChatViewModel mModel =
                new ViewModelProvider(MainActivity.this)
                        .get(ChatViewModel.class);

        @Override
        public void onReceive(final Context theContext, final Intent theIntent) {
            NavController navController =
                    Navigation.findNavController(
                            MainActivity.this, R.id.nav_host_fragment);
            NavDestination navDestination = navController.getCurrentDestination();

            // figure out what kind of pushy notification was sent, then do the corresponding tasks.
            if (theIntent.hasExtra("chatMessage")) {

                ChatMessage chatMessage = (ChatMessage) theIntent.getSerializableExtra("chatMessage");

                //If the user is not on the chat screen, update the
                // NewMessageCountView Model
                if (navDestination.getId() != R.id.navigation_chat_room) {
                    mNewMessageModel.increment();
                }
                //Inform the view model holding chatroom messages of the new
                //message.
                mModel.addMessage(theIntent.getIntExtra("chatid", -1), chatMessage);
            }
            // todo: add other kinds of pushy messages (more else if () options)
        }
    }

}
