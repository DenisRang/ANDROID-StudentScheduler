package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.data.EventContract;
import com.sansara.develop.studentscheduler.data.EventContract.MentorEntry;
import com.sansara.develop.studentscheduler.fragment.MentorsFragment;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnTouch;


public class EditorMentorActivity extends AppCompatActivity {

    private Uri mCurrentMentorUri;

    @BindViews({R.id.edit_name, R.id.edit_phone, R.id.edit_email})
    EditText[] mEditTexts;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view
     */
    @OnTouch({R.id.edit_name, R.id.edit_phone, R.id.edit_email})
    boolean onChangingMentor() {
        mMentorHasChanged = true;
        return false;
    }

    private boolean mMentorHasChanged = false;
    private long mCourseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_mentor);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentMentorUri = intent.getData();
        mCourseId = intent.getExtras().getLong(MentorsFragment.EXTRA_COURSE_ID);


        if (mCurrentMentorUri == null) {
            setTitle(getString(R.string.title_activity_editor_new_mentor));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.title_activity_editor_edit_mentor));
        }

        Bundle bundle = intent.getBundleExtra(DetailedMentorActivity.EXTRA_EXISTING_MENTOR_BUNDLE);
        if (bundle != null && !bundle.isEmpty()) {
            // Extract out the value from the Bundle for the given column index
            final String[] dataInEditTexts = {bundle.getString(DetailedMentorActivity.EXISTING_MENTOR_NAME),
                    bundle.getString(DetailedMentorActivity.EXISTING_MENTOR_PHONE),
                    bundle.getString(DetailedMentorActivity.EXISTING_MENTOR_EMAIL)};

            // Update the views on the screen with the values from the database
            ButterKnife.Action updateEditTexts = new ButterKnife.Action<EditText>() {
                @Override
                public void apply(@NonNull EditText editText, int index) {
                    if (dataInEditTexts[index] != null && !TextUtils.isEmpty(dataInEditTexts[index]))
                        mEditTexts[index].setText(dataInEditTexts[index]);
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
                saveMentor();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mMentorHasChanged) {
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
        if (!mMentorHasChanged) {
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

    private void saveMentor() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        ButterKnife.Action getTexts = new ButterKnife.Action<EditText>() {
            @Override
            public void apply(@NonNull EditText editText, int index) {
                editText.getText().toString().trim();
            }
        };
        String name = mEditTexts[0].getText().toString().trim();
        String phone = mEditTexts[1].getText().toString().trim();
        String email = mEditTexts[2].getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentMentorUri == null &&
                TextUtils.isEmpty(name) && TextUtils.isEmpty(phone) &&
                TextUtils.isEmpty(email)) {
            // Since no fields were modified, we can return early without creating a new mentor.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and mentor attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(MentorEntry.COLUMN_NAME, name);
        values.put(MentorEntry.COLUMN_PHONE, phone);
        values.put(MentorEntry.COLUMN_EMAIL, email);
        values.put(MentorEntry.COLUMN_COURSE_ID, mCourseId);

        // Determine if this is a new or existing mentor by checking if mCurrentMentorUri is null or not
        if (mCurrentMentorUri == null) {
            // This is a NEW pet, so insert a new mentor into the provider,
            // returning the content URI for the new mentor.
            Uri newUri = getContentResolver().insert(MentorEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.error_insert_mentor_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.msg_insert_mentor_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING mentor, so update the mentor with content URI: mCurrentMentorUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentMentorUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentMentorUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.error_update_mentor_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.msg_update_mentor_successful),
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