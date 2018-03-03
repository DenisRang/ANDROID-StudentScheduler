package com.sansara.develop.studentscheduler.alarm;

import android.app.Application;

/**
 * Created by den on 01.03.2018.
 */

public class MyApplication extends Application {

    private static boolean mActivatyVisible;

    public static boolean isActivityVisible() {
        return mActivatyVisible;
    }

    public static void activityResumed() {
        mActivatyVisible = true;
    }

    public static void activityPaused() {
        mActivatyVisible = false;
    }
}
