package com.sansara.develop.studentscheduler.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.DetailedActivity;
import com.sansara.develop.studentscheduler.EditorCourseActivity;
import com.sansara.develop.studentscheduler.R;
import com.sansara.develop.studentscheduler.data.EventContract.CourseEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class DetailedCourseFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_EXISTING_COURSE_BUNDLE = "existing_course_bundle";
    public static final String EXISTING_COURSE_TITLE = "existing_course_title";
    public static final String EXISTING_COURSE_START = "existing_course_start";
    public static final String EXISTING_COURSE_END = "existing_course_end";

    public String TAG=DetailedCourseFragment.class.getSimpleName();

    private static final int EXISTING_COURSE_LOADER = 0;

    private Uri mCurrentCourseUri;
    Context mContext;


    TextView mTextView;

    public DetailedCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"         onCreate");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"         onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_detailed_course, container, false);

        mTextView=(TextView)rootView.findViewById(R.id.text_detailed_course);

        mContext=getActivity();
        mCurrentCourseUri = ((DetailedActivity)getActivity()).getContentUri();


        getLoaderManager().initLoader(EXISTING_COURSE_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG,"         onDestroyView");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                CourseEntry._ID,
                CourseEntry.COLUMN_TITLE,
                CourseEntry.COLUMN_START_TIME,
                CourseEntry.COLUMN_END_TIME};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getActivity(),
                mCurrentCourseUri,
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
            int ColumnIndexTitle = cursor.getColumnIndex(CourseEntry.COLUMN_TITLE);
            int ColumnIndexStart = cursor.getColumnIndex(CourseEntry.COLUMN_START_TIME);
            int ColumnIndexEnd = cursor.getColumnIndex(CourseEntry.COLUMN_END_TIME);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(ColumnIndexTitle);
            String start = cursor.getString(ColumnIndexStart);
            String end = cursor.getString(ColumnIndexEnd);

            Bundle bundleExistingCourse = new Bundle();
            bundleExistingCourse.putString(EXISTING_COURSE_TITLE, title);
            bundleExistingCourse.putString(EXISTING_COURSE_START, start);
            bundleExistingCourse.putString(EXISTING_COURSE_END, end);
            ((DetailedActivity)getActivity()).setBundle(bundleExistingCourse);

            // Update the views on the screen with the values from the database

            Log.e(TAG,"         onLoadFinished");
            mTextView.setText(title + "\n"
                   + start + "\n"
                    + end);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the fields.
        mTextView.setText("");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG,"         onAttach");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG,"         onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG,"         onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"         onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG,"         onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG,"         onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"         onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG,"         onDetach");
    }
}