package com.represent.sigma.represent;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.wearable.view.CardFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Sigma on 2/27/2016.
 */
public class MyCardFragment extends Fragment {
    private View fragmentView; // WITH HELP FROM http://stackoverflow.com/questions/28419057/how-to-change-to-another-activity-when-pressing-on-a-gridviewpager
    private View.OnClickListener listener;
    private Button myButton;
    private String repName;
    private String repParty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.rep_fragment, container, false);
        Log.d("T", "MyCardFragment onCreateContentView");
        Bundle args = getArguments();
        repName = args.getString("Name");
        repParty = args.getString("Party");
        TextView tv = (TextView) fragmentView.findViewById(R.id.cardRepName);
        tv.setBackgroundResource(R.drawable.textview_circular_design);
        String formattedString;
        if (repParty.equalsIgnoreCase("(D)")) {
            formattedString = "<h2>" + repName + "</h2>Democrat";
            tv.setText(formattedString);
        } else if (repParty.equalsIgnoreCase("(R)")) {
            formattedString = "<h2>" + repName + "</h2>Republican";
//            tv.setText(repName + "\nRepublican");
        } else if (repParty.equalsIgnoreCase("(I)")) {
            formattedString = "<h2>" + repName + "</h2>Independent";
//            tv.setText(repName + "\nIndependent");
        } else {
            formattedString = "<h2>" + repName + "</h2>";
        }
        tv.setText(Html.fromHtml(formattedString));

        fragmentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("T", "CardFragment onclick");
                String nameToSend;
                if (repName.contains("Representative")){
                    nameToSend = repName.replaceAll("Representative<br>", "");
                } else {
                    nameToSend = repName.replaceAll("Senator<br>", "");
                }
                Log.d("T", "Rep Name: " + nameToSend);
                Intent intent = new Intent(getActivity().getBaseContext(), WatchToPhoneService.class);
                intent.putExtra("Activity", "DetailedActivity");
                intent.putExtra("RepName", nameToSend);
                getActivity().startService(intent);
            }
        });
        return fragmentView;
    }

}
