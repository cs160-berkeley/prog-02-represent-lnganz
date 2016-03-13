package com.represent.sigma.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    private static final String FRED_FEED = "/Fred";
    private static final String LEXY_FEED = "/Lexy";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
//        Log.v("T", "in WatchListenerService, got: " + messageEvent.getPath());
        //use the 'path' field in sendmessage to differentiate use cases
        //(here, fred vs lexy)
        String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        String path = new String(messageEvent.getPath());
        Log.d("T", "Message Path: " + path);
        Log.d("T", "Message Value: " + value);
        Intent mainIntent, voteIntent;
        if (path.equalsIgnoreCase("/VoteActivity")) {
            voteIntent = new Intent(this, VoteActivity.class);
            voteIntent.putExtra("Votes", value);
            voteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(voteIntent);
        } else if (path.equalsIgnoreCase("/MainActivity")) {
            mainIntent = new Intent(this, MainActivity.class);
            mainIntent.putExtra("Reps", value);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
        }
    }
}