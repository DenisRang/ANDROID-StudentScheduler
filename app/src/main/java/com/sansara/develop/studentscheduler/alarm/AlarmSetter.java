package com.sansara.develop.studentscheduler.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.TextView;

import com.sansara.develop.studentscheduler.R;
import com.sansara.develop.studentscheduler.data.EventContract;

/**
 * Created by den on 01.03.2018.
 */

public class AlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmHelper.getInstance().init(context);
        AlarmHelper alarmHelper = AlarmHelper.getInstance();

        // Recover term's alarms
        String[] projection1 = {
                EventContract.TermEntry._ID,
                EventContract.TermEntry.COLUMN_TITLE,
                EventContract.TermEntry.COLUMN_END_TIME,
                EventContract.TermEntry.COLUMN_TIME_STAMP};
        Cursor cursor = context.getContentResolver().query(EventContract.TermEntry.CONTENT_URI, projection1, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int ColumnIndexTitle = cursor.getColumnIndex(EventContract.TermEntry.COLUMN_TITLE);
                int ColumnIndexEnd = cursor.getColumnIndex(EventContract.TermEntry.COLUMN_END_TIME);
                int ColumnIndexTimeStamp = cursor.getColumnIndex(EventContract.TermEntry.COLUMN_TIME_STAMP);

                // Extract out the value from the Cursor for the given column index
                String title = cursor.getString(ColumnIndexTitle);
                long end = cursor.getLong(ColumnIndexEnd);
                long timeStamp = cursor.getLong(ColumnIndexTimeStamp);

                alarmHelper.setAlarm(title, timeStamp, end);
            }
        }
        cursor.close();

        // Recover course's alarms
        String[] projection2 = {
                EventContract.CourseEntry._ID,
                EventContract.CourseEntry.COLUMN_TITLE,
                EventContract.CourseEntry.COLUMN_END_TIME,
                EventContract.CourseEntry.COLUMN_TIME_STAMP};
        cursor = context.getContentResolver().query(EventContract.CourseEntry.CONTENT_URI, projection2, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int ColumnIndexTitle = cursor.getColumnIndex(EventContract.CourseEntry.COLUMN_TITLE);
                int ColumnIndexEnd = cursor.getColumnIndex(EventContract.CourseEntry.COLUMN_END_TIME);
                int ColumnIndexTimeStamp = cursor.getColumnIndex(EventContract.CourseEntry.COLUMN_TIME_STAMP);

                // Extract out the value from the Cursor for the given column index
                String title = cursor.getString(ColumnIndexTitle);
                long end = cursor.getLong(ColumnIndexEnd);
                long timeStamp = cursor.getLong(ColumnIndexTimeStamp);

                alarmHelper.setAlarm(title, timeStamp, end);
            }
        }
        cursor.close();

        // Recover assessment's alarms
        String[] projection3 = {
                EventContract.AssessmentEntry._ID,
                EventContract.AssessmentEntry.COLUMN_TITLE,
                EventContract.AssessmentEntry.COLUMN_END_TIME,
                EventContract.AssessmentEntry.COLUMN_TIME_STAMP};
        cursor = context.getContentResolver().query(EventContract.AssessmentEntry.CONTENT_URI, projection3, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int ColumnIndexTitle = cursor.getColumnIndex(EventContract.AssessmentEntry.COLUMN_TITLE);
                int ColumnIndexEnd = cursor.getColumnIndex(EventContract.AssessmentEntry.COLUMN_END_TIME);
                int ColumnIndexTimeStamp = cursor.getColumnIndex(EventContract.AssessmentEntry.COLUMN_TIME_STAMP);

                // Extract out the value from the Cursor for the given column index
                String title = cursor.getString(ColumnIndexTitle);
                long end = cursor.getLong(ColumnIndexEnd);
                long timeStamp = cursor.getLong(ColumnIndexTimeStamp);

                alarmHelper.setAlarm(title, timeStamp, end);
            }
        }
        cursor.close();

    }
}
