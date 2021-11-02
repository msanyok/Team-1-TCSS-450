/*
 * TCSS450 Mobile Applications
 * Fall 2021
 */

package edu.uw.tcss450.group1project.ui.messages;

import java.util.Random;

/**
 * MessageGenerator is a class for generating a random message for the purposes of an initial
 * display to MessagesFragment.
 */
public class MessageGenerator {

    /** The array of message options */
    private static final String[] messages = {
            "The best thing about the future is that it comes one day at a time.",
            "I’m sick of following my dreams, man. I’m just going to ask where " +
                                                        "they’re going and hook up with ’em later.",
            "A pessimist is a person who has had to listen to too many optimists.",
            "My favorite machine at the gym is the vending machine.",
            "If you think you are too small to make a difference, try sleeping with a mosquito.",
            "I dream of a better tomorrow, where chickens can cross the road and " +
                                                           "not be questioned about their motives.",
            "I just don't know what to say.... this project is that awesome!"
    };

    /**
     * Returns a random message to be displayed
     *
     * @return the random message
     */
    public static String getRandomMessage() {
        Random rand = new Random();
        return messages[rand.nextInt(messages.length)];
    }
}
