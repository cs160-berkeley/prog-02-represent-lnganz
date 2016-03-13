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
    private String obamaVotes;
    private String romneyVotes;
    private String county;
    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        Intent intent = getIntent();
        String voteString = intent.getStringExtra("Votes");
        if (voteString != null) {
            String[] parsedString = voteString.split(";");
//            obamaVotes = parsedString[0];
            obamaVotes = ((int)Double.parseDouble(parsedString[0])) + "";
//            romneyVotes = parsedString[1];
            romneyVotes = ((int)Double.parseDouble(parsedString[1])) + "";
            county = parsedString[2];
            state = parsedString[3];

            final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
            stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
                @Override
                public void onLayoutInflated(WatchViewStub stub) {
                    mTextView = (TextView) stub.findViewById(R.id.text);
                    TextView tv = (TextView) findViewById(R.id.resultsText);
                    ProgressBar pb = (ProgressBar) findViewById(R.id.voteBar);
                    String toDisplay = "Romney: " + romneyVotes + "%  Obama: " + obamaVotes + "%\n" + county + ", " + state;
                    tv.setText(toDisplay);
                    pb.setProgress(Integer.parseInt(romneyVotes));
                }

            });
        }
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
}
