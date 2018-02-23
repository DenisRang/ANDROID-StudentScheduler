package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.data.EventContract;

/**
 * Displays list of events(terms,courses or assessments) that were entered and stored in the app.
 */
public class ListActivity extends AppCompatActivity {
    private Menu mMenu;
    private Fragment mFragmentList;
    private Uri mContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mFragmentList = (Fragment) getFragmentManager().findFragmentById(R.id.fragment_list);

        int eventId = getIntent().getIntExtra(HomeActivity.EXTRA_EVENT_ID,-1);
        switch (eventId) {
            case HomeActivity.EVENT_ID_TERM:
                setTitle(getString(R.string.event_terms));
                mContentUri=EventContract.TermEntry.CONTENT_URI;
                //mFragmentList.setTargetFragment(new TermsFragment(), eventId);
                break;
            case HomeActivity.EVENT_ID_COURSE:
                setTitle(getString(R.string.event_courses));
                mContentUri=EventContract.CourseEntry.CONTENT_URI;
                //mFragmentList.setTargetFragment(new CoursesFragment(), eventId);
                break;
            case HomeActivity.EVENT_ID_ASSESSMENT:
                setTitle(getString(R.string.event_assessments));
                mContentUri=EventContract.AssessmentEntry.CONTENT_URI;
                Fragment fragment=new AssessmentsFragment();
                fragment.setTargetFragment(mFragmentList, eventId);
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
        int rowAffected = getContentResolver().delete(mContentUri, null, null);
        if (rowAffected == 0) {
            Toast.makeText(this, R.string.error_delete_assessment_failed, Toast.LENGTH_SHORT).show();
        } else {
            hideOption(R.id.item_delete_all_entries);
            Toast.makeText(this, R.string.msg_delete_assessment_successful, Toast.LENGTH_SHORT).show();
        }
    }
}