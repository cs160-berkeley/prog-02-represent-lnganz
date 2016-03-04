package com.represent.sigma.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Sigma on 3/3/2016.
 */
public class RepresentativeService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle b = intent.getExtras();
        String zipcode = b.getString("ZIP");
        Boolean random = b.getBoolean("Random");

        if (random) {
            Intent repIntent = new Intent(getBaseContext(), CongressionalActivity.class);
            repIntent.putExtra("RepSet", 1); // HARD CODED FOR NOW
            repIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(repIntent);
            Log.d("RepresentativeService", "Randomized - Starting Activity: CongressionalActivity");

            Intent voteIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            voteIntent.putExtra("RepSet", 1); // HARD CODED FOR NOW
            voteIntent.putExtra("ACTIVITY", "VoteActivity");
            Log.d("RepresentativeService", "Randomized - Starting Service: PhoneToWatchService");
            startService(voteIntent);
        } else {
            Intent repIntent = new Intent(getBaseContext(), CongressionalActivity.class);
            repIntent.putExtra("RepSet", 2); // HARD CODED FOR NOW
            repIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("RepresentativeService", "Starting Activity: CongressionalActivity");
            startActivity(repIntent);

            Intent voteIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
            voteIntent.putExtra("RepSet", 2); // HARD CODED FOR NOW
            Log.d("RepresentativeService", "Starting Service: PhoneToWatchService");
            startService(voteIntent);
        }

        return START_STICKY;
    }
}
