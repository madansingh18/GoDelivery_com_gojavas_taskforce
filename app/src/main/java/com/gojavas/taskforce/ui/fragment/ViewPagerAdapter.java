package com.gojavas.taskforce.ui.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by GJS280 on 21/4/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence mTitles[];
    private int mNumberOfTabs;

    public ViewPagerAdapter(FragmentManager fragmentManager, CharSequence titles[], int numberOfTabs) {
        super(fragmentManager);
        this.mTitles = titles;
        this.mNumberOfTabs = numberOfTabs;

    }

    /**
     * This method returns the fragment for every position
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            // if the position is 0 we are returning the First tab
            PendingFragment pendingFragment = new PendingFragment();
            return pendingFragment;
        } else if(position == 1) {
            // if the position is 1 we are returning the Third tab
            SuccessFragment successFragment = new SuccessFragment();
            return successFragment;
        } else {
            // if the position is 2 we are returning the Fourth tab
            FailedFragment failedFragment = new FailedFragment();
            return failedFragment;
        }
    }

    /**
     * This method returns all the titles in the tabs
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    /**
     * This methods returns the number of tabs
     * @return
     */
    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}
