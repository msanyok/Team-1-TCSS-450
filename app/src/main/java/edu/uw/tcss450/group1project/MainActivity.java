/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.group1project.ui.contacts.ContactRequestViewModel;
import edu.uw.tcss450.group1project.model.LocationViewModel;
import edu.uw.tcss450.group1project.model.NewMessageCountViewModel;
import edu.uw.tcss450.group1project.model.PushyTokenViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.services.PushReceiver;
import edu.uw.tcss450.group1project.ui.contacts.ContactsViewModel;
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
 * @author Steven Omegna
 * @version Fall 2021
 */
public class MainActivity extends ThemedActivity {

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /** A constant int for the permissions request code. Must be a 16 bit number. */
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;

    /** A Channel ID for app notifications*/
    private static final String CHANNEL_ID = "9898983213213421321";

    /** The location request */
    private LocationRequest mLocationRequest;

    /** Use a FusedLocationProviderClient to request the location */
    private FusedLocationProviderClient mFusedLocationClient;

    /** Will use this call back to decide what to do when a location change is detected */
    private LocationCallback mLocationCallback;

    /** The ViewModel that will store the current location */
    private LocationViewModel mLocationModel;

    /** Used to receive push notifications from PUSHY */
    private MainPushMessageReceiver mPushMessageReceiver;

    /** Keeps track of the new messages */
    private NewMessageCountViewModel mNewMessageModel;

    /** Keeps track of the new contact requests */
    private ContactRequestViewModel mContactRequestViewModel;

    /** Keeps track of contacts */
    private ContactsViewModel mContactsViewModel;

// todo: might need for navigation badges
//    private ActivityMainBinding mBinding;

    /**
     * The configuration for the bottom navigation displayed
     * on fragments in this activity
     */
    private AppBarConfiguration mAppBarConfiguration;

    /** The user info view model that stores the current user's information */
    private UserInfoViewModel mUserInfoModel;

    @Override
    protected void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mLocationModel =
                new ViewModelProvider(MainActivity.this).get(LocationViewModel.class);
        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);

// todo: may need this for navigation badges
//        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(mBinding.getRoot());

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

        // set up all of the view models
        mUserInfoModel = new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getJwt()))
                        .get(UserInfoViewModel.class);
        mContactRequestViewModel = new ViewModelProvider(this).get(ContactRequestViewModel.class);
        mContactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);


        applyTheme();
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_contacts_parent,
                R.id.navigation_messages, R.id.navigation_weather_parent)
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            requestLocation();
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull final LocationResult theLocResult) {
                for (Location location : theLocResult.getLocations()) {
                    mLocationModel.setLocation(location);
                }
            };
        };
        createLocationRequest();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] thePermissions,
                                           @NonNull final int[] theGrantResults) {
        super.onRequestPermissionsResult(requestCode, thePermissions, theGrantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                if (theGrantResults.length > 0
                        && theGrantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
                }
            }
        }
    }

    /**
     * Requests devices location for weather
     */
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("REQUEST LOCATION",
                    "User did NOT allow permission to request location!");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location theLoc) {
                            if (theLoc != null) {
                                mLocationModel.setLocation(theLoc);
                            }
                        }
                    });
        }
    }

    /**
     * Create and configure a Location Request used when retrieving location updates
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
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
     * Displays an error dialog with a custom message to the user
     *
     * @param theMessage the custom message
     */
    public void displayErrorDialog(final String theMessage) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>" +
                theMessage + "</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Ok</font>"),
                (dialog, which) -> {});
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.NEW_PUSHY_NOTIF);
        registerReceiver(mPushMessageReceiver, iFilter);
        stopLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
        startLocationUpdates();
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
                        .getJwt()
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

            final String type = theIntent.getStringExtra("type");

            Log.d("RECIEVE INTENT", "Type: " + type);

            // figure out what kind of pushy notification was sent, then do the corresponding tasks.
            if (type.equals(PushReceiver.NEW_MESSAGE)) {
                completeNewMessageActions(theContext, theIntent);
            } else if (type.equals(PushReceiver.NEW_CONTACT_REQUEST)) {
                completeNewContactRequestActions(theContext, theIntent);
            } else if (type.equals(PushReceiver.CONTACT_REQUEST_RESPONSE)) {
                completeNewContactRequestResponseActions(theContext, theIntent);
            } else if (type.equals(PushReceiver.CONTACT_DELETE)) {
                completeNewContactDeleteActions(theContext, theIntent);
            } else if (type.equals(PushReceiver.CONTACT_REQUEST_DELETE)) {
                completeNewContactRequestDeleteActions(theContext, theIntent);
            }

        }

        /**
         * Handles updating the devices viewmodel when a message is received.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewMessageActions(final Context theContext, final Intent theIntent) {
            Log.d("RECIEVE INTENT", "New Message Actions");

            NavController navController =
                Navigation.findNavController(
                        MainActivity.this, R.id.nav_host_fragment);
            NavDestination navDestination = navController.getCurrentDestination();

            ChatMessage chatMessage =
                    (ChatMessage) theIntent.getSerializableExtra("chatMessage");

            //If the user is not on the chat screen, update the
            // NewMessageCountView Model
            if (navDestination.getId() != R.id.navigation_chat_room) {
                mNewMessageModel.increment();
            }

            //Inform the view model holding chatroom messages of the new
            //message.
            mModel.addMessage(theIntent.getIntExtra("chatid", -1), chatMessage);

        }

        /**
         * Handles updating the devices contact requests when a request is received or sent.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewContactRequestActions(final Context theContext, final Intent theIntent) {

            // todo: need to implement on screen/off screen functionality with in app notifications
            Log.d("RECIEVE INTENT", "New Contact Request Actions");

//            NavController navController =
//                Navigation.findNavController(
//                        MainActivity.this, R.id.nav_host_fragment);
//            NavDestination navDestination = navController.getCurrentDestination();
//
//            final String memberId = mUserInfoModel.getMemberId();
//            final String fromId = theIntent.getStringExtra("fromId");
            //update the contacts viewmodel
            mContactRequestViewModel.allContactRequests(mUserInfoModel.getJwt());

        }
        /**
         * Handles updating the devices contacts and contacts
         * requests when a request is accepted/declined.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewContactRequestResponseActions(final Context theContext, final Intent theIntent) {
            // todo: need to implement on screen/off screen functionality with in app notifications
            Log.d("RECIEVE INTENT", "New Contact Request Response Actions");

            NavController navController =
                    Navigation.findNavController(
                            MainActivity.this, R.id.nav_host_fragment);
            NavDestination navDestination = navController.getCurrentDestination();

            final String memberId = mUserInfoModel.getMemberId();
            final String fromId = theIntent.getStringExtra("fromId");

            if (memberId.equals(fromId)) {

                // we are the member who sent the request, so update our sent requests list
                // todo:
            } else {
                // we are the member who accepted/rejected the request, so update our recieved requests list
                mContactRequestViewModel.allContactRequests(mUserInfoModel.getJwt());
            }

            // update the contacts list for both users if the contact request is accepted
            if (theIntent.getBooleanExtra("isAccept", false)) {
                mContactsViewModel.contactsConnect(mUserInfoModel.getJwt());
            }

        }
        /**
         * Handles updating the devices contacts when a delete request is recieved.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewContactDeleteActions(final Context theContext, final Intent theIntent) {
            Log.d("RECIEVE INTENT", "New Contact Delete Actions");

            mContactsViewModel.contactsConnect(mUserInfoModel.getJwt());

            // todo: offscreen in app notifs? perhaps not.

        }

        /**
         * Handles updating the devices outgoing contact requests when a delete request is recieved.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewContactRequestDeleteActions(final Context theContext, final Intent theIntent) {
            Log.d("RECIEVE INTENT", "New Contact Request Delete Actions");

            mContactRequestViewModel.allContactRequests(mUserInfoModel.getJwt());

            // todo: offscreen in app notifs? perhaps not.

        }

    }

}
