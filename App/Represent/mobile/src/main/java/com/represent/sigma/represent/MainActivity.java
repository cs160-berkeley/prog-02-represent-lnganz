package com.represent.sigma.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.represent.sigma.represent.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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
        Intent watchServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        watchServiceIntent.putExtra("RepSet", 1);
        watchServiceIntent.putExtra("ACTIVITY", "MainActivity");
//        watchServiceIntent.putExtra("REP_NAMES", getString(R.string.rep_names_placeholder));
        startService(watchServiceIntent);
        startActivity(new Intent(view.getContext(), CongressionalActivity.class));
    }

    public void useZipCode(View view) {
        MyGlobals.storedReps = new ArrayList<Representative>();
        MyGlobals.storedReps.add(new Representative());
        Intent watchServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
//        watchServiceIntent.putExtra("REP_NAMES", getString(R.string.rep_names_placeholder));
        watchServiceIntent.putExtra("RepSet", 2);
        watchServiceIntent.putExtra("ACTIVITY", "MainActivity");
        Log.d("MainActivity", "Starting Service: PhoneToWatchService");
        startService(watchServiceIntent);
        Log.d("MainActivity", "Starting Activity: CongressionalActivity");
        Intent congressionalIntent = new Intent(view.getContext(), CongressionalActivity.class); // TODO: Start RepresentativeService instead of CongressionalActivity
        congressionalIntent.putExtra("RepSet", 2);
        startActivity(congressionalIntent);
    }
}
