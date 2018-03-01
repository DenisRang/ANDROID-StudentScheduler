package com.sansara.develop.studentscheduler.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by den on 01.03.2018.
 */

public class AlarmHelper {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_TIME_STAMP = "EXTRA_TIME_STAMP";

    private static AlarmHelper mInstance;
    private Context mContext;
    private AlarmManager mAlarmManager;

    public static AlarmHelper getInstance() {
        if (mInstance == null) {
            mInstance = new AlarmHelper();
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(String title, long timeStamp, long date) {
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(AlarmHelper.EXTRA_TITLE, title);
        intent.putExtra(AlarmHelper.EXTRA_TIME_STAMP, timeStamp);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext()
                , (int) timeStamp, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
    }

    public void removeAlarm(long timeStamp) {
        Intent intent = new Intent(mContext, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext
                , (int) timeStamp, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mAlarmManager.cancel(pendingIntent);
    }
}















