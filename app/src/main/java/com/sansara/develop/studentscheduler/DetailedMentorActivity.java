package com.sansara.develop.studentscheduler;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.adapter.CourseTabAdapter;
import com.sansara.develop.studentscheduler.data.EventContract.MentorEntry;
import com.sansara.develop.studentscheduler.fragment.MentorsFragment;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DetailedMentorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_EXISTING_MENTOR_BUNDLE = "existing_mentor_bundle";
    public static final String EXISTING_MENTOR_NAME = "existing_mentor_name";
    public static final String EXISTING_MENTOR_PHONE = "existing_mentor_phone";
    public static final String EXISTING_MENTOR_EMAIL = "existing_mentor_email";
    private static final int EXISTING_MENTOR_LOADER = 0;


    @BindViews({R.id.text_name, R.id.text_phone, R.id.text_email})
    TextView[] mTextViews;

    @OnClick({R.id.button_call})
    void call(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + mTextViews[1].getText().toString()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
            return;
        }
    }
    @OnClick({R.id.button_write})
    void write(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mTextViews[2].getText().toString()});
        emailIntent.setType("text/plain");
        startActivity(emailIntent);
    }

    private Uri mCurrentMentorUri;
    private String TAG = DetailedMentorActivity.class.getSimpleName();
    private Bundle mBundleExistingMentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_mentor);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentMentorUri = intent.getData();

        setTitle(getString(R.string.title_activity_detailed_mentor));
        getLoaderManager().initLoader(EXISTING_MENTOR_LOADER, null, this);

        mBundleExistingMentor = new Bundle();
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
                Intent intent = new Intent(DetailedMentorActivity.this, EditorMentorActivity.class);
                intent.putExtra(EXTRA_EXISTING_MENTOR_BUNDLE, mBundleExistingMentor);
                intent.putExtra(MentorsFragment.EXTRA_COURSE_ID, getIntent().getExtras().getLong(MentorsFragment.EXTRA_COURSE_ID));
                intent.setData(mCurrentMentorUri);
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
                MentorEntry._ID,
                MentorEntry.COLUMN_NAME,
                MentorEntry.COLUMN_PHONE,
                MentorEntry.COLUMN_EMAIL,
                MentorEntry.COLUMN_COURSE_ID};

        String selection = null;
        String[] selectionArgs = null;
        selection = MentorEntry.COLUMN_COURSE_ID + "=?";
        selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentMentorUri))};
        return new CursorLoader(this,
                mCurrentMentorUri,
                projection,
                selection,
                selectionArgs,
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
            int ColumnIndexName = cursor.getColumnIndex(MentorEntry.COLUMN_NAME);
            int ColumnIndexPhone = cursor.getColumnIndex(MentorEntry.COLUMN_PHONE);
            int ColumnIndexEmail = cursor.getColumnIndex(MentorEntry.COLUMN_EMAIL);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(ColumnIndexName);
            String phone = cursor.getString(ColumnIndexPhone);
            String email = cursor.getString(ColumnIndexEmail);

            mBundleExistingMentor.putString(EXISTING_MENTOR_NAME, name);
            mBundleExistingMentor.putString(EXISTING_MENTOR_PHONE, phone);
            mBundleExistingMentor.putString(EXISTING_MENTOR_EMAIL, email);

            // Update the views on the screen with the values from the database
            mTextViews[0].setText(name);
            mTextViews[1].setText(phone);
            mTextViews[2].setText(email);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the fields.
        mTextViews[0].setText("");
        mTextViews[0].setText("");
        mTextViews[0].setText("");
    }

    private void deleteMentor() {
        if (mCurrentMentorUri != null) {
            int rowAffected = getContentResolver().delete(mCurrentMentorUri, null, null);
            if (rowAffected == 0) {
                Toast.makeText(this, R.string.error_delete_mentor_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.msg_delete_mentor_successful, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_delete_mentor_dialog);
        builder.setPositiveButton(R.string.action_dialog_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteMentor();
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


}