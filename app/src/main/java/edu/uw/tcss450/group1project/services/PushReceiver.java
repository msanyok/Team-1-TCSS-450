/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.services;

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
import edu.uw.tcss450.group1project.ui.messages.ChatMessage;
import me.pushy.sdk.Pushy;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

/**
 * PushReceiver is a class for accepting push notifications from Pushy
 *
 * @author Charles Bryan
 * @author Austn Attaway
 * @version Fall 2021
 */
public class PushReceiver extends BroadcastReceiver {

    /** The String that notifies a new message has been received from pushy  */
    public static final String RECEIVED_NEW_MESSAGE = "new message from pushy";

    /** The ID for the channel used for notifications */
    private static final String CHANNEL_ID = "1";



    // TODO: NOTE: this method is work in progress,
    //             should consider refactoring into smaller methods?
    /**
     * Handles what should occur when this device receives a Pushy payload.
     *
     * @param theContext the context of the application
     * @param theIntent the Intent that stores the Pushy payload
     */
    @Override
    public void onReceive(final Context theContext, final Intent theIntent) {

        // todo: we will likely have different types here (will need different actions for
        //       each type, consider making methods below)
        final String typeOfMessage = theIntent.getStringExtra("type");
        ChatMessage message = null;
        int chatId = -1;

        try{
            message = ChatMessage.createFromJsonString(theIntent.getStringExtra("message"));
            chatId = theIntent.getIntExtra("chatid", -1);
        } catch (JSONException e) {
            //Web service sent us something unexpected...I can't deal with this.
            throw new IllegalStateException("Error from Web Service. Contact Dev Support");
        }

        ActivityManager.RunningAppProcessInfo appProcessInfo =
                new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        // todo: may have multiple methods down here that accept each different type of message,
        //      will be using the typeOfMessage for this
        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            //app is in the foreground so send the message to the active Activities
            Log.d("PUSHY", "Message received in foreground: " + message);

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);
            i.putExtra("chatMessage", message);
            i.putExtra("chatid", chatId);
            i.putExtras(theIntent.getExtras());

            theContext.sendBroadcast(i);

        } else {
            //app is in the background so create and post a notification
            Log.d("PUSHY", "Message received in background: " + message.getMessage());

            Intent i = new Intent(theContext, AuthActivity.class);
            i.putExtras(theIntent.getExtras());

            PendingIntent pendingIntent = PendingIntent.getActivity(theContext, 0,
                    i, PendingIntent.FLAG_UPDATE_CURRENT);

            //research more on notifications the how to display them
            //https://developer.android.com/guide/topics/ui/notifiers/notifications
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(theContext, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_messages_black_24dp)
                    .setContentTitle("Message from: " + message.getSender())
                    .setContentText(message.getMessage())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent);

            // Automatically configure a ChatMessageNotification Channel for devices running Android O+
            Pushy.setNotificationChannel(builder, theContext);

            // Get an instance of the NotificationManager service
            NotificationManager notificationManager =
                    (NotificationManager)
                            theContext.getSystemService(theContext.NOTIFICATION_SERVICE);

            // Build the notification and display it
            notificationManager.notify(1, builder.build());
        }

    }

}

