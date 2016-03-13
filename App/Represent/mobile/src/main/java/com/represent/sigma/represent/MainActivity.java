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
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

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

        Button b = (Button) findViewById(R.id.locationButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent repIntent = new Intent(getBaseContext(), RepresentativeService.class);
                repIntent.putExtra("CurrentLocation", true);
                startService(repIntent);
            }
        });
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


    public void useZipCode(View view) {
        EditText et = (EditText) findViewById(R.id.zipText);
        String zip = et.getText().toString();
        if (zip.length() != 5 || !zip.matches("[0-9]+")) {
            Toast t = Toast.makeText(this, "Check ZIP Format", Toast.LENGTH_SHORT);
            t.show();
        } else {
//            useZipCode(zip, true);
            Intent repIntent = new Intent(getBaseContext(), RepresentativeService.class);
            repIntent.putExtra("Zipcode", zip);
            startService(repIntent);
        }
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
