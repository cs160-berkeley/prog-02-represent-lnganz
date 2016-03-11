package com.represent.sigma.represent;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity implements SensorEventListener {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private RelativeLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TriggerEventListener mTriggerEventListener;
    private double previousReading = 0;
    static double lastTimeRandomized = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "MainActivity Activity Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (RelativeLayout) findViewById(R.id.container);
//        mTextView = (TextView) findViewById(R.id.text);
//        mClockView = (TextView) findViewById(R.id.clock);

        // My Setup
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        Intent intent = getIntent();
//        int repSet = intent.getIntExtra("RepSet", 0);
        String reps = intent.getStringExtra("Reps");
        if (reps != null) {
//            String[] parsedReps = reps.split(";");
            pager.setAdapter(new MyGridPagerAdapter(this, getFragmentManager(), reps));
        } else {
            Log.d("T", "repSet == null in Watch's MainActivity");
            TextView tv = (TextView) findViewById(R.id.titleText);
            tv.setText(R.string.TitleText);
        }
//        if (repSet != 0) {
//            Log.d("T", "repSet == " + repSet + " in Watch's MainActivity");
//            pager.setAdapter(new MyGridPagerAdapter(this, getFragmentManager(), repSet));
//        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double newReading = event.values[0] + event.values[1] + event.values[2];
        if (previousReading == 0) {
            Log.d("T", "FIRST READING or NO CHANGE");
        }
        else if (Math.abs(previousReading - newReading) > 20) {
            if (System.currentTimeMillis()-lastTimeRandomized < 1000) {
                Log.d("T", "Repeated Accelerometer Reading Ignored");
            } else {
                Log.d("T", "--SENSOR VALUES--");
                Log.d("T", "" + event.values[0]);
                Log.d("T", "" + event.values[1]);
                Log.d("T", "" + event.values[2]);
                System.out.println("OUCHOUCHOUCH");
                lastTimeRandomized = System.currentTimeMillis();
                randomizeThings();
            }
        }
        previousReading = newReading;
    }

    public void randomizeThings() {
        Intent intent = new Intent(getBaseContext(), WatchToPhoneService.class);
//        intent.putExtra("Task", "Randomize");
        intent.putExtra("Activity", "Randomize");
//        intent.putExtra("Representative", "Mark DeSaulnier");
        startService(intent);
//        intent = new Intent(getBaseContext(), VoteActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
//            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
//            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
//            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }
    public void startVoteActivity(View v) {
//        startActivity(new Intent(v.getContext(), VoteActivity.class));
        System.out.println("HELLO MAIN ACTIVITY");
    }
}
