package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.data.EventContract;
import com.sansara.develop.studentscheduler.data.EventContract.CourseEntry;
import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;
import com.sansara.develop.studentscheduler.fragment.DetailedCourseFragment;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnTouch;


public class EditorCourseActivity extends AppCompatActivity {

    private Uri mCurrentUri;
    private int mStatus = CourseEntry.STATUS_UNKNOWN;

    @BindViews({R.id.edit_course_title, R.id.edit_course_start_date, R.id.edit_course_start_time,
            R.id.edit_course_end_date, R.id.edit_course_end_time})
    EditText[] mEditTexts;
    @BindView(R.id.spinner_for_terms)
    Spinner mSpinnerTerms;
    @BindView(R.id.spinner_status)
    Spinner mSpinnerStatus;
    @BindView(R.id.edit_note)
    EditText mEditTextNote;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view
     */
    @OnTouch({R.id.edit_course_title, R.id.edit_course_start_date, R.id.edit_course_start_time,
            R.id.edit_course_end_date, R.id.edit_course_end_time})
    boolean onChanging() {
        mHasChanged = true;
        return false;
    }

    private boolean mHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_course);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if (mCurrentUri == null) {
            setTitle(getString(R.string.title_activity_editor_new_course));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.title_activity_editor_edit_course));
        }

        Bundle bundle = intent.getBundleExtra(DetailedCourseFragment.EXTRA_EXISTING_COURSE_BUNDLE);
        if (bundle != null && !bundle.isEmpty()) {
            // Extract out the value from the Bundle for the given column index
            final String[] dataInEditTexts = {bundle.getString(DetailedCourseFragment.EXISTING_COURSE_TITLE),
                    bundle.getString(DetailedCourseFragment.EXISTING_COURSE_START),
                    bundle.getString(DetailedCourseFragment.EXISTING_COURSE_END)};

            // Update the views on the screen with the values from the database
            ButterKnife.Action updateEditTexts = new ButterKnife.Action<EditText>() {
                @Override
                public void apply(@NonNull EditText editText, int index) {
                    if (dataInEditTexts[index] != null && !TextUtils.isEmpty(dataInEditTexts[index]))
                        mEditTexts[index].setText(dataInEditTexts[index]);
                }
            };
            ButterKnife.apply(mEditTexts[0], updateEditTexts);  //TODO: delete [0]
        }
        setupSpinnerStatus();
        setupSpinnerTerms();
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
                saveCourse();
                // Exit activity
                finish();
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

    /**
     * Setup the dropdown spinner that allows the user to select the status of the course.
     */
    private void setupSpinnerStatus() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter statusSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_status_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        statusSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSpinnerStatus.setAdapter(statusSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSpinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.status_in_progress))) {
                        mStatus = CourseEntry.STATUS_IN_PROGRESS;
                    } else if (selection.equals(getString(R.string.status_completed))) {
                        mStatus = CourseEntry.STATUS_COMPLETED;
                    } else if (selection.equals(getString(R.string.status_dropped))) {
                        mStatus = CourseEntry.STATUS_DROPPED;
                    } else if (selection.equals(getString(R.string.status_plan_to_take))) {
                        mStatus = CourseEntry.STATUS_PLAN_TO_TAKE;
                    } else {
                        mStatus = CourseEntry.STATUS_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStatus = CourseEntry.STATUS_UNKNOWN;
            }
        });
    }

    private void setupSpinnerTerms() {
        String[] projection = {
                EventContract.TermEntry._ID,
                EventContract.TermEntry.COLUMN_TITLE};
        Cursor cursor = getContentResolver().query(EventContract.TermEntry.CONTENT_URI, projection, null, null, null);

        if (cursor.getCount() > 0) {
            String[] from = new String[]{EventContract.TermEntry.COLUMN_TITLE};
            // create an array of the display item we want to bind our data to
            int[] to = new int[]{android.R.id.text1};
            SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                    cursor, from, to);
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerTerms.setAdapter(mAdapter);
        }

        mSpinnerTerms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView parent, View view,
                                       int pos, long log) {

                Cursor c = (Cursor) parent.getItemAtPosition(pos);
                int id = c.getInt(c.getColumnIndexOrThrow(EventContract.TermEntry.COLUMN_TITLE));
            }

            public void onNothingSelected(AdapterView arg0) {

            }
        });
    }

    private void saveCourse() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String title = mEditTexts[0].getText().toString().trim();
        String start = mEditTexts[1].getText().toString().trim();
        String end = mEditTexts[2].getText().toString().trim();

        // Check if this is supposed to be a new course
        // and check if all the fields in the editor are blank
        if (mCurrentUri == null &&
                TextUtils.isEmpty(title) && TextUtils.isEmpty(start) &&
                TextUtils.isEmpty(end) && mStatus == CourseEntry.STATUS_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new assessment.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and assessment attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(CourseEntry.COLUMN_TITLE, title);
        values.put(CourseEntry.COLUMN_START_TIME, start);
        values.put(CourseEntry.COLUMN_END_TIME, end);
        values.put(CourseEntry.COLUMN_STATUS, mStatus);

        // Determine if this is a new or existing assessment by checking if mCurrentAssessmentUri is null or not
        if (mCurrentUri == null) {
            // This is a NEW pet, so insert a new assessment into the provider,
            // returning the content URI for the new assessment.
            Uri newUri = getContentResolver().insert(CourseEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_insert_assessment_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.msg_insert_assessment_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING assessment, so update the assessment with content URI: mCurrentAssessmentUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentAssessmentUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.error_update_course_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.msg_update_assessment_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
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