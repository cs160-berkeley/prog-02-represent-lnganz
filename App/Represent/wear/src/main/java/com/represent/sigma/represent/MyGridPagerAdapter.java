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

    public MyGridPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        mContext = ctx;
        // A simple container for static data in each page
        // Create a static set of pages in a 2D array
        PAGES = new Page[1][3];
        PAGES[0][0] = new Page("Mark DeSaulnier", "(D)", R.drawable.test50x50, R.drawable.mark);
        PAGES[0][1] = new Page("Diane Feinstein", "(D)", R.drawable.test50x50, R.drawable.diane);
        PAGES[0][2] = new Page("Barbara Boxer", "(D)", R.drawable.test50x50, R.drawable.barbara);
//        PAGES[1][0] = new Page(null, "TEXT1-0", R.drawable.ic_full_cancel, R.drawable.barbara);
    }

    public MyGridPagerAdapter(Context ctx, FragmentManager fm, int repSet) {
        super(fm);
        mContext = ctx;
        // A simple container for static data in each page
        // Create a static set of pages in a 2D array
        PAGES = new Page[1][3];
        if (repSet == 1) {
            PAGES[0][0] = new Page("Mark DeSaulnier", "(D)", R.drawable.test50x50, R.drawable.mark);
            PAGES[0][1] = new Page("Diane Feinstein", "(D)", R.drawable.test50x50, R.drawable.diane);
            PAGES[0][2] = new Page("Barbara Boxer", "(D)", R.drawable.test50x50, R.drawable.barbara);
        } else if (repSet == 2) {
            PAGES[0][0] = new Page("Frank Lucas", "(R)", R.drawable.test50x50, R.drawable.frank);
            PAGES[0][1] = new Page("Jim Inhofe", "(R)", R.drawable.test50x50, R.drawable.jim);
            PAGES[0][2] = new Page("James Lankford", "(R)", R.drawable.test50x50, R.drawable.james);
        }
//        PAGES[1][0] = new Page(null, "TEXT1-0", R.drawable.ic_full_cancel, R.drawable.barbara);
    }


    static final int[] BG_IMAGES = new int[] {
            R.drawable.card_background, R.drawable.card_background
    };

    private static class Page {
        // static resources
        String title;
        String text;
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
//        CardFragment fragment = MyCardFragment.create(page.title, page.text);
//        CardFragment fragment = MyCardFragment.create(page.title, page.text);
        MyCardFragment fragment = new MyCardFragment();
        Bundle args = new Bundle();
        args.putString("Name", page.title);
        args.putString("Party", page.text);
        fragment.setArguments(args);
//        fragment.setCardGravity(Gravity.BOTTOM);
//        fragment.setExpansionEnabled(true);
//        fragment.setExpansionDirection(CardFragment.EXPAND_DOWN);
//        fragment.setExpansionFactor(3.0f);

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