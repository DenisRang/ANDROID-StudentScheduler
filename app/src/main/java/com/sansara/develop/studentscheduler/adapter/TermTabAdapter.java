package com.sansara.develop.studentscheduler.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.sansara.develop.studentscheduler.fragment.AssessmentsFragment;
import com.sansara.develop.studentscheduler.fragment.CoursesFragment;
import com.sansara.develop.studentscheduler.fragment.DetailedTermFragment;
import com.sansara.develop.studentscheduler.fragment.MentorsFragment;

/**
 * Created by den on 25.02.2018.
 */

public class TermTabAdapter extends FragmentStatePagerAdapter {
    public static String TERM_ID = "TERM_ID";
    private int mNumberOfTabs;
    private long mTermId;


    public TermTabAdapter(FragmentManager fm, int numberOfTabs, long termId) {
        super(fm);
        mNumberOfTabs = numberOfTabs;
        mTermId = termId;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                fragment = new DetailedTermFragment();
                return fragment;
            case 1:
                fragment = new CoursesFragment();
                bundle.putLong(TERM_ID, mTermId);
                fragment.setArguments(bundle);
                return fragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}
