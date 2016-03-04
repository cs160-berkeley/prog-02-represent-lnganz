package com.represent.sigma.represent;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Stolen by Sigma, reated by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String TOAST = "Activity";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        // Value contains the String we sent over in WatchToPhoneService, "good job"

        if (messageEvent.getPath() != null) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            String path = messageEvent.getPath();
            Log.d("T", "Message Received: " + value);
            if (value != null) {
                Intent intent;
                String[] values = value.split(";");
                String activity = values[0];
                Log.d("PhoneListenerService", "Parsed Activity: " + activity);
                if (values.length > 1) {
                    Log.d("PhoneListenerService", "Parsed RepName: " + values[1]);
                }
                if (activity.equals("CongressionalActivity")) {
                    intent = new Intent(getBaseContext(), CongressionalActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d("PhoneListenerService", "Starting Activity: CongressionalActivity");
                    startActivity(intent);
                } else if (activity.equals("DetailedActivity")) {
                    intent = new Intent(getBaseContext(), DetailedActivity.class);
                    intent.putExtra("RepName", values[1]);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.d("PhoneListenerService", "Starting Activity: DetailedActivity");
                    startActivity(intent);
                } else if (activity.equals("Randomize")) {
                    intent = new Intent(getBaseContext(), RepresentativeService.class);
                    intent.putExtra("Random", true);
                    Log.d("PhoneListenerService", "Starting Service: RepresentativeService");
                    startService(intent);
                }
            }

            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions
        }
    }
}
