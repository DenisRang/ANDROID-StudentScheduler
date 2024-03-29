package com.sansara.develop.studentscheduler.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sansara.develop.studentscheduler.DetailedActivity;
import com.sansara.develop.studentscheduler.adapter.CourseTabAdapter;
import com.sansara.develop.studentscheduler.adapter.ListCursorAdapter;
import com.sansara.develop.studentscheduler.DetailedAssessmentActivity;
import com.sansara.develop.studentscheduler.EditorAssessmentActivity;
import com.sansara.develop.studentscheduler.ListActivity;
import com.sansara.develop.studentscheduler.R;
import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * Displays list of events(terms,courses or assessments) that were entered and stored in the app.
 */
public class AssessmentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ASSESSMENTS_LOADER = 0;
    private ListCursorAdapter mListCursorAdapter;
    private Context mContext;
    private Unbinder mUnbinder;
    private String TAG = AssessmentsFragment.class.getSimpleName();

    @OnItemClick(R.id.list_fragment_list)
    void onDetailingAssessment(long id) {
        Uri currentUri = ContentUris.withAppendedId(AssessmentEntry.CONTENT_URI, id);
        Intent intent = new Intent(mContext, DetailedAssessmentActivity.class);
        intent.setData(currentUri);
        startActivity(intent);
    }

    public AssessmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);


        mContext = getActivity();

        ListView assessmentsListView = rootView.findViewById(R.id.list_fragment_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.relative_layout_empty_view);
        assessmentsListView.setEmptyView(emptyView);

        mListCursorAdapter = new ListCursorAdapter(mContext, null);
        assessmentsListView.setAdapter(mListCursorAdapter);

        getLoaderManager().initLoader(ASSESSMENTS_LOADER, null, this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AssessmentEntry._ID,
                AssessmentEntry.COLUMN_TITLE,
                AssessmentEntry.COLUMN_START_TIME,
                AssessmentEntry.COLUMN_END_TIME};
        String selection = null;
        String[] selectionArgs = null;
        if (getArguments() != null) {
            selection = AssessmentEntry.COLUMN_COURSE_ID + "=?";
            selectionArgs = new String[]{String.valueOf(getArguments().getLong(CourseTabAdapter.COURSE_ID))};
        }
        return new CursorLoader(
                mContext,
                AssessmentEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        mListCursorAdapter.swapCursor(data);

        // Adding the ability to delete all of assessments in ListActivity optional menu
        if (mContext instanceof ListActivity) {
            if (mListCursorAdapter.getCount() == 0) {
                ((ListActivity) mContext).hideOption(R.id.item_delete_all_entries);
            } else {
                ((ListActivity) mContext).showOption(R.id.item_delete_all_entries);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mListCursorAdapter.swapCursor(null);
    }

}