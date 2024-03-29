package com.sansara.develop.studentscheduler.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sansara.develop.studentscheduler.R;
import com.sansara.develop.studentscheduler.data.EventContract;

/**
 * {@link ListCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of assessment data as its data source. This adapter knows
 * how to create list items for each row of assessment data in the {@link Cursor}.
 */
public class ListCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ListCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ListCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

    /**
     * This method binds the assessment data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current assessment can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewTitle = view.findViewById(R.id.text_item_list_title);
        int titleColumnIndex = cursor.getColumnIndex(EventContract.AssessmentEntry.COLUMN_TITLE);
        String currentTitle = cursor.getString(titleColumnIndex);

        textViewTitle.setText(currentTitle);

    }
}