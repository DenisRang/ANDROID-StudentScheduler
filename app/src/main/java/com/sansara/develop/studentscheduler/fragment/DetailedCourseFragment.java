package com.sansara.develop.studentscheduler.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sansara.develop.studentscheduler.DetailedActivity;
import com.sansara.develop.studentscheduler.R;
import com.sansara.develop.studentscheduler.data.EventContract;
import com.sansara.develop.studentscheduler.data.EventContract.CourseEntry;
import com.sansara.develop.studentscheduler.utils.DateTimeUtils;


public class DetailedCourseFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_EXISTING_COURSE_BUNDLE = "EXTRA_EXISTING_COURSE_BUNDLE";
    public static final String EXISTING_COURSE_TITLE = "EXISTING_COURSE_TITLE";
    public static final String EXISTING_COURSE_START = "EXISTING_COURSE_START";
    public static final String EXISTING_COURSE_END = "EXISTING_COURSE_END";
    public static final String EXISTING_COURSE_TIME_STAMP = "EXISTING_COURSE_TIME_STAMP";
    public static final String EXISTING_COURSE_STATUS = "EXISTING_COURSE_STATUS";
    public static final String EXISTING_COURSE_NOTE = "EXISTING_COURSE_NOTE";
    public static final String EXISTING_COURSE_TERM_ID = "EXISTING_COURSE_TERM_ID";

    public String TAG = DetailedCourseFragment.class.getSimpleName();

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
        Log.e(TAG, "         onCreate");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "         onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_detailed, container, false);

        mTextView = (TextView) rootView.findViewById(R.id.text_fragment_detailed);

        mContext = getActivity();
        mCurrentCourseUri = ((DetailedActivity) getActivity()).getContentUri();


        getLoaderManager().initLoader(EXISTING_COURSE_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "         onDestroyView");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                CourseEntry._ID,
                CourseEntry.COLUMN_TITLE,
                CourseEntry.COLUMN_START_TIME,
                CourseEntry.COLUMN_END_TIME,
                CourseEntry.COLUMN_TIME_STAMP,
                CourseEntry.COLUMN_STATUS,
                CourseEntry.COLUMN_NOTE,
                CourseEntry.COLUMN_TERM_ID};

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
            int columnIndexTitle = cursor.getColumnIndex(CourseEntry.COLUMN_TITLE);
            int columnIndexStart = cursor.getColumnIndex(CourseEntry.COLUMN_START_TIME);
            int columnIndexEnd = cursor.getColumnIndex(CourseEntry.COLUMN_END_TIME);
            int columnIndexTimeStamp = cursor.getColumnIndex(CourseEntry.COLUMN_TIME_STAMP);
            int columnIndexStatus = cursor.getColumnIndex(CourseEntry.COLUMN_STATUS);
            int columnIndexNoteId = cursor.getColumnIndex(CourseEntry.COLUMN_NOTE);
            int columnIndexTermId = cursor.getColumnIndex(CourseEntry.COLUMN_TERM_ID);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(columnIndexTitle);
            long start = cursor.getLong(columnIndexStart);
            long end = cursor.getLong(columnIndexEnd);
            long stamp = cursor.getLong(columnIndexTimeStamp);
            int status = cursor.getInt(columnIndexStatus);
            String note = cursor.getString(columnIndexNoteId);
            int termId = cursor.getInt(columnIndexTermId);

            Bundle bundleExistingCourse = new Bundle();
            bundleExistingCourse.putString(EXISTING_COURSE_TITLE, title);
            bundleExistingCourse.putLong(EXISTING_COURSE_START, start);
            bundleExistingCourse.putLong(EXISTING_COURSE_END, end);
            bundleExistingCourse.putLong(EXISTING_COURSE_TIME_STAMP, stamp);
            bundleExistingCourse.putInt(EXISTING_COURSE_STATUS, status);
            bundleExistingCourse.putString(EXISTING_COURSE_NOTE, note);
            bundleExistingCourse.putInt(EXISTING_COURSE_TERM_ID, termId);
            ((DetailedActivity) getActivity()).setBundle(bundleExistingCourse);

            // Update the views on the screen with the values from the database
            String statusString = "";
            switch (status) {
                case CourseEntry.STATUS_IN_PROGRESS:
                    statusString = getString(R.string.status_in_progress);
                    break;
                case CourseEntry.STATUS_COMPLETED:
                    statusString = getString(R.string.status_in_progress);
                    break;
                case CourseEntry.STATUS_DROPPED:
                    statusString = getString(R.string.status_in_progress);
                    break;
                case CourseEntry.STATUS_PLAN_TO_TAKE:
                    statusString = getString(R.string.status_in_progress);
                    break;
                default:
                    statusString = getString(R.string.status_unknown);
                    break;
            }

            String[] projection = {
                    EventContract.TermEntry._ID,
                    EventContract.TermEntry.COLUMN_TITLE};
            Cursor cursorForTerm = mContext.getContentResolver()
                    .query(ContentUris.withAppendedId(EventContract.TermEntry.CONTENT_URI, termId), projection, null, null, null);
            cursorForTerm.moveToFirst();
            int columnIndexTermTitle = cursorForTerm.getColumnIndex(EventContract.TermEntry.COLUMN_TITLE);
            String term;
            try {
                term = cursorForTerm.getString(columnIndexTermTitle);
            } catch (Exception e) {
                term = getString(R.string.not_assigned);
            }
            cursorForTerm.close();

            mTextView.setText(getString(R.string.title_hint) + ":   " + title + "\n"
                    + getString(R.string.start) + "   " + DateTimeUtils.getFullDate(start) + "\n"
                    + getString(R.string.end) + "   " + DateTimeUtils.getFullDate(end) + "\n"
                    + getString(R.string.msg_spinner_status) + "   " + statusString + "\n"
                    + getString(R.string.note) + ":   " + note + "\n"
                    + getString(R.string.msg_spinner_terms) + "   " + term);

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
        Log.e(TAG, "         onAttach");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "         onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "         onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "         onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "         onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "         onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "         onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "         onDetach");
    }
}