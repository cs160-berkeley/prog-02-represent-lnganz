package com.represent.sigma.represent;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.view.CardFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Sigma on 2/27/2016.
 */
public class MyCardFragment extends Fragment implements View.OnClickListener {
    private View fragmentView; // WITH HELP FROM http://stackoverflow.com/questions/28419057/how-to-change-to-another-activity-when-pressing-on-a-gridviewpager
    private View.OnClickListener listener;
    private Button myButton;
    private String repName;
    private String repParty;

//    @Override
//    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        fragmentView = inflater.inflate(R.layout.rep_fragment, container, false);
//        System.out.println("WE HERE HELLO");
//        Log.d("T", "TESTING TESTING onCreateContentView");
//        fragmentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("CardFragment onclick");
//                Log.d("T", "CardFragment onclick");
//            }
//        });
//        return fragmentView;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.rep_fragment, container, false);
        Log.d("T", "MyCardFragment onCreateContentView");
        Bundle args = getArguments();
        repName = args.getString("Name");
        repParty = args.getString("Party");
        TextView tv = (TextView) fragmentView.findViewById(R.id.cardRepName);
        if (repName.equals("Mark DeSaulnier") || repName.equals("Frank Lucas")) {
            tv.setText("Rep. " + repName + " " + repParty);
        } else {
            tv.setText("Sen. " + repName + " " + repParty);
        }

        fragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("T", "CardFragment onclick");
                Intent intent = new Intent(getActivity().getBaseContext(), WatchToPhoneService.class);
                intent.putExtra("Activity", "DetailedActivity");
                intent.putExtra("RepName", repName);
                getActivity().startService(intent);
            }
        });
        return fragmentView;
    }

    @Override
    public void onClick(View v) {
        System.out.println("WE HERE HELLO");
        Log.d("T", "TESTING TESTING onCreateContentView");
    }

}
