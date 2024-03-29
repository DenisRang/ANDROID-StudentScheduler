package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.alarm.AlarmHelper;
import com.sansara.develop.studentscheduler.data.EventContract;
import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;
import com.sansara.develop.studentscheduler.utils.DateTimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailedAssessmentActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_EXISTING_ASSESSMENT_BUNDLE = "EXTRA_EXISTING_ASSESSMENT_BUNDLE";
    public static final String EXISTING_ASSESSMENT_TITLE = "EXISTING_ASSESSMENT_TITLE";
    public static final String EXISTING_ASSESSMENT_START = "EXISTING_ASSESSMENT_START";
    public static final String EXISTING_ASSESSMENT_END = "EXISTING_ASSESSMENT_END";
    public static final String EXISTING_ASSESSMENT_TIME_STAMP = "EXISTING_ASSESSMENT_TIME_STAMP";
    public static final String EXISTING_ASSESSMENT_COURSE_ID = "EXISTING_ASSESSMENT_COURSE_ID";

    private static final int EXISTING_ASSESSMENT_LOADER = 0;

    private Uri mCurrentAssessmentUri;
    private String TAG = DetailedAssessmentActivity.class.getSimpleName();

    @BindView(R.id.text_fragment_detailed)
    TextView mTextView;
    private Bundle mBundleExistingAssessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detailed);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentAssessmentUri = intent.getData();

        setTitle(getString(R.string.title_activity_detailed_assessment));
        getLoaderManager().initLoader(EXISTING_ASSESSMENT_LOADER, null, this);

        mBundleExistingAssessment = new Bundle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detailed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Change" menu option
            case R.id.item_action_redo:
                Intent intent = new Intent(DetailedAssessmentActivity.this, EditorAssessmentActivity.class);
                intent.putExtra(EXTRA_EXISTING_ASSESSMENT_BUNDLE, mBundleExistingAssessment);
                intent.setData(mCurrentAssessmentUri);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.item_action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AssessmentEntry._ID,
                AssessmentEntry.COLUMN_TITLE,
                AssessmentEntry.COLUMN_START_TIME,
                AssessmentEntry.COLUMN_END_TIME,
                AssessmentEntry.COLUMN_TIME_STAMP,
                AssessmentEntry.COLUMN_COURSE_ID};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                mCurrentAssessmentUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of attributes that we're interested in
            int ColumnIndexTitle = cursor.getColumnIndex(AssessmentEntry.COLUMN_TITLE);
            int ColumnIndexStart = cursor.getColumnIndex(AssessmentEntry.COLUMN_START_TIME);
            int ColumnIndexEnd = cursor.getColumnIndex(AssessmentEntry.COLUMN_END_TIME);
            int ColumnIndexTimeStamp = cursor.getColumnIndex(AssessmentEntry.COLUMN_TIME_STAMP);
            int ColumnIndexCourseId = cursor.getColumnIndex(AssessmentEntry.COLUMN_COURSE_ID);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(ColumnIndexTitle);
            long start = cursor.getLong(ColumnIndexStart);
            long end = cursor.getLong(ColumnIndexEnd);
            long stamp = cursor.getLong(ColumnIndexTimeStamp);
            int courseId = cursor.getInt(ColumnIndexCourseId);

            mBundleExistingAssessment.putString(EXISTING_ASSESSMENT_TITLE, title);
            mBundleExistingAssessment.putLong(EXISTING_ASSESSMENT_START, start);
            mBundleExistingAssessment.putLong(EXISTING_ASSESSMENT_END, end);
            mBundleExistingAssessment.putLong(EXISTING_ASSESSMENT_TIME_STAMP, stamp);
            mBundleExistingAssessment.putInt(EXISTING_ASSESSMENT_COURSE_ID, courseId);

            String[] projection = {
                    EventContract.CourseEntry._ID,
                    EventContract.CourseEntry.COLUMN_TITLE};
            Cursor cursorForCourse = getContentResolver()
                    .query(ContentUris.withAppendedId(EventContract.CourseEntry.CONTENT_URI, courseId), projection, null, null, null);
            cursorForCourse.moveToFirst();
            int columnIndexCourseTitle = cursorForCourse.getColumnIndex(EventContract.CourseEntry.COLUMN_TITLE);
            String course;
            try {
                course = cursorForCourse.getString(columnIndexCourseTitle);
            } catch (Exception e) {
                course = getString(R.string.not_assigned);
            }
            cursorForCourse.close();

            mTextView.setText(getString(R.string.title_hint) + ":   " + title + "\n"
                    + getString(R.string.start) + "   " + DateTimeUtils.getFullDate(start) + "\n"
                    + getString(R.string.end) + "   " + DateTimeUtils.getFullDate(end) + "\n"
                    + getString(R.string.msg_spinner_courses) + "   " + course);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the fields.
        mTextView.setText("");
    }

    private void deleteAssessment() {
        if (mCurrentAssessmentUri != null) {
            int rowAffected = getContentResolver().delete(mCurrentAssessmentUri, null, null);
            if (rowAffected == 0) {
                Toast.makeText(this, R.string.error_delete_assessment_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.msg_delete_assessment_successful, Toast.LENGTH_SHORT).show();
                AlarmHelper.getInstance().removeAlarm(mBundleExistingAssessment.getLong(EXISTING_ASSESSMENT_TIME_STAMP));
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_delete_assessment_dialog);
        builder.setPositiveButton(R.string.action_dialog_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAssessment();
                finish();
            }
        });
        builder.setNegativeButton(R.string.action_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "         onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "         onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "         onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "         onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "         onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "         onDestroy");
    }
}