package com.sansara.develop.studentscheduler.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;
import com.sansara.develop.studentscheduler.data.EventContract.CourseEntry;
import com.sansara.develop.studentscheduler.data.EventContract.TermEntry;
import com.sansara.develop.studentscheduler.data.EventContract.MentorEntry;

/**
 * {@link ContentProvider} for Student scheduler app.
 */
public class EventProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = EventProvider.class.getSimpleName();
    private EventDbHelper mDbHelper;

    private static final int ASSESSMENTS = 100;
    private static final int ASSESSMENT_ID = 101;
    private static final int COURSES = 200;
    private static final int COURSE_ID = 201;
    private static final int TERMS = 300;
    private static final int TERM_ID = 301;
    private static final int MENTORS = 400;
    private static final int MENTOR_ID = 401;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_ASSESSMENTS, ASSESSMENTS);
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_ASSESSMENTS + "/#", ASSESSMENT_ID);
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_COURSES, COURSES);
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_COURSES + "/#", COURSE_ID);
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_TERMS, TERMS);
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_TERMS + "/#", TERM_ID);
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_MENTORS, MENTORS);
        sUriMatcher.addURI(EventContract.CONTENT_AUTHORITY, EventContract.PATH_MENTORS + "/#", MENTOR_ID);

    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new EventDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ASSESSMENTS:
                cursor = database.query(AssessmentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ASSESSMENT_ID:
                selection = AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(AssessmentEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSES:
                cursor = database.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case COURSE_ID:
                selection = AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(CourseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TERMS:
                cursor = database.query(TermEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TERM_ID:
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TermEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MENTORS:
                cursor = database.query(MentorEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MENTOR_ID:
                selection = MentorEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(MentorEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ASSESSMENTS:
                return insertAssessment(uri, contentValues);
            case COURSES:
                return insertCourse(uri, contentValues);
            case TERMS:
                return insertTerm(uri, contentValues);
            case MENTORS:
                return insertMentor(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ASSESSMENTS:
                return updateAssessment(uri, contentValues, selection, selectionArgs);
            case ASSESSMENT_ID:
                selection = AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateAssessment(uri, contentValues, selection, selectionArgs);
            case COURSES:
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case COURSE_ID:
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateCourse(uri, contentValues, selection, selectionArgs);
            case TERMS:
                return updateTerm(uri, contentValues, selection, selectionArgs);
            case TERM_ID:
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTerm(uri, contentValues, selection, selectionArgs);
            case MENTORS:
                return updateMentor(uri, contentValues, selection, selectionArgs);
            case MENTOR_ID:
                selection = MentorEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateMentor(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ASSESSMENTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(AssessmentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ASSESSMENT_ID:
                // Delete a single row given by the ID in the URI
                selection = AssessmentEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(AssessmentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COURSE_ID:
                // Delete a single row given by the ID in the URI
                selection = CourseEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(CourseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TERMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TermEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TERM_ID:
                // Delete a single row given by the ID in the URI
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TermEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MENTORS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(MentorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MENTOR_ID:
                // Delete a single row given by the ID in the URI
                selection = TermEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(MentorEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ASSESSMENTS:
                return AssessmentEntry.CONTENT_LIST_TYPE;
            case ASSESSMENT_ID:
                return AssessmentEntry.CONTENT_ITEM_TYPE;
            case COURSES:
                return CourseEntry.CONTENT_LIST_TYPE;
            case COURSE_ID:
                return CourseEntry.CONTENT_ITEM_TYPE;
            case TERMS:
                return TermEntry.CONTENT_LIST_TYPE;
            case TERM_ID:
                return TermEntry.CONTENT_ITEM_TYPE;
            case MENTORS:
                return MentorEntry.CONTENT_LIST_TYPE;
            case MENTOR_ID:
                return MentorEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    private Uri insertAssessment(Uri uri, ContentValues values) {
        // Check that fields is not null
        String title = values.getAsString(AssessmentEntry.COLUMN_TITLE);
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("Assessment requires a name");
        }

        // No need to check the start and end date, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(AssessmentEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertCourse(Uri uri, ContentValues values) {
        // Check that fields is not null
        String title = values.getAsString(CourseEntry.COLUMN_TITLE);
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("Course requires a name");
        }

        // No need to check the start and end date, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(CourseEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertTerm(Uri uri, ContentValues values) {
        // Check that fields is not null
        String title = values.getAsString(TermEntry.COLUMN_TITLE);
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("Term requires a name");
        }

        // No need to check the start and end date, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(TermEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertMentor(Uri uri, ContentValues values) {
        // Check that fields is not null
        String name = values.getAsString(MentorEntry.COLUMN_NAME);
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Mentor requires a name");
        }

        // No need to check the start and end date, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(MentorEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    private int updateAssessment(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Check that fields is not null
        String title = values.getAsString(AssessmentEntry.COLUMN_TITLE);
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("Assessment requires a name");
        }

        // No need to check the start and end date, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(AssessmentEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateCourse(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Check that fields is not null
        String title = values.getAsString(CourseEntry.COLUMN_TITLE);
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("Course requires a name");
        }

        // No need to check the start and end date, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(CourseEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateTerm(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Check that fields is not null
        String title = values.getAsString(TermEntry.COLUMN_TITLE);
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("Term requires a name");
        }

        // No need to check the start and end date, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(TermEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateMentor(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Check that fields is not null
        String title = values.getAsString(MentorEntry.COLUMN_NAME);
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("Term requires a name");
        }

        // No need to check the start and end date, any value is valid (including null).

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(MentorEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }
}