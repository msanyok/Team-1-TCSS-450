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
                {"Charles", "Bryan", "Big C", "1"},
                {"Austn", "Attaway", "Attaboy", "2"},
                {"Steven", "Omegna", "The Fisherman", "3"},
                {"Alex", "Maistruk", "The Human Database", "4"},
                {"Parker", "Rosengreen", "Muscles", "5"},
                {"Chris", "Ding", "The Intern", "6"},
                {"Random", "Guy", "IDKWHATTOPUT", "7"},
                {"Harold", "LooooooooonnnnnngggggName", "The G.O.A.T", "8"}
        };
        List<Contact> contacts = new ArrayList<>();
        for (String[] info : data) {
            contacts.add(new Contact(info[0], info[1], info[2], info[3]));
        }
        return contacts;
    }
}