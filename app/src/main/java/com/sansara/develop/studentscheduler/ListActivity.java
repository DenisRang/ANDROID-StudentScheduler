package com.sansara.develop.studentscheduler;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;

/**
 * Displays list of events(terms,courses or assessments) that were entered and stored in the app.
 */
public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ASSESSMENT_LOADER = 0;
    private AssessmentCursorAdapter mAssessmentCursorAdapter;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, EditorAssessmentActivity.class);
                startActivity(intent);
            }
        });

        ListView petListView = findViewById(R.id.list_activity_list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.relative_layout_empty_view);
        petListView.setEmptyView(emptyView);

        mAssessmentCursorAdapter = new AssessmentCursorAdapter(this, null);
        petListView.setAdapter(mAssessmentCursorAdapter);
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri currentPetUri = ContentUris.withAppendedId(AssessmentEntry.CONTENT_URI, id);
                Intent intent = new Intent(ListActivity.this, EditorAssessmentActivity.class);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(ASSESSMENT_LOADER, null, this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mAssessmentCursorAdapter.getCount() == 0) hideOption(R.id.item_delete_all_entries);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/activity_list.xml file.
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

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                AssessmentEntry._ID,
                AssessmentEntry.COLUMN_TITLE,
                AssessmentEntry.COLUMN_START_TIME,
                AssessmentEntry.COLUMN_END_TIME};
        return new CursorLoader(
                this,
                AssessmentEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        mAssessmentCursorAdapter.swapCursor(data);
        if (mAssessmentCursorAdapter.getCount() > 0) showOption(R.id.item_delete_all_entries);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAssessmentCursorAdapter.swapCursor(null);
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
        int rowAffected = getContentResolver().delete(AssessmentEntry.CONTENT_URI, null, null);
        if (rowAffected == 0) {
            Toast.makeText(this, R.string.error_delete_assessment_failed, Toast.LENGTH_SHORT).show();
        } else {
            hideOption(R.id.item_delete_all_entries);
            Toast.makeText(this, R.string.msg_delete_assessment_successful, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideOption(int id) {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(id);
            item.setVisible(false);
        }
    }

    private void showOption(int id) {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(id);
            item.setVisible(true);
        }
    }
}