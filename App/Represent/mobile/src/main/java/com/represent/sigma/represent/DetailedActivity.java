package com.represent.sigma.represent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DetailedActivity extends AppCompatActivity {

    private final int COMMITTEE_LIMIT = 3;
    private final int BILL_LIMIT = 3;
    private Representative rep;
    private String committeesString;
    private String billsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        //My Setup
        Intent intent = getIntent();
        rep = intent.getParcelableExtra("Representative");
//        setTitle("Mark Desaulnier (D)");
        setTitle(rep.name + " (" + rep.party + ")");

        getDetails();

        String repName = intent.getStringExtra("RepName");
//        ImageView iv = (ImageView) findViewById(R.id.repImage);
        new DownloadImageTask((ImageView) findViewById(R.id.repImage))
                .execute(rep.imageUrl);
    }

    public void getDetails() {
        try {
//        congress.api.sunlightfoundation.com/committees?member_ids=C001036&apikey=83097d17b7e24cf7bf3e8da5dd132ff0
            String committeesPathStart = "https://congress.api.sunlightfoundation.com/committees?member_ids=";
            String member_id=rep.bioguideId.trim();
            String committeesPathEnd = "&apikey=83097d17b7e24cf7bf3e8da5dd132ff0"; // TODO: HIDE API KEY
            URL committeesUrl = new URL(committeesPathStart + member_id + committeesPathEnd);
            Log.d("DetailedActivity", "Requesting Committees from Sunlight Foundation");
            new GetCommitteeRequestTask().execute(committeesUrl);

//        congress.api.sunlightfoundation.com/bills?sponsor_id=C001036&apikey=83097d17b7e24cf7bf3e8da5dd132ff0
            String billsPathStart = "https://congress.api.sunlightfoundation.com/bills?sponsor_id=";
            String sponsor_id = rep.bioguideId.trim();
            String billsPathEnd = "&apikey=83097d17b7e24cf7bf3e8da5dd132ff0"; // TODO: HIDE API KEY
            URL billsUrl = new URL(billsPathStart + sponsor_id + committeesPathEnd);
            Log.d("DetailedActivity", "Requesting Bills from Sunlight Foundation");
            new GetBillsRequestTask().execute(billsUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetCommitteeRequestTask extends AsyncTask<URL, Integer, String> {
        @Override
        protected String doInBackground(URL... urls) {
            return handleUrls(urls);
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {}
        @Override
        protected void onPostExecute(String response) {
            Log.d("DetailedActivity", "Response: " + response);
            parseCommitteeJSONString(response);
        }
    }

    private class GetBillsRequestTask extends AsyncTask<URL, Integer, String> {
        @Override
        protected String doInBackground(URL... urls) {
            return handleUrls(urls);
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {}
        @Override
        protected void onPostExecute(String response) {
            Log.d("DetailedActivity", "Response: " + response);
            parseBillsJSONString(response);
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

    public void parseCommitteeJSONString(String jsonString) {
        ArrayList<String> parsedCommittees = new ArrayList<String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray committees = obj.getJSONArray("results");
            Log.d("DetailedActivity", "Committees: " + committees.toString());
            for (int i = 0; i < committees.length(); i++) {
                JSONObject committeeJSON = committees.getJSONObject(i);
                String cName = committeeJSON.getString("name");
                Log.d("DetailedActivity", "Committee: " + cName);
                parsedCommittees.add(cName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateCommittees(parsedCommittees);
    }

    public void parseBillsJSONString(String jsonString) {
        ArrayList<String> parsedBills = new ArrayList<String>();
        try {
            JSONObject obj = new JSONObject(jsonString);
            JSONArray bills = obj.getJSONArray("results");
            Log.d("DetailedActivity", "Bill: " + bills.toString());
            for (int i = 0; i < bills.length(); i++) {
                JSONObject billsJSON = bills.getJSONObject(i);
                String bName = billsJSON.getString("official_title");
                String date = billsJSON.getString("introduced_on");
                Log.d("DetailedActivity", "Bill: " + bName);
                parsedBills.add(date + ": " + bName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateBills(parsedBills);
    }
    public void updateCommittees(ArrayList<String> committees) {
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < committees.size() && i < COMMITTEE_LIMIT; i++) {
            sb.append(committees.get(i) + "\n");
        }
        committeesString = sb.toString();
        updateText();
    }

    public void updateBills(ArrayList<String> bills) {
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0; i < bills.size() && i < BILL_LIMIT; i++) {
            sb.append(bills.get(i) + "\n");
        }
        billsString = sb.toString();
        updateText();
    }

    public void updateText() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("Congressional Term Ends: " + rep.endDate + "\n\n");
        sb.append("--Committees--\n");
        sb.append(committeesString);
        sb.append("\n--Recently Sponsored Bills--\n");
        sb.append(billsString);

        TextView tv = (TextView) findViewById(R.id.repDetailedText);
        tv.setText(sb.toString());
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
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
