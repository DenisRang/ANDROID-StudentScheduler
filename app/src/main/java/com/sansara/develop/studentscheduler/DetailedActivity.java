package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.adapter.CourseTabAdapter;
import com.sansara.develop.studentscheduler.adapter.TermTabAdapter;
import com.sansara.develop.studentscheduler.alarm.AlarmHelper;
import com.sansara.develop.studentscheduler.fragment.MentorsFragment;

import static com.sansara.develop.studentscheduler.fragment.DetailedCourseFragment.EXISTING_COURSE_TIME_STAMP;
import static com.sansara.develop.studentscheduler.fragment.DetailedCourseFragment.EXTRA_EXISTING_COURSE_BUNDLE;
import static com.sansara.develop.studentscheduler.fragment.DetailedTermFragment.EXTRA_EXISTING_TERM_BUNDLE;

/**
 * Displays list of events(terms,courses or assessments) that were entered and stored in the app.
 */
public class DetailedActivity extends AppCompatActivity {
    public Uri mContentUri;

    private Bundle mBundle;
    private long mItemId;
    private int mEventId;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detailed);

        Intent intent = getIntent();
        mContentUri = intent.getData();
        mItemId = ContentUris.parseId(mContentUri);
        mEventId = intent.getIntExtra(HomeActivity.EXTRA_EVENT_ID, -1);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout_detailed);
        mViewPager = (ViewPager) findViewById(R.id.pager_detailed);

        switch (mEventId) {
            case HomeActivity.EVENT_ID_TERM:
                setTitle(getString(R.string.title_activity_detailed_term));
                mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_term_info));
                mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_term_courses));
                TermTabAdapter adapterTerm = new TermTabAdapter(getFragmentManager(), 2, mItemId);
                mViewPager.setAdapter(adapterTerm);
                break;
            case HomeActivity.EVENT_ID_COURSE:
                setTitle(getString(R.string.title_activity_detailed_course));
                mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_course_info));
                mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_course_assessments));
                mTabLayout.addTab(mTabLayout.newTab().setText(R.string.tab_course_mentors));
                CourseTabAdapter adapterCourse = new CourseTabAdapter(getFragmentManager(), 3, mItemId);
                mViewPager.setAdapter(adapterCourse);
                break;
        }
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/fragment_list.xmlnts.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.activity_detailed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Change" menu option
            case R.id.item_action_redo:
                Intent intent = null;
                switch (mEventId) {
                    case HomeActivity.EVENT_ID_TERM:
                        intent = new Intent(DetailedActivity.this, EditorTermActivity.class);
                        intent.putExtra(EXTRA_EXISTING_TERM_BUNDLE, mBundle);
                        intent.putExtra(MentorsFragment.EXTRA_COURSE_ID, mItemId);
                        break;
                    case HomeActivity.EVENT_ID_COURSE:
                        intent = new Intent(DetailedActivity.this, EditorCourseActivity.class);
                        intent.putExtra(EXTRA_EXISTING_COURSE_BUNDLE, mBundle);
                        break;
                }
                if (intent != null) {
                    intent.setData(mContentUri);
                    startActivity(intent);
                }
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

    public Uri getContentUri() {
        return mContentUri;
    }


    private void deleteEvent() {
        if (mContentUri != null) {
            int rowAffected = getContentResolver().delete(mContentUri, null, null);
            if (rowAffected == 0) {
                Toast.makeText(this, R.string.error_delete_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.msg_delete_successful, Toast.LENGTH_SHORT).show();
                AlarmHelper.getInstance().removeAlarm(mBundle.getLong(EXISTING_COURSE_TIME_STAMP));
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.msg_delete_term_dialog);
        builder.setPositiveButton(R.string.action_dialog_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteEvent();
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


    public void setBundle(Bundle bundle) {
        mBundle = bundle;
    }
}