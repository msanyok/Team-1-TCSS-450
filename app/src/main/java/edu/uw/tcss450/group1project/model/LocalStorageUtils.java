/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LocalStorageUtils {

    public static final void putMissedMessage(final Context theContext, String theChatId) {

        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences("NewMessages", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = newMessagesStorage.edit();

        final Set<String> chatIds =
                newMessagesStorage.getStringSet("chatIds", new HashSet<String>());
        int newMessageCount = newMessagesStorage.getInt(theChatId, 0);

        // ensure that the string set that stores all chatIds with a new message contains theChatId
        chatIds.add(theChatId);
        editor.putStringSet("chatIds", chatIds);
        editor.putInt(theChatId, newMessageCount + 1);
        editor.apply();

    }

    // todo: clear the shared preferences to default values once the vals are recieved
    public static final Map<String, Integer> getMissedMessages(final Context theContext) {
        final Map<String, Integer> missedMessagesMap = new HashMap<>();

        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences("NewMessages", Context.MODE_PRIVATE);


        Set<String> chatIdKeys = newMessagesStorage.getStringSet("chatIds", new HashSet<>());
        for (String chatId : chatIdKeys) {
            missedMessagesMap.put(chatId, newMessagesStorage.getInt(chatId, 0));

            // remove the mapping of chatId -> number of missed messages for this chat



Log.d("BACKGROUND MESSAGES", chatId + ": " + newMessagesStorage.getInt(chatId, 0) + " new messages!");
        }




        return missedMessagesMap;

    }

    public static final void removeNewMessageStore(final Context theContext, final int theChatId) {

        final SharedPreferences newMessagesStorage =
                theContext.getSharedPreferences("NewMessages", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = newMessagesStorage.edit();

        editor.remove(String.valueOf(theChatId));
        editor.apply();
    }



}
