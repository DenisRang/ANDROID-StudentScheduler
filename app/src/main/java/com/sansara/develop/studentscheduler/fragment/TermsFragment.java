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
import com.sansara.develop.studentscheduler.adapter.ListCursorAdapter;
import com.sansara.develop.studentscheduler.DetailedAssessmentActivity;
import com.sansara.develop.studentscheduler.EditorAssessmentActivity;
import com.sansara.develop.studentscheduler.ListActivity;
import com.sansara.develop.studentscheduler.R;
import com.sansara.develop.studentscheduler.data.EventContract.CourseEntry;
import com.sansara.develop.studentscheduler.data.EventContract.TermEntry;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * Displays list of events(terms,courses or assessments) that were entered and stored in the app.
 */
public class TermsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int TERMS_LOADER = 0;
    private ListCursorAdapter mListCursorAdapter;
    private Context mContext;
    private Unbinder mUnbinder;

    @OnItemClick(R.id.list_fragment_list)
    void onDetailingTerms(long id) {
        Uri currentUri = ContentUris.withAppendedId(TermEntry.CONTENT_URI, id);
        Intent intent = new Intent(mContext, DetailedActivity.class);
        intent.putExtra(HomeActivity.EXTRA_EVENT_ID, HomeActivity.EVENT_ID_TERM);
        intent.setData(currentUri);
        startActivity(intent);
    }

    public TermsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        mContext = getActivity();

        ListView termsListView = rootView.findViewById(R.id.list_fragment_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.relative_layout_empty_view);
        termsListView.setEmptyView(emptyView);

        mListCursorAdapter = new ListCursorAdapter(mContext, null);
        termsListView.setAdapter(mListCursorAdapter);

        getLoaderManager().initLoader(TERMS_LOADER, null, this);

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
                TermEntry._ID,
                TermEntry.COLUMN_TITLE,
                TermEntry.COLUMN_START_TIME,
                TermEntry.COLUMN_END_TIME};
        return new CursorLoader(
                mContext,
                TermEntry.CONTENT_URI,
                projection,
                null,
                null,
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