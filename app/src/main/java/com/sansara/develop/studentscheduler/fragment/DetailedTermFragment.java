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
import com.sansara.develop.studentscheduler.data.EventContract.TermEntry;
import com.sansara.develop.studentscheduler.utils.DateTimeUtils;


public class DetailedTermFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_EXISTING_TERM_BUNDLE = "EXTRA_EXISTING_TERM_BUNDLE";
    public static final String EXISTING_TERM_TITLE = "EXISTING_TERM_TITLE";
    public static final String EXISTING_TERM_START = "EXISTING_TERM_START";
    public static final String EXISTING_TERM_END = "EXISTING_TERM_END";
    public static final String EXISTING_TERM_TIME_STAMP = "EXISTING_TERM_TIME_STAMP";


    public String TAG = DetailedTermFragment.class.getSimpleName();

    private static final int EXISTING_TERM_LOADER = 0;

    private Uri mCurrentTermUri;
    Context mContext;


    TextView mTextView;

    public DetailedTermFragment() {
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
        mCurrentTermUri = ((DetailedActivity) getActivity()).getContentUri();


        getLoaderManager().initLoader(EXISTING_TERM_LOADER, null, this);

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
                TermEntry._ID,
                TermEntry.COLUMN_TITLE,
                TermEntry.COLUMN_START_TIME,
                TermEntry.COLUMN_END_TIME,
                TermEntry.COLUMN_TIME_STAMP};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getActivity(),
                mCurrentTermUri,
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
            int columnIndexTitle = cursor.getColumnIndex(TermEntry.COLUMN_TITLE);
            int columnIndexStart = cursor.getColumnIndex(TermEntry.COLUMN_START_TIME);
            int columnIndexEnd = cursor.getColumnIndex(TermEntry.COLUMN_END_TIME);
            int columnIndexTimeStamp = cursor.getColumnIndex(TermEntry.COLUMN_TIME_STAMP);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(columnIndexTitle);
            long start = cursor.getLong(columnIndexStart);
            long end = cursor.getLong(columnIndexEnd);
            long stamp = cursor.getLong(columnIndexTimeStamp);

            Bundle bundleExistingTerm = new Bundle();
            bundleExistingTerm.putString(EXISTING_TERM_TITLE, title);
            bundleExistingTerm.putLong(EXISTING_TERM_START, start);
            bundleExistingTerm.putLong(EXISTING_TERM_END, end);
            bundleExistingTerm.putLong(EXISTING_TERM_TIME_STAMP, stamp);

            ((DetailedActivity) getActivity()).setBundle(bundleExistingTerm);

            mTextView.setText(getString(R.string.title_hint) + ":   " + title + "\n"
                    + getString(R.string.start) + "   " + DateTimeUtils.getFullDate(start) + "\n"
                    + getString(R.string.end) + "   " + DateTimeUtils.getFullDate(end));

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