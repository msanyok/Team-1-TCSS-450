/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uw.tcss450.group1project.ui.contacts.ContactsParentFragment;

/**
 * Provides utility methods for storing small amounts of data locally.
 * These functions should only be used when the app is not in the foreground.
 *
 * Note: This approach does not work when users are logging in and
 * out of the service on the same device. It is important to call
 * clearAllStoredNotifications() when the user logs out
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public final class LocalStorageUtils {

    /** The Shared Pref. key that is used to determine storage for new message notifications */
    private static final String NEW_MESSAGES_KEY = "NewMessages";

    /** The Shared Pref. key that is used to determine storage for new contact notifications */
    private static final String NEW_CONTACTS_KEY = "NewMessages";


    /** The key of the String set used to store the current chatIds with new messages */
    private static final String NEW_MESSAGE_STRING_SET = "chatIds";

    /**
     * Increments the counter for the given chat in local storage.
     *
     * @param theContext the context this method is used from
     * @param theChatId the chat a new message came from
     */
    public static final void putMissedMessage(final Context theContext, final String theChatId) {
        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences(NEW_MESSAGES_KEY, Context.MODE_PRIVATE);
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
    public static final Map<String, Integer> getMissedMessages(final Context theContext) {
        final Map<String, Integer> missedMessagesMap = new HashMap<>();
        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences(NEW_MESSAGES_KEY, Context.MODE_PRIVATE);

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
    public static final void removeNewMessageStore(final Context theContext, final int theChatId) {
        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences(NEW_MESSAGES_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = newMessagesStorage.edit();

        editor.remove(String.valueOf(theChatId));
        editor.apply();
    }

    /**
     * Clears all new message and contact notification data for this user. MUST do this action when
     * the account is logged out.
     *
     * @param theContext
     */
    public static void clearAllStoredNotifications(final Context theContext) {
        // remove the messages notifications
        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences(NEW_MESSAGES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = newMessagesStorage.edit();

        Set<String> chatIdKeys = newMessagesStorage.getStringSet(NEW_MESSAGE_STRING_SET,
                new HashSet<>());
        // clear all chatIds, then clear the string set
        for (String chatId : chatIdKeys) {
            editor.remove(chatId);
        }
        editor.remove(NEW_MESSAGE_STRING_SET);
        editor.apply();

        // remove the contacts notifications
        final SharedPreferences contactsStorage =
                theContext.getSharedPreferences(NEW_CONTACTS_KEY, Context.MODE_PRIVATE);
        editor = contactsStorage.edit();
        editor.remove(ContactsParentFragment.ALL_CONTACTS);
        editor.remove(ContactsParentFragment.REQUESTS);
        editor.remove(ContactNotificationViewModel.TOTAL_KEY);

        editor.apply();
    }

    /**
     * Increments the contact notification count for the given tab.
     *
     * @param theContext the context of where the put is being called
     * @param theTabString the tab that identifies which navigation tab the notification is for
     */
    public static void putContactNotification(final Context theContext,
                                              final String theTabString) {
        final SharedPreferences contactsStorage =
                theContext.getSharedPreferences(NEW_CONTACTS_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = contactsStorage.edit();

        // increment the store for the tab
        editor.putInt(theTabString, contactsStorage.getInt(theTabString, 0) + 1);
        editor.apply();
    }

    /**
     * Clears the contact notification count for the given tab.
     *
     * @param theContext the context of where the delete is being called
     * @param theTabString the tab that identifies which navigation tab should be cleared
     */
    public static void deleteContactNotifications(final Context theContext,
                                                  final String theTabString) {
        final SharedPreferences contactsStorage =
                theContext.getSharedPreferences(NEW_CONTACTS_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = contactsStorage.edit();

        // increment the store for the tab
        editor.putInt(theTabString, 0);
        editor.apply();
    }

    /**
     * Returns the contact notifications that are stored in storage.
     *
     * @param theContext the context of where the get is being called
     * @return the contact notifications that are stored in storage.
     */
    public static Map<String, Integer> getMissedContacts(final Context theContext) {

        final Map<String, Integer> contactsMap = new HashMap<>();
        final SharedPreferences missedContactsStorage =
                theContext.getSharedPreferences(NEW_CONTACTS_KEY, Context.MODE_PRIVATE);

        contactsMap.put(ContactsParentFragment.ALL_CONTACTS,
                missedContactsStorage.getInt(ContactsParentFragment.ALL_CONTACTS, 0));
        contactsMap.put(ContactsParentFragment.REQUESTS,
                missedContactsStorage.getInt(ContactsParentFragment.REQUESTS, 0));

        int total = missedContactsStorage.getInt(ContactsParentFragment.ALL_CONTACTS, 0) +
                missedContactsStorage.getInt(ContactsParentFragment.REQUESTS, 0);
        contactsMap.put(ContactNotificationViewModel.TOTAL_KEY, total);

        return contactsMap;
    }

    /**
     * Saves the given contact notification map on the device
     * storage so it can be loaded when the app runs again.
     *
     * @param theContext the context of where the save is being called
     * @param theMap the map to be saved
     */
    public static void saveContactsData(final Context theContext,
                                        final Map<String, Integer> theMap) {

        final SharedPreferences contactsStorage =
                theContext.getSharedPreferences(NEW_CONTACTS_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = contactsStorage.edit();

        int total = 0;
        for (Map.Entry<String, Integer> entry : theMap.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            editor.putInt(key, value);
        }
        editor.putInt(ContactNotificationViewModel.TOTAL_KEY, total);

        editor.apply();

    }

}