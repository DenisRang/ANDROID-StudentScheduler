package com.sansara.develop.studentscheduler.adapter;



import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.sansara.develop.studentscheduler.fragment.AssessmentsFragment;
import com.sansara.develop.studentscheduler.fragment.DetailedCourseFragment;

/**
 * Created by den on 25.02.2018.
 */

public class CourseTabAdapter extends FragmentStatePagerAdapter {
    private int mNumberOfTabs;


    public CourseTabAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        mNumberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DetailedCourseFragment();
            case 1:
            case 2:
                return new AssessmentsFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return mNumberOfTabs;
    }
}
