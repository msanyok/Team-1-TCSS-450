/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.services;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;

import edu.uw.tcss450.group1project.AuthActivity;
import edu.uw.tcss450.group1project.R;
import edu.uw.tcss450.group1project.model.LocalStorageUtils;
import edu.uw.tcss450.group1project.ui.messages.ChatMessage;
import me.pushy.sdk.Pushy;

/**
 * PushReceiver is a class for accepting push notifications from Pushy
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @author Steven Omegna
 * @version Fall 2021
 */
public class PushReceiver extends BroadcastReceiver {

    /** The String that notifies a new notification has been received from pushy  */
    public static final String NEW_PUSHY_NOTIF = "newMessageFromPushy";

    /** The String that notifies a message has been sent or received from pushy */
    public static final String NEW_MESSAGE = "msg";

    /** The String that notifies a new contact request has been sent or received from pushy  */
    public static final String NEW_CONTACT_REQUEST = "newContactRequest";

    /** The String that notifies a contact request response has occurred from pushy */
    public static final String CONTACT_REQUEST_RESPONSE = "contactRequestResponse";

    /** The String that notifies a contact was deleted from pushy */
    public static final String CONTACT_DELETE = "contactDeleted";

    /** The String that notifies a contact request was deleted from pushy */
    public static final String CONTACT_REQUEST_DELETE = "contactRequestDeleted";

    /** The String that notifies a typing message came from pushy */
    public static final String TYPING = "typing";

    /** The ID for the channel used for notifications */
    private static final String CHANNEL_ID = "1";

    /**
     * Handles what should occur when this device receives a Pushy payload.
     *
     * @param theContext the context of the application
     * @param theIntent the Intent that stores the Pushy payload
     */
    @Override
    public void onReceive(final Context theContext, final Intent theIntent) {

        // the type of message will determine the type of intent we create
        final String typeOfMessage = theIntent.getStringExtra("type");

        if (typeOfMessage.equals("msg")) {
            acceptMessagePushy(theContext, theIntent);
        } else if (typeOfMessage.equals(NEW_CONTACT_REQUEST)) {
            acceptNewContactRequestPushy(theContext, theIntent);
        } else if (typeOfMessage.equals(CONTACT_REQUEST_RESPONSE)) {
            acceptContactRequestResponsePushy(theContext, theIntent);
        } else if (typeOfMessage.equals(CONTACT_DELETE)) {
            acceptContactDeletePushy(theContext, theIntent);
        } else if (typeOfMessage.equals(CONTACT_REQUEST_DELETE)) {
            acceptContactRequestDeletePushy(theContext, theIntent);
        } else if (typeOfMessage.equals(TYPING)) {
            acceptTypingPushy(theContext, theIntent);
        } else {
            // unexpected pushy
            Log.d("PUSH RECEIVE", "UNEXPECTED PUSHY RECEIVED");
        }
    }


    /**
     * Handles what should occur when this device receives a message from a Pushy payload.
     *
     * @param theContext the context of the application
     * @param theIntent the Intent that stores the Pushy payload
     */
    private void acceptMessagePushy(final Context theContext, final Intent theIntent) {
        ChatMessage message = null;
        int chatId = -1;

        try {
            message = ChatMessage.createFromJsonString(theIntent.getStringExtra("message"));
            chatId = theIntent.getIntExtra("chatid", -1);
        } catch (JSONException exception) {
            //Web service sent us something unexpected...I can't deal with this.
            throw new IllegalStateException("Error from Web Service. Contact Dev Support");
        }

        // get tools to check if the user is in the app or not
        ActivityManager.RunningAppProcessInfo appProcessInfo =
                new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        // do a particular type of notification depending
        // on the state of the user in or outside the app
        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            // the user is inside the application, so send an intent to the MainActivity
            // create an Intent to broadcast a message to other parts of the app.
            Intent intent = new Intent(NEW_PUSHY_NOTIF);
            intent.putExtra("type", NEW_MESSAGE);
            intent.putExtra("chatMessage", message);
            intent.putExtra("chatid", chatId);
            intent.putExtras(theIntent.getExtras());
            theContext.sendBroadcast(intent);

        } else {
            // the user is not inside the application, so send a notification
            // set up the intent
            Intent intent = new Intent(theContext, AuthActivity.class);
            intent.putExtra("chatId", chatId);
            intent.putExtra("chatName", theIntent.getStringExtra("chatName"));
            intent.putExtras(theIntent.getExtras());
            PendingIntent pendingIntent = PendingIntent.getActivity(theContext, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Build notification
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(theContext, CHANNEL_ID)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_android_black_24dp)
                            .setContentTitle("Message from: " + message.getSender())
                            .setContentText(message.getMessage())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent);

            // Automatically configure a ChatMessageNotification Channel
            Pushy.setNotificationChannel(builder, theContext);

            // Get an instance of the NotificationManager service
            NotificationManager notificationManager =
                    (NotificationManager)
                            theContext.getSystemService(theContext.NOTIFICATION_SERVICE);

            // Build the notification and display it
            notificationManager.notify(1, builder.build());
        }

        // insert the new message into internal storage so if the user navigates closes the app,
        // the saved notification data is not lost. Note: we only want to store the
        // missed messages if we are not in the chat fragment.
        LocalStorageUtils.putMissedMessage(theContext, String.valueOf(chatId));
    }

    /**
     * Handles when this device receives a contact request from a Pushy payload.
     *
     * @param theContext the context of the application
     * @param theIntent the Intent that stores the Pushy payload
     */
    private void acceptNewContactRequestPushy(final Context theContext, final Intent theIntent) {
        String toId = theIntent.getStringExtra("toId");
        String fromId = theIntent.getStringExtra("fromId");
        String fromNickname = theIntent.getStringExtra("fromNickname");

        // get tools to check if the user is in the app or not
        ActivityManager.RunningAppProcessInfo appProcessInfo =
                new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        // store the notification locally so it can be loaded when the app restarts
        // (only if we aren't the one who sent it)
        LocalStorageUtils.putContactRequestNotification(theContext, fromNickname);

        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            // user is inside the app
            Intent intent = new Intent(NEW_PUSHY_NOTIF);
            intent.putExtra("type", NEW_CONTACT_REQUEST);
            intent.putExtra("toId", toId);
            intent.putExtra("fromId", fromId);
            intent.putExtra("fromNickname", fromNickname);
            intent.putExtras(theIntent.getExtras());

            theContext.sendBroadcast(intent);

        } else {
            // user is outside of the app
            // set up the intent
            Intent intent = new Intent(theContext, AuthActivity.class);
            intent.putExtra("newContact", "theValue");
            intent.putExtras(theIntent.getExtras());

            PendingIntent pendingIntent = PendingIntent.getActivity(theContext, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Build notification
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(theContext, CHANNEL_ID)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_baseline_person_24)
                            .setContentTitle("Contact Request")
                            .setContentText(fromNickname + " sent you a contact request")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent);

            // Automatically configure a ChatMessageNotification Channel
            Pushy.setNotificationChannel(builder, theContext);

            // Get an instance of the NotificationManager service
            NotificationManager notificationManager =
                    (NotificationManager)
                            theContext.getSystemService(theContext.NOTIFICATION_SERVICE);

            // Build the notification and display it
            notificationManager.notify(1, builder.build());
        }
    }

    /**
     * Handles when this device receives a contact request response from a Pushy payload.
     *
     * @param theContext the context of the application
     * @param theIntent the Intent that stores the Pushy payload
     */
    private void acceptContactRequestResponsePushy(final Context theContext,
                                                   final Intent theIntent) {
        String toId = theIntent.getStringExtra("toId");
        String fromId = theIntent.getStringExtra("fromId");
        String fromNickname = theIntent.getStringExtra("fromNickname");
        Boolean isAccept = theIntent.getBooleanExtra("isAccept", false);

        // get tools to check if the user is in the app or not
        ActivityManager.RunningAppProcessInfo appProcessInfo =
                new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        if (isAccept) {
            // store a contacts notification so it can be loaded when the app restarts
            // only if the response to the request was an acceptance
            LocalStorageUtils.putNewContactsNotification(theContext, toId);
        }

        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            // user is inside the app
            Intent intent = new Intent(NEW_PUSHY_NOTIF);
            intent.putExtra("type", CONTACT_REQUEST_RESPONSE);
            intent.putExtra("toId", toId);
            intent.putExtra("fromId", fromId);
            intent.putExtra("fromNickname", fromNickname);
            intent.putExtra("isAccept", isAccept);
            intent.putExtras(theIntent.getExtras());

            theContext.sendBroadcast(intent);

        } else if (isAccept) {
            // we know that the user is outside of the app,
            // so send a notification ONLY IF it is an acceptance notification.
            // we don't want to send a notification if the user is not accepted.
            // set up the intent
            Intent intent = new Intent(theContext, AuthActivity.class);
            intent.putExtras(theIntent.getExtras());

            PendingIntent pendingIntent = PendingIntent.getActivity(theContext, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Build notification
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(theContext, CHANNEL_ID)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_messages_black_24dp)
                            .setContentTitle("Contact Request Acceptance")
                            .setContentText(fromNickname + " accepted your contact request!")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent);

            // Automatically configure a ChatMessageNotification Channel
            Pushy.setNotificationChannel(builder, theContext);

            // Get an instance of the NotificationManager service
            NotificationManager notificationManager =
                    (NotificationManager)
                            theContext.getSystemService(theContext.NOTIFICATION_SERVICE);

            // Build the notification and display it
            notificationManager.notify(1, builder.build());
        }
    }

    /**
     * Handles when this device receives a Delete Contact request from a Pushy payload.
     *
     * @param theContext the context of the application
     * @param theIntent the Intent that stores the Pushy payload
     */
    private void acceptContactDeletePushy(final Context theContext, final Intent theIntent) {
        // this nickname is the nickname of the account
        // that initially sent the request but then deleted the request,
        // we need it so we can delete a contact request notification if one exists
        // corresponding to it
        final String fromNickname = theIntent.getStringExtra("fromNickname");

        // get tools to check if the user is in the app or not
        ActivityManager.RunningAppProcessInfo appProcessInfo =
                new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            // user is inside the app
            Intent intent = new Intent(NEW_PUSHY_NOTIF);
            intent.putExtra("type", CONTACT_DELETE);
            intent.putExtra("fromNickname", fromNickname);
            intent.putExtras(theIntent.getExtras());

            theContext.sendBroadcast(intent);
        }
        // we don't want any kind of outside-app notifications when someone gets deleted
    }

    /**
     * Handles when this device receives a Outgoing Contact request Deletion from a Pushy payload.
     *
     * @param theContext the context of the application
     * @param theIntent the Intent that stores the Pushy payload
     */
    private void acceptContactRequestDeletePushy(final Context theContext, final Intent theIntent) {

        String deletedId = theIntent.getStringExtra("deletedId");
        String deletorId = theIntent.getStringExtra("deletorId");

        // get tools to check if the user is in the app or not
        ActivityManager.RunningAppProcessInfo appProcessInfo =
                new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            // user is inside the app
            Intent intent = new Intent(NEW_PUSHY_NOTIF);
            intent.putExtra("type", CONTACT_REQUEST_DELETE);
            intent.putExtra("deletedId", deletedId);
            intent.putExtra("deletorId", deletorId);
            intent.putExtras(theIntent.getExtras());

            theContext.sendBroadcast(intent);
        }
        // we don't want any kind of outside-app notifications when someone gets deleted
    }

    /**
     * Handles when this device receives a typing notification from a Pushy payload.
     *
     * @param theContext the context of the application
     * @param theIntent the Intent that stores the Pushy payload
     */
    private void acceptTypingPushy(final Context theContext, final Intent theIntent) {

        // get tools to check if the user is in the app or not
        ActivityManager.RunningAppProcessInfo appProcessInfo =
                new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            // only send to main activity if it exists
            Intent intent = new Intent(NEW_PUSHY_NOTIF);
            intent.putExtra("type", TYPING);
            intent.putExtra("chatId", theIntent.getIntExtra("chatId", 0));
            intent.putExtra("nickname", theIntent.getStringExtra("nickname"));
            intent.putExtra("isTyping", theIntent.getBooleanExtra("isTyping", false));
            intent.putExtras(theIntent.getExtras());

            // send to MainActivity
            theContext.sendBroadcast(intent);
        }
    }
}