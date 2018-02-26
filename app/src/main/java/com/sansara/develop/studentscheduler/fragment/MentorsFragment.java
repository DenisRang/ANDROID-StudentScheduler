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
import com.sansara.develop.studentscheduler.DetailedMentorActivity;
import com.sansara.develop.studentscheduler.EditorMentorActivity;
import com.sansara.develop.studentscheduler.ListActivity;
import com.sansara.develop.studentscheduler.R;
import com.sansara.develop.studentscheduler.adapter.MentorsCursorAdapter;
import com.sansara.develop.studentscheduler.data.EventContract.MentorEntry;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * Displays list of events(terms,courses or mentors) that were entered and stored in the app.
 */
public class MentorsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_COURSE_ID = "EXTRA_COURSE_ID";
    private static final int MENTORS_LOADER = 10;
    private MentorsCursorAdapter mListCursorAdapter;
    private Context mContext;
    private Unbinder mUnbinder;
    private String TAG = MentorsFragment.class.getSimpleName();

    @OnItemClick(R.id.list_mentors_list)
    void onDetailingMentor(long id) {
        Uri currentUri = ContentUris.withAppendedId(MentorEntry.CONTENT_URI, id);
        Intent intent = new Intent(mContext, DetailedMentorActivity.class);
        intent.putExtra(EXTRA_COURSE_ID, getArguments().getLong(CourseTabAdapter.COURSE_ID));
        intent.setData(currentUri);
        startActivity(intent);
    }

    @OnClick(R.id.button_mentors_add)
    void onAddingAssessment() {
        Intent intent = new Intent(mContext, EditorMentorActivity.class);
        intent.putExtra(EXTRA_COURSE_ID, getArguments().getLong(CourseTabAdapter.COURSE_ID));
        startActivity(intent);
    }

    public MentorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_mentors, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mContext = getActivity();

        ListView mentorsListView = rootView.findViewById(R.id.list_mentors_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.relative_layout_mentors_empty_view);
        mentorsListView.setEmptyView(emptyView);

        mListCursorAdapter = new MentorsCursorAdapter(mContext, null);
        mentorsListView.setAdapter(mListCursorAdapter);

        getLoaderManager().initLoader(MENTORS_LOADER, null, this);

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
                MentorEntry._ID,
                MentorEntry.COLUMN_NAME,
                MentorEntry.COLUMN_PHONE,
                MentorEntry.COLUMN_EMAIL,
                MentorEntry.COLUMN_COURSE_ID};
        String selection = null;
        String[] selectionArgs = null;
        if (getArguments() != null) {
            selection = MentorEntry.COLUMN_COURSE_ID + "=?";
            selectionArgs = new String[]{String.valueOf(getArguments().getLong(CourseTabAdapter.COURSE_ID))};
        }
        return new CursorLoader(
                mContext,
                MentorEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        mListCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mListCursorAdapter.swapCursor(null);
    }
}