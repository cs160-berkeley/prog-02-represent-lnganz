package com.represent.sigma.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Heavily inspired by Catnip, created by joleary and noon on 2/19/16 at very late in the night.
 */
public class PhoneToWatchService extends Service {
    private GoogleApiClient mApiClient;
    private String repNames;
    private String activity;
    private int repSet;
    private String reps;
    private String votes;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Which cat do we want to feed? Grab this info from INTENT
        // which was passed over when we called startService
        String path;
        String message;
        Bundle extras = intent.getExtras();
        repNames = extras.getString("REP_NAMES");
        activity = extras.getString("Activity");
        reps = extras.getString("Reps");
        votes = extras.getString("Votes");
//        repSet = extras.getInt("RepSet");

        // Send the message with the cat name
        new Thread(new Runnable() {
            @Override
            public void run() {
                //first, connect to the apiclient
                mApiClient.connect();
                //now that you're connected, send a message with the representative name
                if (activity != null) {
                   if (activity.equalsIgnoreCase("MainActivity")) {
                       sendMessage("/MainActivity", reps);
                   } else if (activity.equalsIgnoreCase("VoteActivity")) {
                       sendMessage("/VoteActivity", votes);
                   } else if (activity.equalsIgnoreCase("BothActivities")) {
                       sendMessage("/BothActivities", reps + ";" + votes);
                   }
                }
//                    if (activity != null && activity.equals("VoteActivity")) {
//                    if (repSet != 0) {
//                        sendMessage("/rep_set", "" + repSet);
//                    }
//                    sendMessage("/activity", "VoteActivity");
//                }
////                else if (repNames != null) {
////                    sendMessage("/rep_names", repNames);
////                }
//                else {
//                    sendMessage("/rep_set", "" + 1);
//                }
            }
        }).start();

        return START_STICKY;
    }

    @Override //remember, all services need to implement an IBinder
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendMessage( final String path, final String text ) {
        //one way to send message: start a new thread and call .await()
        //see watchtophoneservice for another way to send a message
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    //we find 'nodes', which are nearby bluetooth devices (aka emulators)
                    //send a message for each of these nodes (just one, for an emulator)
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, text.getBytes() ).await();
                    //4 arguments: api client, the node ID, the path (for the listener to parse),
                    //and the message itself (you need to convert it to bytes.)
                }
            }
        }).start();
    }
}
