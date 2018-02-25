package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.data.EventContract;
import com.sansara.develop.studentscheduler.fragment.AssessmentsFragment;
import com.sansara.develop.studentscheduler.fragment.CoursesFragment;

/**
 * Displays list of events(terms,courses or assessments) that were entered and stored in the app.
 */
public class ListActivity extends AppCompatActivity {
    private String TAG = ListActivity.class.getSimpleName();
    private Menu mMenu;
    private Fragment mFragmentList;
    private Uri mContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Log.e(TAG,"         onCreate");

//        getFragmentManager().popBackStack();
//        mFragmentList = (Fragment) getFragmentManager().findFragmentById(R.id.fragment_list);

        Fragment fragment;
        int eventId = getIntent().getIntExtra(HomeActivity.EXTRA_EVENT_ID, -1);
        switch (eventId) {
            case HomeActivity.EVENT_ID_TERM:
                setTitle(getString(R.string.event_terms));
                mContentUri = EventContract.TermEntry.CONTENT_URI;
                //mFragmentList.setTargetFragment(new TermsFragment(), eventId);
                break;
            case HomeActivity.EVENT_ID_COURSE:
                setTitle(getString(R.string.event_courses));
                mContentUri = EventContract.CourseEntry.CONTENT_URI;
                getFragmentManager().popBackStack();
                getFragmentManager().beginTransaction()
                        .add(R.id.linear_layout_fragment_list, new CoursesFragment()).addToBackStack(null).commit();
                break;
            case HomeActivity.EVENT_ID_ASSESSMENT:
                setTitle(getString(R.string.event_assessments));
                mContentUri = EventContract.AssessmentEntry.CONTENT_URI;
                getFragmentManager().beginTransaction()
                        .add(R.id.linear_layout_fragment_list, new AssessmentsFragment()).addToBackStack(null).commit();
//                fragment=new AssessmentsFragment();
//                fragment.setTargetFragment(mFragmentList, eventId);
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG,"         onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG,"         onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"         onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"         onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"         onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"         onDestroy");
    }
}