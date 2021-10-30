package edu.uw.tcss450.group1project.ui.contacts;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.group1project.R;

public class ContactGenerator {

    public static List<Contact> getContactList() {
        String[][] data = {
                {"Charles", "Bryan", "Big C"},
                {"Austn", "Attaway", "Attaboy"},
                {"Steven", "Omegna", "The Fisherman"},
                {"Alex", "Maistruk", "The Human Database"},
                {"Parker", "Rosengreen", "Muscles"},
                {"Chris", "Ding", "The Intern"}
        };
        List<Contact> contacts = new ArrayList<>();
        for (String[] info : data) {
            contacts.add(new Contact(info[0], info[1], info[2]));
        }
        return contacts;
    }
}
