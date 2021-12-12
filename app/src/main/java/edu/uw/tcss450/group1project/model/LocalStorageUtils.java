/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provides utility methods for storing small amounts of data locally.
 *
 * Note: This approach is not ideal and does not work when users are logging in and
 * out of the service on the same device. It is important to call
 * clearAllStoredNotifications() when the user logs out to ensure old data from
 * previous accounts does not persist.
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public final class LocalStorageUtils {

    /** The Shared Pref. key that is used to determine storage for new notifications */
    private static final String NOTIFICATION_STORAGE = "NewNotifications";

    /** The key of the String set used to store the current chatIds with new messages */
    private static final String NEW_MESSAGE_STRING_SET = "chatIds";

    /** The key for the String set that stores contact request notifications */
    private static final String CONTACT_REQUESTS = "newContactRequestNotifications";

    /** The key for the String set that stores contact notifications */
    private static final String CONTACTS = "newContactNotifications";

    /**
     * Increments the counter for the given chat in local storage.
     *
     * @param theContext the context this method is used from
     * @param theChatId the chat a new message came from
     */
    public static void putMissedMessage(final Context theContext, final String theChatId) {
        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = newMessagesStorage.edit();

        final Set<String> chatIds =
                new HashSet<>(newMessagesStorage.getStringSet(NEW_MESSAGE_STRING_SET,
                        new HashSet<>()));
        int newMessageCount = newMessagesStorage.getInt(theChatId, 0);

        // ensure that the string set that stores all chatIds with a new message contains theChatId
        chatIds.add(theChatId);
        editor.putStringSet(NEW_MESSAGE_STRING_SET, chatIds);
        editor.putInt(theChatId, newMessageCount + 1);
        editor.apply();
    }

    /**
     * Returns a map (ChatId -> New message count) that corresponds to the
     * number of messages missed in chats while the program was not in the foreground.
     *
     * @param theContext where this method was called from
     * @return the map
     */
    public static Map<String, Integer> getMissedMessages(final Context theContext) {
        final Map<String, Integer> missedMessagesMap = new HashMap<>();
        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);

        Set<String> chatIdKeys = newMessagesStorage.getStringSet(NEW_MESSAGE_STRING_SET,
                new HashSet<>());
        for (String chatId : chatIdKeys) {
            missedMessagesMap.put(chatId, newMessagesStorage.getInt(chatId, 0));
        }

        return missedMessagesMap;
    }

    /**
     * Removes the new chat message count local storage for the given chat ID
     *
     * @param theContext the context this method is used from
     * @param theChatId the chat a new message came from
     */
    public static void removeNewMessageStore(final Context theContext, final int theChatId) {
        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = newMessagesStorage.edit();

        editor.remove(String.valueOf(theChatId));
        editor.apply();
    }

    /**
     * Clears all new message and contact notification data for this user. MUST do this action when
     * the account is logged out.
     *
     * @param theContext the context this method is used from
     */
    public static void clearAllStoredNotifications(final Context theContext) {
        theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE).
                edit().clear().apply();
    }


    /**
     * Increments the contact request notification count.
     *
     * @param theContext the context of where the put is being called
     * @param theNickname the nickname that corresponds to who sent you a contact request
     */
    public static void putContactRequestNotification(final Context theContext,
                                                     final String theNickname) {
        final SharedPreferences contactRequestStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = contactRequestStorage.edit();

        // increment the store
        Set<String> set = new HashSet<>(
                contactRequestStorage.getStringSet(CONTACT_REQUESTS, new HashSet<>()));
        set.add(theNickname);
        editor.putStringSet(CONTACT_REQUESTS, set);
        editor.apply();
    }

    /**
     * Increments the new contacts notification count.
     *
     * @param theContext the context of where the put is being called
     * @param theNewContactId the id of the new contact (a unique identifier)
     */
    public static void putNewContactsNotification(final Context theContext,
                                                  final String theNewContactId) {
        final SharedPreferences contactStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = contactStorage.edit();

        // increment the store
        Set<String> set = new HashSet<>(contactStorage.getStringSet(CONTACTS, new HashSet<>()));
        set.add(theNewContactId);
        editor.putStringSet(CONTACTS, set);
        editor.apply();
        System.out.println(set.size());
    }


    /**
     * Removes the contact request notification corresponding to theNickname
     *
     * @param theContext the context of where the delete is being called
     * @param theNickname the nickname that corresponds to the contact request
     *                    that should no longer be a notification
     */
    public static void decrementContactRequestNotifications(final Context theContext,
                                                            final String theNickname) {
        final SharedPreferences contactsStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = contactsStorage.edit();

        // increment the store for the tab
        Set<String> set =
                new HashSet<>(contactsStorage.getStringSet(CONTACT_REQUESTS, new HashSet<>()));
        set.remove(theNickname);
        editor.putStringSet(CONTACT_REQUESTS, set);
        editor.apply();
    }

    /**
     * Clears all of the contact notifications.
     *
     * @param theContext where this method was called
     */
    public static void clearContactsNotifications(final Context theContext) {
        System.out.println("DELETING CONTACTS NOTIFICATIONS");
        final SharedPreferences contactsStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = contactsStorage.edit();
        editor.remove(CONTACTS);
        editor.apply();
    }

    /**
     * Clears all of the contact request notifications.
     *
     * @param theContext where this method was called
     */
    public static void clearContactRequestsNotifications(final Context theContext) {
        final SharedPreferences contactsStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = contactsStorage.edit();
        editor.remove(CONTACT_REQUESTS);
        editor.apply();
    }

    /**
     * Returns the String set that corresponds to the nicknames for contact request notifications
     *
     * @param theContext where this method was called
     * @return the String set
     */
    public static Set<String> getContactRequestNotifications(final Context theContext) {
        final SharedPreferences missedContactRequestStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);

        // there is a chance that we save a contact request notification that is
        // one that we sent someone, so remove a notification from the set that corresponds
        // to the user's nickname if it exists
        final Set<String> savedContactRequests =
                new HashSet<>(missedContactRequestStorage.getStringSet(
                        CONTACT_REQUESTS, new HashSet<>()));
        savedContactRequests.remove(new ViewModelProvider((ViewModelStoreOwner) theContext).
                get(UserInfoViewModel.class).getNickname());
        return savedContactRequests;
    }

    /**
     * Returns the String set that corresponds to the nicknames for new contacts notifications
     *
     * @param theContext where this method was called
     * @return the String set
     */
    public static Set<String> getContactsNotifications(final Context theContext) {
        final SharedPreferences missedContactRequestStorage =
                theContext.getSharedPreferences(NOTIFICATION_STORAGE, Context.MODE_PRIVATE);

        return missedContactRequestStorage.getStringSet(CONTACTS, new HashSet<>());
    }
}