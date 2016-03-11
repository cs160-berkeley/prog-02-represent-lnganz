package com.represent.sigma.represent;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Sigma on 3/3/2016.
 */
public class RepresentativeService extends Service {

    public ArrayList<Representative> reps;
    public String county;
    public String state;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String curLat, curLng;
            Bundle b = intent.getExtras();
            String zipcode = b.getString("ZIP");
            Boolean random = b.getBoolean("Random");
//            if (intent.hasExtra("Random")) {
//                zipcode = getRandomZipcode();
//            } else if (intent.hasExtra("GetLocation")) {
//                getCurrentLocation();
//            } else
            if (intent.hasExtra("Zipcode")) {
                zipcode = b.getString("Zipcode");
            }
            if (intent.hasExtra("County")) {
                county = b.getString("County");
            }
            if (intent.hasExtra("State")) {
                state = b.getString("State");
            }
            if (intent.hasExtra("Lat")) {
                curLat = b.getString("Lat");
                curLng = b.getString("Lng");
                getRepresentatives(curLat, curLng);
            } else if (zipcode != null) {
                getRepresentatives(zipcode);
            }
//        }
        }
        return START_STICKY;
    }

    public void getRepresentatives(String zip) {
        try {
            String startPath = "https://congress.api.sunlightfoundation.com/legislators/locate?zip=";
            zip = zip.trim();
            String apikey = "&apikey=83097d17b7e24cf7bf3e8da5dd132ff0"; // TODO: HIDE API KEY
            URL url = new URL(startPath + zip + apikey);
            Log.d("RepresentativeService", "Requesting Reps from Sunlight Foundation");
            new GetBasicRequestTask().execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRepresentatives(String lat, String lng) {
        try {
            String startPath = "https://congress.api.sunlightfoundation.com/legislators/locate?";
            String latStr = "latitude=" + lat;
            String lngStr = "&longitude=" + lng;
            String apikey = "&apikey=83097d17b7e24cf7bf3e8da5dd132ff0"; // TODO: HIDE API KEY
            URL url = new URL(startPath + latStr + lngStr + apikey);
            Log.d("RepresentativeService", "Requesting Reps from Sunlight Foundation");
            new GetBasicRequestTask().execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCurrentLocation() {
        // Google location stuff
    }

    public String getRandomZipcode() {
        String[] zipcodes = {"10021", "93110", "73103", "94596"};
        String zip = zipcodes[(int)(Math.random()*4)];
        return zip;
    }

    private class GetBasicRequestTask extends AsyncTask<URL, Integer, String> {
        @Override
        protected String doInBackground(URL... urls) {
            return handleUrls(urls);
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {}
        @Override
        protected void onPostExecute(String response) {
            Log.d("RepresentativeService", "Response: " + response);
            parseBasicJSONString(response);
        }
    }

    private class GetDetailedRequestTask extends AsyncTask<URL, Integer, String> {
        @Override
        protected String doInBackground(URL... urls) {
            return handleUrls(urls);
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {}
        @Override
        protected void onPostExecute(String response) {
            Log.d("RepresentativeService", "Response: " + response);
            parseDetailedJSONString(response);
        }
    }

    public String handleUrls(URL[] urls) {
        try {
            URLConnection urlConnection = urls[0].openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            JSONObject response = new JSONObject(responseStrBuilder.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void parseBasicJSONString(String jsonString) {
        ArrayList<Representative> parsedReps = new ArrayList<Representative>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray reps = obj.getJSONArray("results");
            Log.d("RepresentativeService", "Reps: " + reps.toString());
            for (int i = 0; i < reps.length(); i++) {
                JSONObject repJSON = reps.getJSONObject(i);
                Representative newRep = new Representative();
                newRep.name = repJSON.getString("first_name") + " " + repJSON.getString("last_name");
                newRep.chamber = repJSON.getString("chamber");
                newRep.party = repJSON.getString("party");
                newRep.website = repJSON.getString("website");
                newRep.emailAddress = repJSON.getString("oc_email");
                newRep.endDate = repJSON.getString("term_end");
                newRep.bioguideId = repJSON.getString("bioguide_id");
                newRep.twitterId = repJSON.getString("twitter_id");
                Log.d("RepresentativeService", "Rep: " + newRep.name);
                parsedReps.add(newRep);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        reps = parsedReps;
        startCongressionalActivity();
    }

    public void startCongressionalActivity() {
        Intent intent = new Intent(getBaseContext(), CongressionalActivity.class);
        intent.putExtra("County", county);
        intent.putExtra("State", state);
        intent.putParcelableArrayListExtra("Representatives", reps);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        Intent watchIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        watchIntent.putExtra("Activity", "MainActivity");
        watchIntent.putExtra("Reps", reps.get(0).chamber + ";" + reps.get(0).name + ";(" + reps.get(0).party + ");"
                + reps.get(1).chamber + ";" + reps.get(1).name + ";(" + reps.get(1).party + ");"
                + reps.get(2).chamber + ";" + reps.get(2).name + ";(" + reps.get(2).party + ")");
        startService(watchIntent);
    }
    public void parseDetailedJSONString(String jsonString) {

    }

    public void getDetails(ArrayList<Representative> reps) {
        for (int i = 0; i < reps.size(); i++) {
            Representative rep = reps.get(i);
        }
    }
}
