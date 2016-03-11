package com.represent.sigma.represent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class VoteActivity extends Activity {

    public static boolean active = false;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                TextView tv = (TextView) findViewById(R.id.resultsText);
                ProgressBar pb = (ProgressBar) findViewById(R.id.voteBar);
                if (extras != null && extras.getInt("RepSet", 1) == 2) {
                    tv.setText("Romney: 67%   Obama: 33%\nCanadian County, OK");
                    pb.setProgress(67);
                }
            }
        });

//        String location = extras.getString("Location");

    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
//    public void launchDetailedView(View view) {
//        System.out.println("VoteActivity button clicked");
//        Intent intent = new Intent(getBaseContext(), WatchToPhoneService.class);
//        intent.putExtra("Activity", "DetailedActivity");
//        intent.putExtra("Representative", "Mark DeSaulnier");
//        startService(intent);
//    }
}
