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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;

import com.sansara.develop.studentscheduler.DetailedActivity;
import com.sansara.develop.studentscheduler.EditorCourseActivity;
import com.sansara.develop.studentscheduler.HomeActivity;
import com.sansara.develop.studentscheduler.adapter.CourseTabAdapter;
import com.sansara.develop.studentscheduler.adapter.ListCursorAdapter;
import com.sansara.develop.studentscheduler.DetailedAssessmentActivity;
import com.sansara.develop.studentscheduler.EditorAssessmentActivity;
import com.sansara.develop.studentscheduler.ListActivity;
import com.sansara.develop.studentscheduler.R;
import com.sansara.develop.studentscheduler.adapter.TermTabAdapter;
import com.sansara.develop.studentscheduler.data.EventContract.CourseEntry;
import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * Displays list of events(terms,courses or assessments) that were entered and stored in the app.
 */
public class CoursesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int COURSES_LOADER = 0;
    private ListCursorAdapter mListCursorAdapter;
    private Context mContext;
    private Unbinder mUnbinder;

    @OnItemClick(R.id.list_fragment_list)
    void onDetailingCourses(long id) {
        Uri currentUri = ContentUris.withAppendedId(CourseEntry.CONTENT_URI, id);
        Intent intent = new Intent(mContext, DetailedActivity.class);
        intent.putExtra(HomeActivity.EXTRA_EVENT_ID, HomeActivity.EVENT_ID_COURSE);
        intent.setData(currentUri);
        startActivity(intent);
    }

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mContext = getActivity();

        ListView coursesListView = rootView.findViewById(R.id.list_fragment_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.relative_layout_empty_view);
        coursesListView.setEmptyView(emptyView);

        mListCursorAdapter = new ListCursorAdapter(mContext, null);
        coursesListView.setAdapter(mListCursorAdapter);

        getLoaderManager().initLoader(COURSES_LOADER, null, this);

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
                CourseEntry._ID,
                CourseEntry.COLUMN_TITLE,
                CourseEntry.COLUMN_TERM_ID};
        String selection = null;
        String[] selectionArgs = null;
        if (getArguments() != null) {
            selection = CourseEntry.COLUMN_TERM_ID + "=?";
            selectionArgs = new String[]{String.valueOf(getArguments().getLong(TermTabAdapter.TERM_ID))};
        }
        return new CursorLoader(
                mContext,
                CourseEntry.CONTENT_URI,
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