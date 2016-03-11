package com.represent.sigma.represent;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParser;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.*;

import org.json.JSONArray;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import retrofit.http.Query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CongressionalActivity extends AppCompatActivity {

    private TwitterLoginButton loginButton;
    private LinearLayout tweetParentView;
    public ArrayList<Representative> repList;
    private int repSet;
    private String county;
    private String stateAbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congressional);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        repList = intent.getParcelableArrayListExtra("Representatives");
        county = intent.getStringExtra("County");
        stateAbr = intent.getStringExtra("State");

        if (repList == null || repList.size() < 3) {
            initializeRepresentatives();
        }
        testThatWeGotReps();

        //My Setup
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        setTitle("Congressional Representatives in 94596");
        repSet = intent.getIntExtra("RepSet", 1);

        updateLayout();

        tweetParentView = (LinearLayout) findViewById(R.id.repOuterLayout);
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session != null) {
            TwitterLoginButton tlb = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
            tlb.setVisibility(View.GONE);
            getRecentTweets();
        } else {
            loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
            loginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // The TwitterSession is also available through:
                    // Twitter.getInstance().core.getSessionManager().getActiveSession()
                    TwitterSession session = result.data;
                    // TODO: Remove toast and use the TwitterSession's userID
                    // with your app's user model
                    String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                    getRecentTweets();

                }

                @Override
                public void failure(TwitterException exception) {
                    Log.d("TwitterKit", "Login with Twitter failure", exception);
                }
            });
        }


//        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
//        StatusesService statusesService = twitterApiClient.getStatusesService();
//        statusesService.userTimeline(null, "ArchonNadia", 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
//            @Override
//            public void success (Result<List<Tweet>> result) {
//                Log.d("CongressionalActivity", "Got tweet" + result.toString());
////                TweetView tweetView = new TweetView(CongressionalActivity.this, result.data);
////                parentView.addView(tweetView, 4);
////                Log.d("CongressionalActivity", );
//            }
//            @Override
//            public void failure(TwitterException e) {
//                Log.d("CongressionalActivity", "Couldn't get tweet for some reason");
//                e.printStackTrace();
//            }
//        });
//        getAndShowTweets();

    }

    public void getRecentTweets() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final StatusesService statusesService = twitterApiClient.getStatusesService();
        statusesService.userTimeline(null, repList.get(0).twitterId, 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
            @Override
            public void success (Result<List<Tweet>> result) {
                Log.d("CongressionalActivity", "Got tweet: " + result.toString());
                Tweet tweet = result.data.get(0);
                TweetView tweetView = new TweetView(CongressionalActivity.this, tweet);
                tweetParentView.addView(tweetView, 3);
                String imgUrl = tweet.user.profileImageUrl;
                imgUrl = imgUrl.replaceAll("_normal", ""); // Remove _normal from end of url to get full-size image
                repList.get(0).imageUrl = imgUrl;
                Log.d("CongressionalActivity", "Twitter Profile Image URL: " + imgUrl);
                new DownloadImageTask((ImageButton) findViewById(R.id.repImageButton1))
                        .execute(imgUrl);

//                Log.d("CongressionalActivity", );
                statusesService.userTimeline(null, repList.get(1).twitterId, 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                    @Override
                    public void success (Result<List<Tweet>> result) {
                        Log.d("CongressionalActivity", "Got tweet: " + result.toString());
                        Tweet tweet = result.data.get(0);
                        TweetView tweetView = new TweetView(CongressionalActivity.this, tweet);
                        tweetParentView.addView(tweetView, 5);
                        String imgUrl = tweet.user.profileImageUrl;
                        imgUrl = imgUrl.replaceAll("_normal", "");
                        repList.get(1).imageUrl = imgUrl;
//                        imgUrl = imgUrl.substring(0, imgUrl.length()-12) + ".jpeg"; // Remove _normal from end of url to get full-size image
                        Log.d("CongressionalActivity", "Twitter Profile Image URL: " + imgUrl);
                        new DownloadImageTask((ImageButton) findViewById(R.id.repImageButton2))
                                .execute(imgUrl);
                        statusesService.userTimeline(null, repList.get(2).twitterId, 1, null, null, null, null, null, null, new Callback<List<Tweet>>() {
                            @Override
                            public void success(Result<List<Tweet>> result) {
                                Log.d("CongressionalActivity", "Got tweet: " + result.toString());
                                Tweet tweet = result.data.get(0);
                                TweetView tweetView = new TweetView(CongressionalActivity.this, tweet);
                                tweetParentView.addView(tweetView, 7);
                                String imgUrl = tweet.user.profileImageUrl;
                                imgUrl = imgUrl.replaceAll("_normal", ""); // Remove _normal from end of url to get full-size image
                                repList.get(2).imageUrl = imgUrl;
                                Log.d("CongressionalActivity", "Twitter Profile Image URL: " + imgUrl);
                                new DownloadImageTask((ImageButton) findViewById(R.id.repImageButton3))
                                        .execute(imgUrl);
                            }
                            @Override
                            public void failure(TwitterException e) {
                                Log.d("CongressionalActivity", "Couldn't get tweet for some reason");
                                e.printStackTrace();
                            }
                        });
                    }
                    @Override
                    public void failure(TwitterException e) {
                        Log.d("CongressionalActivity", "Couldn't get tweet for some reason");
                        e.printStackTrace();
                    }
                });
            }
            @Override
            public void failure(TwitterException e) {
                Log.d("CongressionalActivity", "Couldn't get tweet for some reason");
                e.printStackTrace();
            }
        });

    }

    public void getProfileImages() {
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        twitterApiClient.getAccountService().verifyCredentials(false,false, new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                String name = userResult.data.name;
                String profilebannerurl = userResult.data.profileBannerUrl;
                String profileurl = userResult.data.profileImageUrl;

            }

            @Override
            public void failure(TwitterException e) {

            }
        });
    }
//    public void getAndShowTweets() {
//        ListView lv = (ListView) findViewById(R.id.twitterListView);
//        final UserTimeline userTimeline = new UserTimeline.Builder()
//                .screenName("ArchonNadia")
//                .maxItemsPerRequest(1)
//                .build();
//        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
//                .setTimeline(userTimeline)
//                .build();
//        lv.setAdapter(adapter);
//        // TODO: Use a more specific parent
////        final ViewGroup parentView = (ViewGroup) getWindow().getDecorView().getRootView();
//        // TODO: Base this Tweet ID on some data from elsewhere in your app
//        long tweetId = 707441229777215488L;
////        String searchQuery = "";
//        TweetUtils.loadTweet(tweetId, new Callback<Tweet>() {
//            @Override
//            public void success(Result<Tweet> result) {
//                TweetView tweetView = new TweetView(CongressionalActivity.this, result.data);
//                tweetParentView.addView(tweetView, 4);
//                tweetView = new TweetView(CongressionalActivity.this, result.data);
//                tweetParentView.addView(tweetView, 3);
//                tweetView = new TweetView(CongressionalActivity.this, result.data);
//                tweetParentView.addView(tweetView, 2);
//            }
//            @Override
//            public void failure(TwitterException exception) {
//                Log.d("TwitterKit", "Load Tweet failure", exception);
//            }
//        });
//    }

    public void testThatWeGotReps() {
        for (int i = 0; i < repList.size(); i++) {
            Log.d("CongressionalActivity", repList.get(i).name);
        }
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
        tv.setText(county + ", " + stateAbr);
        String reptext = buildRepText(repList.get(0));

        ib1.setImageResource(R.drawable.frank);
        ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailedActivity(repList.get(0));
            }
        });
        tv1.setText(reptext);
//        tv1.setText(getString(R.string.frank_congressional_placeholder));
        reptext = buildRepText(repList.get(1));
        ib2.setImageResource(R.drawable.jim);
        ib2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailedActivity(repList.get(1));
            }
        });
        tv2.setText(reptext);
//        tv2.setText(getString(R.string.jim_congressional_placeholder));
        reptext = buildRepText(repList.get(2));
        ib3.setImageResource(R.drawable.james);
        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailedActivity(repList.get(2));
            }
        });
        tv3.setText(reptext);
//        tv3.setText(getString(R.string.james_congressional_placeholder));
//        }
    }

    public String buildRepText(Representative rep) {
        StringBuilder sb = new StringBuilder(32);
        sb.append(rep.name + " (" + rep.party + ")\n\n");
        sb.append("Email: " + rep.emailAddress + "\n");
        sb.append("Website: " + rep.website + "\n");
        return sb.toString();
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

    public void startDetailedActivity(Representative rep) {
        Intent intent = new Intent(getBaseContext(), DetailedActivity.class);
        intent.putExtra("Representative", rep);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startWatchVoteActivity(View view) {
        double obamaPercent = 0;
        double romneyPercent = 0;
        Log.d("CongressionalActivity", "Looking up votes for: " + county + ", " + stateAbr);
        try {
            AssetManager am = getAssets();
            InputStream is = am.open("election-county-2012.json");
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String inputString;
            while ((inputString = streamReader.readLine()) != null) {
                sb.append(inputString);
            }
//            JSONObject obj = new JSONObject(sb.toString());
            JSONArray counties = new JSONArray(sb.toString());
            String countyName = county.replaceAll("County", "").trim();
            for (int i = 0; i < counties.length(); i++) {
                JSONObject countyJSON = counties.getJSONObject(i);
                if (countyJSON.getString("county-name").equalsIgnoreCase(countyName)) {
                    if (countyJSON.getString("state-postal").equalsIgnoreCase(stateAbr)) {
                        obamaPercent = Double.parseDouble(countyJSON.getString("obama-percentage"));
                        romneyPercent = Double.parseDouble(countyJSON.getString("romney-percentage"));
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("CongressionalActivity", "Obama Percentage: " + obamaPercent);
        Log.d("CongressionalActivity", "Romney Percentage: " + romneyPercent);
        Intent watchServiceIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        watchServiceIntent.putExtra("Activity", "VoteActivity");
        watchServiceIntent.putExtra("Votes", obamaPercent + ";" + romneyPercent + ";" + county + ";" + stateAbr);
//        watchServiceIntent.putExtra("RepSet", repSet);
        startService(watchServiceIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

//    public void onClick(View v) {
//        startActivity(new Intent(this, IndexActivity.class));
//        finish();
//
//    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageButton bmImage;

        public DownloadImageTask(ImageButton bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
