package com.represent.sigma.represent;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "iSf8f9bk9uIzv38QCcpQ8Ms4t";
    private static final String TWITTER_SECRET = "HzJcpoDRdqMqWNqTjyJZGJQ56Wc08QfykufMV6arjWeDRI9KsL";

    private String curLat;
    private String curLng;
    private GoogleApiClient mGoogleApiClient;
    public final static String EXTRA_MESSAGE = "com.represent.sigma.represent.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if (mGoogleApiClient == null) {
            Log.d("MainActivity", "Trying to connect Google API");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startCongressionalActivity(View view) {
//        Intent intent = new Intent(this, CongressionalActivity.class);
//        EditText editText = (EditText) findViewById(R.id.zipText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
//        Intent watchServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
//        watchServiceIntent.putExtra("RepSet", 1);
//        watchServiceIntent.putExtra("ACTIVITY", "MainActivity");
//        watchServiceIntent.putExtra("REP_NAMES", getString(R.string.rep_names_placeholder));
//        startService(watchServiceIntent);
        startActivity(new Intent(view.getContext(), CongressionalActivity.class));
    }

    public void useZipCode(View view) {
        EditText et = (EditText) findViewById(R.id.zipText);
        String zip = et.getText().toString();
        Geocoder geo = new Geocoder(getBaseContext());
        try {
            List<Address> locations = geo.getFromLocationName(zip, 1);
            if (locations.size() > 0) {
                Address foundLocation = locations.get(0);
                Log.d("MainActivity", "Geocoder OP, Found: " + foundLocation);
                curLat = foundLocation.getLatitude() + "";
                curLng = foundLocation.getLongitude() + "";
                reverseGeocode(curLat, curLng);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        Intent watchServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
//        Intent repServiceIntent = new Intent(getBaseContext(), RepresentativeService.class);
////        watchServiceIntent.putExtra("Zipcode", zip);
//        repServiceIntent.putExtra("Zipcode", zip);
//        Log.d("MainActivity", "Starting RepresentativeService with zip: " + zip);
//        startService(repServiceIntent);
    }

    @Override
    public void onConnected(Bundle bundle) {
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null) {
//            Log.d("MainActivity", String.valueOf(mLastLocation.getLatitude()));
//            Log.d("MainActivity", String.valueOf(mLastLocation.getLongitude()));
//        }
        Log.d("MainActivity", "Google API Connected!");
        Location mTestLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mTestLocation != null) {
            Log.d("MainActivity", String.valueOf(mTestLocation.getLatitude()));
            Log.d("MainActivity", String.valueOf(mTestLocation.getLongitude()));
        } else {
            Log.d("MainActivity", "Location Currently Null");
        }
        Button b = (Button) findViewById(R.id.locationButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    curLat = String.valueOf(mLastLocation.getLatitude());
                    curLng = String.valueOf(mLastLocation.getLongitude());
                    Log.d("MainActivity", curLat);
                    Log.d("MainActivity", curLng);
                    reverseGeocode(curLat, curLng);
                }
            }
        });
    }

    public void reverseGeocode(String lat, String lng) {
        try {
            URL locationUrl = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=AIzaSyBYLatfWm2MYM8fXPFKuviOfr-2cU6daTo"); // TODO: Hide API Key
            new GetLocationRequestTask().execute(locationUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("MainActivity", "Google API Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("MainActivity", "Google API Connection Failed");
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
            Log.d("MainActivity", "Response: " + response);
            parseLocation(response);
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

    public void parseLocation(String jsonString) {
//        Log.d("MainActivity", "Reverse Geocode Response: " + jsonString);
        try {
            String county = null;
            String zip = null;
            String state = null;
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
                startRepresentativeService(curLat, curLng, zip, county, state);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return null;
    }

    public void startRepresentativeService(String lat, String lng, String zip, String county, String state) {
        Intent repServiceIntent = new Intent(getBaseContext(), RepresentativeService.class);
        repServiceIntent.putExtra("Lat", curLat);
        repServiceIntent.putExtra("Lng", curLng);
        repServiceIntent.putExtra("Zipcode", zip);
        repServiceIntent.putExtra("County", county);
        repServiceIntent.putExtra("State", state);
        Log.d("MainActivity", "Starting RepresentativeService with zip=" + zip + ", Lat & Lng=" + curLat + ", " + curLng);
        startService(repServiceIntent);
    }

    public void startRepresentativeService(String zip, String county) {
        Intent repServiceIntent = new Intent(getBaseContext(), RepresentativeService.class);
        repServiceIntent.putExtra("Zipcode", zip);
        repServiceIntent.putExtra("County", county);
        Log.d("MainActivity", "Starting RepresentativeService with zip: " + zip);
        startService(repServiceIntent);
    }
}
