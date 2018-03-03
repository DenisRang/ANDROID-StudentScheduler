package com.sansara.develop.studentscheduler;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sansara.develop.studentscheduler.alarm.AlarmHelper;
import com.sansara.develop.studentscheduler.data.EventContract;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    public static class SettingsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference terms = findPreference(getString(R.string.settings_terms_key));
            bindPreferenceSummaryToValue(terms);

            Preference courses = findPreference(getString(R.string.settings_courses_key));
            bindPreferenceSummaryToValue(courses);

            Preference assessments = findPreference(getString(R.string.settings_assessments_key));
            bindPreferenceSummaryToValue(assessments);

        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            Cursor cursor = null;
            String[] projection = {
                    EventContract.TermEntry._ID,
                    EventContract.TermEntry.COLUMN_TITLE,
                    EventContract.TermEntry.COLUMN_END_TIME,
                    EventContract.TermEntry.COLUMN_TIME_STAMP};
            switch (preference.getOrder()) {
                case 0:
                    cursor = getActivity().getContentResolver().query(EventContract.TermEntry.CONTENT_URI, projection, null, null, null);
                    break;
                case 1:
                    cursor = getActivity().getContentResolver().query(EventContract.CourseEntry.CONTENT_URI, projection, null, null, null);
                    break;
                case 2:
                    cursor = getActivity().getContentResolver().query(EventContract.AssessmentEntry.CONTENT_URI, projection, null, null, null);
                    break;
            }
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if ((boolean) value) {
                        int ColumnIndexTitle = cursor.getColumnIndex(EventContract.TermEntry.COLUMN_TITLE);
                        int ColumnIndexEnd = cursor.getColumnIndex(EventContract.TermEntry.COLUMN_END_TIME);
                        int ColumnIndexTimeStamp = cursor.getColumnIndex(EventContract.TermEntry.COLUMN_TIME_STAMP);

                        // Extract out the value from the Cursor for the given column index
                        String title = cursor.getString(ColumnIndexTitle);
                        long end = cursor.getLong(ColumnIndexEnd);
                        long timeStamp = cursor.getLong(ColumnIndexTimeStamp);

                        AlarmHelper.getInstance().setAlarm(title, timeStamp, end);
                    } else {
                        int ColumnIndexTimeStamp = cursor.getColumnIndex(EventContract.TermEntry.COLUMN_TIME_STAMP);
                        long timeStamp = cursor.getLong(ColumnIndexTimeStamp);
                        AlarmHelper.getInstance().removeAlarm(timeStamp);
                    }
                }
                cursor.close();
            }
            return true;
        }
    }

}