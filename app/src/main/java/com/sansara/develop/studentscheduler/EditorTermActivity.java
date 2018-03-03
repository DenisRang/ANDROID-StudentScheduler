package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.alarm.AlarmHelper;
import com.sansara.develop.studentscheduler.data.EventContract;
import com.sansara.develop.studentscheduler.data.EventContract.TermEntry;
import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;
import com.sansara.develop.studentscheduler.fragment.DatePickerFragment;
import com.sansara.develop.studentscheduler.fragment.DetailedTermFragment;
import com.sansara.develop.studentscheduler.fragment.TimePickerFragment;
import com.sansara.develop.studentscheduler.utils.DateTimeUtils;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


public class EditorTermActivity extends AppCompatActivity {

    @BindViews({R.id.edit_term_title, R.id.edit_term_start_date, R.id.edit_term_start_time,
            R.id.edit_term_end_date, R.id.edit_term_end_time})
    EditText[] mEditTexts;

    @OnClick({R.id.edit_term_start_date, R.id.edit_term_end_date})
    void onClickDate(final EditText editText) {
        if (editText.length() == 0) {
            editText.setText(" ");
        }

        @SuppressWarnings("ValidFragment") DialogFragment datePickerFragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.set(year, monthOfYear, dayOfMonth);
                editText.setText(DateTimeUtils.getDate(dateCalendar.getTimeInMillis()));
                editText.setContentDescription(String.valueOf(dateCalendar.getTimeInMillis()));
            }
        };
        datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
    }

    @OnClick({R.id.edit_term_start_time, R.id.edit_term_end_time})
    void onClickTime(final EditText editText) {
        if (editText.length() == 0) {
            editText.setText(" ");
        }

        @SuppressWarnings("ValidFragment") DialogFragment timePickerFragment = new TimePickerFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar timeCalendar = Calendar.getInstance();
                timeCalendar.set(0, 0, 0, hourOfDay, minute);
                editText.setText(DateTimeUtils.getTime(timeCalendar.getTimeInMillis()));
                editText.setContentDescription(String.valueOf(timeCalendar.getTimeInMillis()));
            }
        };
        timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
    }

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view
     */
    @OnTouch({R.id.edit_term_title, R.id.edit_term_start_date, R.id.edit_term_start_time,
            R.id.edit_term_end_date, R.id.edit_term_end_time})
    boolean onChanging() {
        mHasChanged = true;
        return false;
    }

    private Uri mCurrentUri;
    private long mTimeStamp = -1;
    private boolean mHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_term);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            setTitle(getString(R.string.title_activity_editor_new_term));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.title_activity_editor_edit_term));
        }

        Bundle bundle = intent.getBundleExtra(DetailedTermFragment.EXTRA_EXISTING_TERM_BUNDLE);
        if (bundle != null && !bundle.isEmpty()) {
            // Extract out the value from the Bundle for the given column index
            final String[] dataInEditTexts = {bundle.getString(DetailedTermFragment.EXISTING_TERM_TITLE)
                    , DateTimeUtils.getDate(bundle.getLong(DetailedTermFragment.EXISTING_TERM_START))
                    , DateTimeUtils.getTime(bundle.getLong(DetailedTermFragment.EXISTING_TERM_START))
                    , DateTimeUtils.getDate(bundle.getLong(DetailedTermFragment.EXISTING_TERM_END))
                    , DateTimeUtils.getTime(bundle.getLong(DetailedTermFragment.EXISTING_TERM_END))};

            // Update the views on the screen with the values from the database
            ButterKnife.Action updateEditTexts = new ButterKnife.Action<EditText>() {
                @Override
                public void apply(@NonNull EditText editText, int index) {
                    if (dataInEditTexts[index] != null && !TextUtils.isEmpty(dataInEditTexts[index]))
                        mEditTexts[index].setText(dataInEditTexts[index]);
                }
            };
            ButterKnife.apply(mEditTexts, updateEditTexts);

            mTimeStamp = bundle.getLong(DetailedTermFragment.EXISTING_TERM_TIME_STAMP);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.item_action_save:
                saveTerm();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mHasChanged) {
                    finish();
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                finish();
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void saveTerm() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String title = mEditTexts[0].getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, R.string.error_enter_title, Toast.LENGTH_SHORT).show();
            return;
        }

        Long start = null;
        if (mEditTexts[1].getContentDescription() != null && mEditTexts[2].getContentDescription() != null) {
            Date startDate = new Date(Long.valueOf(mEditTexts[1].getContentDescription().toString()));
            Time startTime = new Time(Long.valueOf(mEditTexts[2].getContentDescription().toString()));
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.set(startDate.getYear() + 1900, startDate.getMonth(), startDate.getDate()
                    , startTime.getHours(), startTime.getMinutes());
            start = calendarStart.getTimeInMillis();
        }

        Long end = null;
        if (mEditTexts[3].getContentDescription() != null && mEditTexts[4].getContentDescription() != null) {
            Date endDate = new Date(Long.valueOf(mEditTexts[3].getContentDescription().toString()));
            Time endTime = new Time(Long.valueOf(mEditTexts[4].getContentDescription().toString()));
            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.set(endDate.getYear() + 1900, endDate.getMonth(), endDate.getDate()
                    , endTime.getHours(), endTime.getMinutes());
            end = calendarEnd.getTimeInMillis();
        }
        if (mTimeStamp == -1) mTimeStamp = new Date().getTime();


        // Create a ContentValues object where column names are the keys,
        // and term attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(TermEntry.COLUMN_TITLE, title);
        values.put(TermEntry.COLUMN_START_TIME, start);
        values.put(TermEntry.COLUMN_END_TIME, end);
        values.put(TermEntry.COLUMN_TIME_STAMP, mTimeStamp);

        if (mCurrentUri == null) {
            Uri newUri = getContentResolver().insert(TermEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_insert_term_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.msg_insert_term_successful),
                        Toast.LENGTH_SHORT).show();
                if (end != null) {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                    if (sharedPrefs.getBoolean(getString(R.string.settings_terms_key), false)) {
                        AlarmHelper.getInstance().setAlarm(title, mTimeStamp, end);
                    }
                }
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.error_update_term_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.msg_update_assessment_successful),
                        Toast.LENGTH_SHORT).show();
                if (end != null) {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
                    if (sharedPrefs.getBoolean(getString(R.string.settings_terms_key), false)) {
                        AlarmHelper.getInstance().removeAlarm(mTimeStamp);
                        AlarmHelper.getInstance().setAlarm(title, mTimeStamp, end);
                    }
                }
            }
        }

        // Exit activity
        finish();
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_unsaved_changes_dialog);
        builder.setPositiveButton(R.string.action_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.action_keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}