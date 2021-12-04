package edu.uw.tcss450.group1project.services;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TypingTimeout {

    public static Timer timeout() {
Log.d("starting timer", "starting timer");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
Log.d("ending timer", "ending timer");
            }
        };
        timer.schedule(task, 5000);
        return timer;
    }
}
