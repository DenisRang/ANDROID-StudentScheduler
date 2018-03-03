package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.alarm.AlarmHelper;
import com.sansara.develop.studentscheduler.data.EventContract;
import com.sansara.develop.studentscheduler.fragment.AssessmentsFragment;
import com.sansara.develop.studentscheduler.fragment.CoursesFragment;
import com.sansara.develop.studentscheduler.fragment.TermsFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Displays list of events(terms,courses or assessments) that were entered and stored in the app.
 */
public class ListActivity extends AppCompatActivity {
    private String TAG = ListActivity.class.getSimpleName();
    private Menu mMenu;
    private Fragment mFragmentList;
    private Uri mContentUri;
    private Class mEditorClass;

    @OnClick(R.id.button_add)
    void onAddingAssessment() {
        Intent intent = new Intent(this, mEditorClass);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        int eventId = getIntent().getIntExtra(HomeActivity.EXTRA_EVENT_ID, -1);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (eventId) {
            case HomeActivity.EVENT_ID_TERM:
                setTitle(getString(R.string.event_terms));
                mContentUri = EventContract.TermEntry.CONTENT_URI;
                mEditorClass = EditorTermActivity.class;
                ft.add(R.id.frame_layout_for_list_fragment, new TermsFragment()).commit();
                break;
            case HomeActivity.EVENT_ID_COURSE:
                setTitle(getString(R.string.event_courses));
                mContentUri = EventContract.CourseEntry.CONTENT_URI;
                mEditorClass = EditorCourseActivity.class;
                ft.add(R.id.frame_layout_for_list_fragment, new CoursesFragment()).commit();
                break;
            case HomeActivity.EVENT_ID_ASSESSMENT:
                setTitle(getString(R.string.event_assessments));
                mContentUri = EventContract.AssessmentEntry.CONTENT_URI;
                mEditorClass = EditorAssessmentActivity.class;
                ft.add(R.id.frame_layout_for_list_fragment, new AssessmentsFragment()).commit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/fragment_list.xmlnts.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.activity_list, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.item_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showOption(int id) {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(id);
            item.setVisible(true);
        }
    }

    public void hideOption(int id) {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(id);
            item.setVisible(false);
        }
    }

    private void showDeleteConfirmationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.msg_delete_all);
        builder.setNegativeButton(R.string.action_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(R.string.action_dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllItems();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteAllItems() {
        String[] projection = {
                EventContract.AssessmentEntry._ID,
                EventContract.AssessmentEntry.COLUMN_TIME_STAMP};
        Cursor cursor = this.getContentResolver().query(mContentUri, projection, null, null, null);

        int rowAffected = getContentResolver().delete(mContentUri, null, null);

        if (rowAffected == 0 || cursor.getCount() != rowAffected) {
            Toast.makeText(this, getString(R.string.error_delete_failed) + " " + getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            if (cursor.getCount() == rowAffected) {
                while (cursor.moveToNext()) {
                    int ColumnIndexTimeStamp = cursor.getColumnIndex(EventContract.AssessmentEntry.COLUMN_TIME_STAMP);
                    long timeStamp = cursor.getLong(ColumnIndexTimeStamp);
                    AlarmHelper.getInstance().removeAlarm(timeStamp);
                }
            }
            hideOption(R.id.item_delete_all_entries);
            Toast.makeText(this, getTitle() + " " + getString(R.string.msg_delete_successful), Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

}