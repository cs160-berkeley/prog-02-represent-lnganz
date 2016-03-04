package com.represent.sigma.represent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class DetailedActivity extends AppCompatActivity {

    private Representative rep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        //My Setup
        setTitle("Mark Desaulnier (D)");

        Intent intent = getIntent();
        String repName = intent.getStringExtra("RepName");
        ImageView iv = (ImageView) findViewById(R.id.repImage);
        if (repName.equals("Mark DeSaulnier")) {
            setTitle("Mark DeSaulnier (D)");
            iv.setImageResource(R.drawable.mark);
        } else if (repName.equals("Dianna Feinstein")) {
            setTitle("Dianne Feinstein (D)");
            iv.setImageResource(R.drawable.dianne);
        } else if (repName.equals("James Lankford")) {
            setTitle("James Lankford (R)");
            iv.setImageResource(R.drawable.james);
        }
        //setTitle(rep.name);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
