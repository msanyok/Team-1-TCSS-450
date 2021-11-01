/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.uw.tcss450.group1project.ui.contacts.Contact;
import edu.uw.tcss450.group1project.ui.contacts.ContactGenerator;

/**
 * ChatRoomGenerator is a class for generating "dummy" chat rooms for initial display to
 * MessagesFragment.
 */
public class ChatRoomGenerator {

    /**
     * Returns a list of all combinations of chat rooms between registered contacts
     *
     * @return the list of chat rooms
     */
    public static List<ChatRoom> getChatRooms() {
        List<ChatRoom> rooms = new ArrayList<>();
        List<Contact> contacts = ContactGenerator.getContactList();
        List<Contact[]> combinations = new ArrayList<>();
        for (int i = 1; i <= contacts.size(); i++) { // generate all possible combinations
            generateCombinations(combinations, 0, 0, contacts, new Contact[i]);
        }
        Collections.shuffle(combinations);
        for (Contact[] arr : combinations) {
            rooms.add(new ChatRoom(Arrays.asList(arr)));
        }
        return rooms;
    }

    /**
     * A utility method to generate all combinations of size theCurrentCombo.length
     *
     * @param theCombos the accumulated list of combinatios
     * @param theInsertionIndex the index to insert into theCurrentCombo
     * @param theListIndex the index from which to retrieve the next contact
     * @param theContacts the list of available contacts
     * @param theCurrentCombo the combination of contacts
     */
    private static void generateCombinations(final List<Contact[]> theCombos,
                                             final int theInsertionIndex,
                                             final int theListIndex,
                                             final List<Contact> theContacts,
                                             final Contact[] theCurrentCombo) {
        if (theInsertionIndex == theCurrentCombo.length) {
            theCombos.add(Arrays.copyOf(theCurrentCombo, theCurrentCombo.length));
            return;
        }
        if (theListIndex < theContacts.size()) {
            theCurrentCombo[theInsertionIndex] = theContacts.get(theListIndex);
            generateCombinations(theCombos, theInsertionIndex + 1, theListIndex + 1,
                                 theContacts, theCurrentCombo);
            generateCombinations(theCombos, theInsertionIndex, theListIndex + 1,
                                 theContacts, theCurrentCombo);

        }
    }
}
