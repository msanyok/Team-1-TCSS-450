/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.contacts;

import java.util.ArrayList;
import java.util.List;

/**
 * ContactGenerator is a class to generate static contact data for initial testing.
 *
 * @author Parker Rosengreen
 * @version Fall 2021
 */
public class ContactGenerator {

    /**
     * Returns a list of "dummy" contacts
     *
     * @return the list of contacts
     */
    public static List<Contact> getContactList() {
        String[][] data = {
                {"Charles", "Bryan", "Big C"},
                {"Austn", "Attaway", "Attaboy"},
                {"Steven", "Omegna", "The Fisherman"},
                {"Alex", "Maistruk", "The Human Database"},
                {"Parker", "Rosengreen", "Muscles"},
                {"Chris", "Ding", "The Intern"},
                {"Random", "Guy", "IDKWHATTOPUT"},
                {"Harold", "LooooooooonnnnnngggggName", "The G.O.A.T"}
        };
        List<Contact> contacts = new ArrayList<>();
        for (String[] info : data) {
            contacts.add(new Contact(info[0], info[1], info[2]));
        }
        return contacts;
    }
}
