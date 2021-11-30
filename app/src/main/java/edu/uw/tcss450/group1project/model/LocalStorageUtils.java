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

/**
 * Provides utility methods for storing small amounts of data locally.
 * These functions should only be used when the app is not in the foreground.
 *
 * @author Austn Attaway
 * @version Fall 2021
 */
public final class LocalStorageUtils {

    /**
     * Increments the counter for the given chat in local storage.
     *
     * @param theContext the context this method is used from
     * @param theChatId the chat a new message came from
     */
    public static final void putMissedMessage(final Context theContext, final String theChatId) {

        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences("NewMessages", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = newMessagesStorage.edit();

        final Set<String> chatIds =
                new HashSet<>(newMessagesStorage.getStringSet("chatIds", new HashSet<String>()));
        int newMessageCount = newMessagesStorage.getInt(theChatId, 0);

        // ensure that the string set that stores all chatIds with a new message contains theChatId
        chatIds.add(theChatId);
        editor.putStringSet("chatIds", chatIds);
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
                theContext.getSharedPreferences("NewMessages", Context.MODE_PRIVATE);

        Set<String> chatIdKeys = newMessagesStorage.getStringSet("chatIds", new HashSet<>());
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
                theContext.getSharedPreferences("NewMessages", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = newMessagesStorage.edit();

        editor.remove(String.valueOf(theChatId));
        editor.apply();
    }

}