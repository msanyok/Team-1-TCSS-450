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
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.group1project.model.ContactNotificationViewModel;
import edu.uw.tcss450.group1project.model.IsTypingViewModel;
import edu.uw.tcss450.group1project.model.LocalStorageUtils;
import edu.uw.tcss450.group1project.model.LocationViewModel;
import edu.uw.tcss450.group1project.model.NewMessageCountViewModel;
import edu.uw.tcss450.group1project.model.PushyTokenViewModel;
import edu.uw.tcss450.group1project.model.UserInfoViewModel;
import edu.uw.tcss450.group1project.services.PushReceiver;
import edu.uw.tcss450.group1project.ui.contacts.ContactRequestViewModel;
import edu.uw.tcss450.group1project.ui.contacts.ContactsParentFragment;
import edu.uw.tcss450.group1project.ui.contacts.ContactsViewModel;
import edu.uw.tcss450.group1project.ui.messages.ChatMessage;
import edu.uw.tcss450.group1project.ui.messages.ChatViewModel;
import edu.uw.tcss450.group1project.ui.messages.ChatsListViewModel;

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
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 50000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /** A constant int for the permissions request code. Must be a 16 bit number. */
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;

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

    /** Keeps track of the new message notifications */
    private NewMessageCountViewModel mNewMessageModel;

    /** Keeps track of the new contacts notifications (new contact, new contact req sent/received */
    private ContactNotificationViewModel mContactTabNewCountViewModel;

    /** Keeps track of the new contact requests */
    private ContactRequestViewModel mContactRequestViewModel;

    /** Keeps track of the the list of chats */
    private ChatsListViewModel mChatListViewModel;

    /** Keeps track of contacts */
    private ContactsViewModel mContactsViewModel;

    /** Keeps track of typing actions */
    private IsTypingViewModel mTypingModel;

    /**
     * The configuration for the bottom navigation displayed
     * on fragments in this activity
     */
    private AppBarConfiguration mAppBarConfiguration;

    /** The user info view model that stores the current user's information */
    private UserInfoViewModel mUserInfoModel;

    /** The color used for notification badges */
    private int mBadgeColor;

    /** The color used for notification badge text */
    private int mBadgeTextColor;

    @Override
    protected void onCreate(final Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);


        //onNewIntent(getIntent());

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

        // set up all of the view models
        mLocationModel =
                new ViewModelProvider(MainActivity.this).get(LocationViewModel.class);
        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);
        mContactTabNewCountViewModel =
                new ViewModelProvider(this).get(ContactNotificationViewModel.class);
        mUserInfoModel = new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getJwt()))
                        .get(UserInfoViewModel.class);
        mContactRequestViewModel =
                new ViewModelProvider(this).get(ContactRequestViewModel.class);
        mContactsViewModel = new ViewModelProvider(this).get(ContactsViewModel.class);
        mChatListViewModel = new ViewModelProvider(this).get(ChatsListViewModel.class);
        mTypingModel = new ViewModelProvider(this).get(IsTypingViewModel.class);

        applyTheme();
        setContentView(R.layout.activity_main);
        int[] attr = { R.attr.colorAccent, R.attr.buttonTextColor };
        TypedArray tA = obtainStyledAttributes(attr);
        mBadgeColor = tA.getResourceId(0, R.color.rose);
        mBadgeTextColor = tA.getResourceId(1, R.color.white);
        tA.recycle();

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
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (id == R.id.navigation_home ||
                    id == R.id.navigation_contacts_parent ||
                    id == R.id.navigation_messages ||
                    id == R.id.navigation_weather_parent) {
                navView.setVisibility(View.VISIBLE);
            } else {
                navView.setVisibility(View.GONE);
            }
            if (id == R.id.navigation_chat_room_info || id == R.id.navigation_contacts_parent ||
                id == R.id.navigation_password_change || id == R.id.navigation_messages ||
                id == R.id.navigation_weather_location_selection) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        });

        // Handles the notification badge drawing for new messages
        mNewMessageModel.addMessageCountObserver(this, count -> {
            BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_messages);
            badge.setMaxCharacterCount(2);
            badge.setBackgroundColor(getResources().getColor(mBadgeColor, getTheme()));
            badge.setBadgeTextColor(getResources().getColor(mBadgeTextColor, getTheme()));

            if (count > 0) {
                // new messages! update and show the notification badge.
                badge.setNumber(count);
                badge.setVisible(true);
            } else {
                // user did some action to clear the new messages, remove the badge
                badge.clearNumber();
                badge.setVisible(false);
            }
        });

        // Handles the notification badge drawing for contacts, note: we need to observe both
        // contact requests and contacts live data and do the same action when one changes
        mContactTabNewCountViewModel.addContactRequestNotifObserver(this, set -> {
            setContactsNotificationBadge(navView);
        });
        mContactTabNewCountViewModel.addContactsNotifObserver(this, set -> {
            setContactsNotificationBadge(navView);
        });

        // check for locations permissions
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

    /**
     * Sets the badges on the bottom navigation for the contacts
     * @param theNavView the bottom navigation
     */
    private void setContactsNotificationBadge(final BottomNavigationView theNavView) {
        int totalCount = mContactTabNewCountViewModel.getTotalContactsNotificationCount();
        BadgeDrawable badge = theNavView.getOrCreateBadge(R.id.navigation_contacts_parent);
        badge.setMaxCharacterCount(2);
        badge.setBackgroundColor(getResources().getColor(mBadgeColor, getTheme()));
        badge.setBadgeTextColor(getResources().getColor(mBadgeTextColor, getTheme()));
        if (totalCount > 0) {
            badge.setNumber(totalCount);
            badge.setVisible(true);
        } else {
            badge.clearNumber();
            badge.setVisible(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int theRequestCode,
                                           @NonNull final String[] thePermissions,
                                           @NonNull final int[] theGrantResults) {
        super.onRequestPermissionsResult(theRequestCode, thePermissions, theGrantResults);
        if (theRequestCode == MY_PERMISSIONS_LOCATIONS) {
            if (theGrantResults.length > 0
                    && theGrantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
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
                    .addOnSuccessListener(this, theLoc -> {
                        if (theLoc != null) {
                            mLocationModel.setLocation(theLoc);
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
                    .navigate(R.id.action_global_navigation_settings);
            return true;
        }
        if (id == R.id.action_sign_out){
            displaySignOutDialog();
        }
        return super.onOptionsItemSelected(theItem);
    }

    /**
     * Displays a dialog to check if the user really wants to sign out
     */
    private void displaySignOutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(Html.fromHtml("<font color='#000000'>" +
                "Are you sure you want to sign out?" + "</font>"));
        alertDialog.setPositiveButton(Html.fromHtml("<font color='000000'>Ok</font>"),
                (dialog, which) -> signOut());
        alertDialog.setNegativeButton(Html.fromHtml("<font color='000000'>Cancel</font>"),
                (dialog, which) -> {});
        alertDialog.show();
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

        // get the notifications that occurred while the app was not in the foreground
        mNewMessageModel.putData(LocalStorageUtils.getMissedMessages(this));
        mContactTabNewCountViewModel.
                putContactsData(LocalStorageUtils.getContactsNotifications(this));
        mContactTabNewCountViewModel.
                putContactRequestData(LocalStorageUtils.
                        getContactRequestNotifications(this));

        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.NEW_PUSHY_NOTIF);
        registerReceiver(mPushMessageReceiver, iFilter);
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null){
            unregisterReceiver(mPushMessageReceiver);
        }
        stopLocationUpdates();
    }

    /**
     * A helper method for sign-out function.
     */
    private void signOut() {

        // remove the locally stored notification data
        LocalStorageUtils.clearAllStoredNotifications(this);

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
        private ChatViewModel mChatMessageViewModel =
                new ViewModelProvider(MainActivity.this)
                        .get(ChatViewModel.class);

        @Override
        public void onReceive(final Context theContext, final Intent theIntent) {

            final String type = theIntent.getStringExtra("type");

            Log.d("RECEIVE INTENT", "Type: " + type);

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
            } else if (type.equals(PushReceiver.TYPING)) {
                completeNewTypingActions(theContext, theIntent);
            }
        }

        /**
         * Handles updating the devices view model when a message is received.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewMessageActions(final Context theContext, final Intent theIntent) {

            ChatMessage chatMessage =
                    (ChatMessage) theIntent.getSerializableExtra("chatMessage");

            // We will always update the new message view model because we
            // want to receive messages from other chats even if we are on
            // the chat fragment for a different chat.
            // Thus, we ensure to DELETE the added new message in the new message observer
            // inside the chat fragment. This will occur as a result of
            // calling addMessage on the chat message view model.

            int chatId = theIntent.getIntExtra("chatid", -1);
            mNewMessageModel.increment(chatId);
            mChatListViewModel.getChatListData(mUserInfoModel.getJwt());

            // Inform the view model holding chatroom messages of the new
            // message.
            mChatMessageViewModel.addMessage(
                    theIntent.getIntExtra("chatid", -1), chatMessage);
        }

        /**
         * Handles updating the devices contact requests when a request is received or sent.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewContactRequestActions(final Context theContext,
                                                      final Intent theIntent) {
            // notify that there is a new contact request
            mContactRequestViewModel.allContactRequests(mUserInfoModel.getJwt());

            // if the user is not on the sent/received requests page,
            // update the new count view model
            final NavController navController =
                    Navigation.findNavController(
                            MainActivity.this, R.id.nav_host_fragment);
            final NavDestination navDestination = navController.getCurrentDestination();

            boolean onFragment = false;
            if (navDestination.getId() == R.id.navigation_contacts_parent) {

                final Fragment navHostFragment =
                        getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                String tabString =
                        ((ContactsParentFragment) navHostFragment.getChildFragmentManager().
                                getFragments().get(0)).getCurrentTabString();
                onFragment = tabString.equals(ContactsParentFragment.REQUESTS);
            }

            final String fromNickname = theIntent.getStringExtra("fromNickname");
            boolean isARequestWeSent = fromNickname.equals(mUserInfoModel.getNickname());
            if (!onFragment && !isARequestWeSent) {
                Log.d("", "ADDING CONTACT REQUEST FROM " + fromNickname);
                mContactTabNewCountViewModel.addContactRequestNotification(fromNickname);
            }
        }

        /**
         * Handles updating the devices contacts and contacts
         * requests when a request is accepted/declined.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewContactRequestResponseActions(final Context theContext,
                                                              final Intent theIntent) {
            Log.d("RECIEVE INTENT", "New Contact Request Response Actions");
            // update the contact request list
            mContactRequestViewModel.allContactRequests(mUserInfoModel.getJwt());

            // update the contacts list for both users if the contact request is accepted
            if (theIntent.getBooleanExtra("isAccept", false)) {
                //update the user's list of contacts
                mContactsViewModel.contactsConnect(mUserInfoModel.getJwt());

                // if the user is not on the contacts fragment we want to make a notification
                // for that tab and bottom navigation
                final NavController navController =
                        Navigation.findNavController(
                                MainActivity.this, R.id.nav_host_fragment);
                final NavDestination navDestination = navController.getCurrentDestination();

                boolean onFragment = false;
                if (navDestination.getId() == R.id.navigation_contacts_parent) {

                    final Fragment navHostFragment =
                            getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                    String tabString =
                            ((ContactsParentFragment) navHostFragment.getChildFragmentManager().
                                    getFragments().get(0)).getCurrentTabString();
                    onFragment = tabString.equals(ContactsParentFragment.ALL_CONTACTS);
                }

                if (!onFragment) {
                    // store the nickname of the user who just added you as a contact
                    String otherNickname = theIntent.getStringExtra("fromNickname");
                    if (otherNickname.equals(mUserInfoModel.getNickname())) {
                        otherNickname = theIntent.getStringExtra("fromNickname");
                    }
                    mContactTabNewCountViewModel.addContactsNotification(otherNickname);
                }

            }
        }

        /**
         * Handles updating the devices contacts when a delete request is recieved.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewContactDeleteActions(final Context theContext,
                                                     final Intent theIntent) {
            mContactsViewModel.contactsConnect(mUserInfoModel.getJwt());
        }

        /**
         * Handles updating the devices outgoing contact requests when someone deletes a
         * contact request they sent you
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewContactRequestDeleteActions(final Context theContext,
                                                            final Intent theIntent) {
            Log.d("RECEIVE INTENT", "New Contact Request Delete Actions");
            mContactRequestViewModel.allContactRequests(mUserInfoModel.getJwt());

            // remove any contact request notification from this user if one exists
            mContactTabNewCountViewModel.
                    removeContactRequestNotification(getApplicationContext(),
                            theIntent.getStringExtra("fromNickname"));
        }

        /**
         * Handles updating the devices typing timers when typing notification is received.
         *
         * @param theContext the context of the application
         * @param theIntent the Intent that stores the Pushy payload
         */
        private void completeNewTypingActions(final Context theContext,
                                              final Intent theIntent) {
            Log.d("RECEIVE INTENT", "New Typing Actions");

            if (theIntent.getBooleanExtra("isTyping", false)) {
                // a notification came that tells us a user is typing
                mTypingModel.putTyping(theIntent.getIntExtra("chatId", 0),
                        theIntent.getStringExtra("nickname"));
            } else {
                // a notification came that tells us a user has stopped typing
                mTypingModel.stopTyping(theIntent.getIntExtra("chatId", 0),
                        theIntent.getStringExtra("nickname"));
            }
        }
    }

    @Override
    protected void onNewIntent(final Intent theIntent) {
        super.onNewIntent(theIntent);

        if (theIntent.hasExtra("newContact")) {
            Navigation.findNavController(
                    this, R.id.nav_host_fragment).navigate(R.id.action_contacts_global);

        } else if (theIntent.hasExtra("chatId")) {
            MainGraphDirections.ActionChatroomGlobal actionNavigation =
                    MainGraphDirections.actionChatroomGlobal(theIntent.getStringExtra("chatName"),
                    String.valueOf(theIntent.getIntExtra("chatId", -1)));

            Navigation.findNavController(
                    this, R.id.nav_host_fragment).navigate(actionNavigation);

        }

    }

}
