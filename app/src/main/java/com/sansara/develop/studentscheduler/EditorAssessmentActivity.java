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
import android.widget.EditText;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnTouch;


public class EditorAssessmentActivity extends AppCompatActivity {

    private Uri mCurrentAssessmentUri;

    @BindViews({R.id.edit_assessment_title, R.id.edit_assessment_start_time, R.id.edit_assessment_end_time})
    EditText[] mEditTexts;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view
     */
    @OnTouch({R.id.edit_assessment_title, R.id.edit_assessment_start_time, R.id.edit_assessment_end_time})
    boolean onChangingAssessment() {
        mAssessmentHasChanged = true;
        return false;
    }

    private boolean mAssessmentHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_assessment);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentAssessmentUri = intent.getData();

        if (mCurrentAssessmentUri == null) {
            setTitle(getString(R.string.title_activity_editor_new_assessment));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.title_activity_editor_edit_assessment));
        }

        Bundle bundle = intent.getBundleExtra(DetailedAssessmentActivity.EXTRA_EXISTING_ASSESSMENT_BUNDLE);
        if (bundle != null && !bundle.isEmpty()) {
            // Extract out the value from the Bundle for the given column index
            final String[] dataInEditTexts = {bundle.getString(DetailedAssessmentActivity.EXISTING_ASSESSMENT_TITLE),
                    bundle.getString(DetailedAssessmentActivity.EXISTING_ASSESSMENT_START),
                    bundle.getString(DetailedAssessmentActivity.EXISTING_ASSESSMENT_END)};

            // Update the views on the screen with the values from the database
            ButterKnife.Action updateEditTexts = new ButterKnife.Action<EditText>() {
                @Override
                public void apply(@NonNull EditText editText, int index) {
                    if (dataInEditTexts[index] != null && !TextUtils.isEmpty(dataInEditTexts[index]))
                        mEditTexts[0].setText(dataInEditTexts[index]);
                }
            };
            ButterKnife.apply(mEditTexts, updateEditTexts);
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
                saveAssessment();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mAssessmentHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorAssessmentActivity.this);
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
                                NavUtils.navigateUpFromSameTask(EditorAssessmentActivity.this);
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
        if (!mAssessmentHasChanged) {
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

    private void saveAssessment() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        ButterKnife.Action getTexts = new ButterKnife.Action<EditText>() {
            @Override
            public void apply(@NonNull EditText editText, int index) {
                editText.getText().toString().trim();
            }
        };
        String title = mEditTexts[0].getText().toString().trim();
        String start = mEditTexts[1].getText().toString().trim();
        String end = mEditTexts[2].getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentAssessmentUri == null &&
                TextUtils.isEmpty(title) && TextUtils.isEmpty(start) &&
                TextUtils.isEmpty(end)) {
            // Since no fields were modified, we can return early without creating a new assessment.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and assessment attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(AssessmentEntry.COLUMN_TITLE, title);
        values.put(AssessmentEntry.COLUMN_START_TIME, start);
        values.put(AssessmentEntry.COLUMN_END_TIME, end);

        // Determine if this is a new or existing assessment by checking if mCurrentAssessmentUri is null or not
        if (mCurrentAssessmentUri == null) {
            // This is a NEW pet, so insert a new assessment into the provider,
            // returning the content URI for the new assessment.
            Uri newUri = getContentResolver().insert(AssessmentEntry.CONTENT_URI, values);

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
            int rowsAffected = getContentResolver().update(mCurrentAssessmentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.error_update_assessment_failed),
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