package com.sarahrobinson.finalyearproject.classes;

import android.util.Log;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.eventFragment;

/**
 * Created by sarahrobinson on 27/05/2017.
 */

public class Server implements Runnable {

    private static final String TAG = "Server class ******* ";

    public static volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            Log.d(TAG, "Server is running");
            eventFragment.checkIfDone();
        }
        Log.d(TAG, "Server is stopped");
    }

    public void terminate() {
        running = false;
    }
}
