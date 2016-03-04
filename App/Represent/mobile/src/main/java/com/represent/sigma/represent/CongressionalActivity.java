package com.represent.sigma.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CongressionalActivity extends AppCompatActivity {

    public ArrayList<Representative> repList;
    private int repSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //My Setup
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        setTitle("Congressional Representatives in 94596");
        repSet = intent.getIntExtra("RepSet", 1);
        initializeRepresentatives();

        updateLayout();
    }

    public void onRestart(Bundle savedInstance) {
        updateLayout();
    }

    private void updateLayout() {
        TextView tv = (TextView) findViewById(R.id.locationText);
        ImageButton ib1 = (ImageButton) findViewById(R.id.repImageButton1);
        ImageButton ib2 = (ImageButton) findViewById(R.id.repImageButton2);
        ImageButton ib3 = (ImageButton) findViewById(R.id.repImageButton3);
        TextView tv1 = (TextView) findViewById(R.id.repText1);
        TextView tv2 = (TextView) findViewById(R.id.repText2);
        TextView tv3 = (TextView) findViewById(R.id.repText3);
        if (repSet == 1) {
            tv.setText("Members of Congress\nContra Costa County, CA");
        } else if (repSet == 2) {
            tv.setText("Members of Congress\nCanadian County, OK");
            ib1.setImageResource(R.drawable.frank);
            tv1.setText(getString(R.string.frank_congressional_placeholder));
            ib2.setImageResource(R.drawable.jim);
            tv2.setText(getString(R.string.jim_congressional_placeholder));
            ib3.setImageResource(R.drawable.james);
            tv3.setText(getString(R.string.james_congressional_placeholder));
        }
    }

    public void initializeRepresentatives() {
        repList = new ArrayList<Representative>();
        repList.add(new Representative());
        repList.add(new Representative());
        repList.add(new Representative());
    }

    public void startDetailedActivity(View view) {
        Intent intent = new Intent(view.getContext(), DetailedActivity.class);
        if (repSet == 2) {
            intent.putExtra("RepName", "James Lankford");
        } else {
            intent.putExtra("RepName", "Mark DeSaulnier");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startWatchVoteActivity(View view) {
        Intent watchServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        watchServiceIntent.putExtra("ACTIVITY", "VoteActivity");
        watchServiceIntent.putExtra("RepSet", repSet);
        startService(watchServiceIntent);
    }

}
