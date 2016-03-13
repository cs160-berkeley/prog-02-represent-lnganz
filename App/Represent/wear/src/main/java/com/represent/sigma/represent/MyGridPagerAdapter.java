package com.represent.sigma.represent;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.view.Gravity;
import android.view.View;

import java.util.List;

/**
 * Created by Sigma on 2/27/2016.
 * MOSTLY COPIED FROM http://developer.android.com/training/wearables/ui/2d-picker.html
 */
public class MyGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private List mRows;
    private final Page[][] PAGES;

    public MyGridPagerAdapter(Context ctx, FragmentManager fm, String reps) {
        super(fm);
        mContext = ctx;
        // A simple container for static data in each page
        // Create a static set of pages in a 2D array
        PAGES = new Page[1][3];
        String[] repList = reps.split(";");
        if (repList[0].equalsIgnoreCase("house")) {
            PAGES[0][0] = new Page("Representative<br>" + repList[1], repList[2], R.drawable.test50x50, R.drawable.flag);
        } else {
            PAGES[0][0] = new Page("Senator<br>" + repList[1], repList[2], R.drawable.test50x50, R.drawable.flag);
        }
        if (repList[3].equalsIgnoreCase("house")) {
            PAGES[0][1] = new Page("Representative<br>" + repList[4], repList[5], R.drawable.test50x50, R.drawable.flag);
        } else {
            PAGES[0][1] = new Page("Senator<br>" + repList[4], repList[5], R.drawable.test50x50, R.drawable.flag);
        }
        if (repList[6].equalsIgnoreCase("house")) {
            PAGES[0][2] = new Page("Representative<br>" + repList[7], repList[8], R.drawable.test50x50, R.drawable.flag);
        } else {
            PAGES[0][2] = new Page("Senator<br>" + repList[7], repList[8], R.drawable.test50x50, R.drawable.flag);

        }
    }


    static final int[] BG_IMAGES = new int[] {
            R.drawable.card_background, R.drawable.card_background
    };

    private static class Page {
        // static resources
        String title;
        String text;
        int repIndex;
        int iconId;
        int backgroundId;


        private Page(String title, String text, int iconRes, int backgroundId) {
            this.title = title;
            this.text = text;
            this.iconId = iconRes;
            this.backgroundId = backgroundId;
        }

    }

    // Obtain the number of pages (vertical)
    @Override
    public int getRowCount() {
        return PAGES.length;
    }

    // Obtain image for page
    @Override
    public Drawable getBackgroundForPage(int row, int col) {
        Page page = PAGES[row][col];
        return mContext.getResources().getDrawable(page.backgroundId, null);
    }

    @Override
    public Fragment getFragment(int row, int col) {
        Page page = PAGES[row][col];
        MyCardFragment fragment = new MyCardFragment();
        Bundle args = new Bundle();
        args.putString("Name", page.title);
        args.putString("Party", page.text);
        fragment.setArguments(args);

        return fragment;
    }

    // Obtain the background image for the row
    @Override
    public Drawable getBackgroundForRow(int row) {
        return mContext.getResources().getDrawable(
                (BG_IMAGES[row % BG_IMAGES.length]), null);
    }

    // Obtain the number of pages (horizontal)
    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }

};