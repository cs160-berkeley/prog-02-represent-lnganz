package com.represent.sigma.represent;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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
import java.util.List;

/**
 * Created by Sigma on 3/3/2016.
 */
public class RepresentativeService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    public ArrayList<Representative> reps;
    private String curLat;
    private String curLng;
    private String zipCode;
    public String county;
    public String state;
    private Boolean random;
    private Boolean showToasts;
    private Boolean useCurrentLocation;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
//            String zipcode = b.getString("ZIP");
            Bundle b = intent.getExtras();
            random = b.getBoolean("Random", false);
            showToasts = !random;
            if (intent.hasExtra("Zipcode")) {
                zipCode = b.getString("Zipcode");
            }
            if (intent.hasExtra("County")) {
                county = b.getString("County");
            }
            if (intent.hasExtra("State")) {
                state = b.getString("State");
            }
            useCurrentLocation = intent.getBooleanExtra("CurrentLocation", false);
            if (mGoogleApiClient == null) {
                Log.d("RepresentativeService", "Trying to connect Google API");
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            } else {
                if (useCurrentLocation) {
                    useCurrentLocation();
                }
            }
            if (random) {
                randomize();
            } else if (zipCode != null && !useCurrentLocation) {
                useZipCode(zipCode, showToasts);
//                getRepresentatives(zip);
            }
        }
        return START_STICKY;
    }

    public boolean useZipCode(String zip, boolean showToasts) {
        Geocoder geo = new Geocoder(getBaseContext());
        try {
            List<Address> locations = geo.getFromLocationName(zip, 1);
            if (locations.size() > 0) {
                Address foundLocation = locations.get(0);
                Log.d("RepresentativeService", "Geocoder OP, Found: " + foundLocation);
                curLat = foundLocation.getLatitude() + "";
                curLng = foundLocation.getLongitude() + "";
                reverseGeocode(curLat, curLng);
                return true;
            }
            Log.d("RepresentativeService", zip + " is not a valid zipcode");
            if (showToasts) {
                Toast t = Toast.makeText(this, "Invalid ZIP", Toast.LENGTH_SHORT);
                t.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast t = Toast.makeText(this, "Couldn't access Google API", Toast.LENGTH_SHORT);
            t.show();
            Log.d("RepresentativeService", "Couldn't access Google API");
        }
        return false;
    }

    public void randomize() {
        boolean successful = false;
        int tries = 0;
        while (!successful && tries < 20) {
            String testZip = "" + (int) (Math.random() * 99999);
            Log.d("RepresentativeService", "Finding random ZIP, testing: " + testZip);
            successful = useZipCode(testZip, false);
            tries++;
        }
    }

    public void reverseGeocode(String lat, String lng) {
        try {
            URL locationUrl = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=AIzaSyBYLatfWm2MYM8fXPFKuviOfr-2cU6daTo"); // TODO: Hide API Key
            new GetLocationRequestTask().execute(locationUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetLocationRequestTask extends AsyncTask<URL, Integer, String> {
        @Override
        protected String doInBackground(URL... urls) {
            return handleUrls(urls);
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {}
        @Override
        protected void onPostExecute(String response) {
            Log.d("RepresentativeService", "Response: " + response);
            parseLocation(response);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("RepresentativeService", "Google API Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("RepresentativeService", "Google API Connection Failed");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("RepresentativeService", "Google API Connected!");
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (useCurrentLocation) {
            useCurrentLocation();
        }
    }

    public void useCurrentLocation() {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (useCurrentLocation) {
            if (mLastLocation != null) {
                curLat = String.valueOf(mLastLocation.getLatitude());
                curLng = String.valueOf(mLastLocation.getLongitude());
                Log.d("RepresentativeService", curLat);
                Log.d("RepresentativeService", curLng);
                reverseGeocode(curLat, curLng);
            } else {
                Log.d("RepresentativeService", "Location Currently Null");
                if (showToasts) {
                    Toast t = Toast.makeText(getBaseContext(), "Current Location Unknown", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        }
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
        if (reps.size() == 0) {
            if (random) {
                Log.d("RepresentativeService", "No representatives found, trying another random ZIP");
                randomize();
            } else {
                Log.d("RepresentativeService", "No representatives found for give ZIP");
                Toast t = Toast.makeText(getBaseContext(), "No Representatives Found", Toast.LENGTH_SHORT);
                t.show();
            }
        } else {
            startCongressionalActivity();
        }
    }

    public void parseLocation(String jsonString) {
//        Log.d("MainActivity", "Reverse Geocode Response: " + jsonString);
        if (jsonString == null) {
            Log.d("RepresentativeService", "Couldn't find location. API may be down.");
            return;
        }
        try {
            county = null;
            String zip = null;
            state = null;
            JSONObject obj = new JSONObject(jsonString);
            JSONArray results = obj.getJSONArray("results");
//            for (int i = 0; i < results.length(); i++) {
            for (int i = 0; i < 1; i++) {
                JSONArray components = results.getJSONObject(i).getJSONArray("address_components");
                for (int j = 0; j < components.length(); j++) {
                    JSONObject c = components.getJSONObject(j);
                    JSONArray types = c.getJSONArray("types");
                    String[] arr = new String[types.length()];
                    for (int k = 0; k < types.length(); k++) {
                        arr[k] = types.getString(k);
                    }
                    for (int k = 0; k < arr.length; k++) {
                        if (arr[k].equalsIgnoreCase("administrative_area_level_2")) {
                            county = c.getString("long_name");
                            Log.d("MainActivity", "Parsing Reverse Geocode, Found County: " + county);
//                            return county;
                        } else if (arr[k].equalsIgnoreCase("postal_code")) {
                            zip = c.getString("long_name");
                            Log.d("MainActivity", "Parsing Reverse Geocode, Found ZIP: " + zip);
                        } else if (arr[k].equalsIgnoreCase("administrative_area_level_1")) {
                            state = c.getString("short_name");
                            Log.d("MainActivity", "Parsing Reverse Geocode, Found State: " + state);
                        }
                    }
                }
            }
            getRepresentatives(curLat, curLng);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
