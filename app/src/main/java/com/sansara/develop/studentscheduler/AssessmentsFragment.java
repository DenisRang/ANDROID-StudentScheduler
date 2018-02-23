package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;

/**
 * Displays list of events(terms,courses or assessments) that were entered and stored in the app.
 */
public class AssessmentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ASSESSMENT_LOADER = 0;
    private AssessmentCursorAdapter mAssessmentCursorAdapter;
    private Context mContext;

    public AssessmentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mContext=getActivity();

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.button_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditorAssessmentActivity.class);
                startActivity(intent);
            }
        });

        ListView assessmentsListView = rootView.findViewById(R.id.list_fragment_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = rootView.findViewById(R.id.relative_layout_empty_view);
        assessmentsListView.setEmptyView(emptyView);

        mAssessmentCursorAdapter = new AssessmentCursorAdapter(mContext, null);
        assessmentsListView.setAdapter(mAssessmentCursorAdapter);
        assessmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri currentPetUri = ContentUris.withAppendedId(AssessmentEntry.CONTENT_URI, id);
                Intent intent = new Intent(mContext, DetailedAssessmentActivity.class);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(ASSESSMENT_LOADER, null, this);

        return rootView;
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AssessmentEntry._ID,
                AssessmentEntry.COLUMN_TITLE,
                AssessmentEntry.COLUMN_START_TIME,
                AssessmentEntry.COLUMN_END_TIME};
        return new CursorLoader(
                mContext,
                AssessmentEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        mAssessmentCursorAdapter.swapCursor(data);

        // Adding the ability to delete all of assessments in ListActivity optional menu
        if (mContext instanceof ListActivity){
            if(mAssessmentCursorAdapter.getCount() == 0){
                ((ListActivity)mContext).hideOption(R.id.item_delete_all_entries);
            }else{
                ((ListActivity)mContext).showOption(R.id.item_delete_all_entries);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAssessmentCursorAdapter.swapCursor(null);
    }

}