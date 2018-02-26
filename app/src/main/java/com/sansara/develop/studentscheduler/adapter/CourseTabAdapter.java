package com.sansara.develop.studentscheduler.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.sansara.develop.studentscheduler.fragment.AssessmentsFragment;
import com.sansara.develop.studentscheduler.fragment.DetailedCourseFragment;
import com.sansara.develop.studentscheduler.fragment.MentorsFragment;

/**
 * Created by den on 25.02.2018.
 */

public class CourseTabAdapter extends FragmentStatePagerAdapter {
    public static String COURSE_ID = "COURSE_ID";
    private int mNumberOfTabs;
    private long mCourseId;


    public CourseTabAdapter(FragmentManager fm, int numberOfTabs, long courseId) {
        super(fm);
        mNumberOfTabs = numberOfTabs;
        mCourseId = courseId;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                fragment = new DetailedCourseFragment();
                return fragment;
            case 1:
                fragment = new AssessmentsFragment();
                bundle.putLong(COURSE_ID, mCourseId);
                fragment.setArguments(bundle);
                return fragment;
            case 2:
                fragment = new MentorsFragment();
                bundle.putLong(COURSE_ID, mCourseId);
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
