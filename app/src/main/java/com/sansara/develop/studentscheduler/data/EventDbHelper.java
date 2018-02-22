package com.sansara.develop.studentscheduler.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sansara.develop.studentscheduler.data.EventContract.TermEntry;
import com.sansara.develop.studentscheduler.data.EventContract.CourseEntry;
import com.sansara.develop.studentscheduler.data.EventContract.AssessmentEntry;
import com.sansara.develop.studentscheduler.data.EventContract.MentorEntry;

/**
 * Created by den on 27.01.2018.
 */

public class EventDbHelper extends SQLiteOpenHelper {
    private static final String TAG = EventDbHelper.class.getName();
    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //For switch on "FOREIGN KEY" option
        String SQL_FOREIGN_KEYS_ON = "PRAGMA foreign_keys=on;";

        String SQL_CREATE_TERMS_TABLE = "CREATE TABLE " + TermEntry.TABLE_NAME + " ("
                + TermEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TermEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + TermEntry.COLUMN_START_TIME + " TEXT, "
                + TermEntry.COLUMN_END_TIME + " TEXT);";
        Log.d(TAG, ">>>  " + SQL_CREATE_TERMS_TABLE);

        String SQL_CREATE_COURSES_TABLE = "CREATE TABLE " + CourseEntry.TABLE_NAME + " ("
                + CourseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CourseEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + CourseEntry.COLUMN_START_TIME + " TEXT, "
                + CourseEntry.COLUMN_END_TIME + " TEXT, "
                + CourseEntry.COLUMN_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                + CourseEntry.COLUMN_NOTE + " TEXT, "
                // Course can has no term so "NOT NULL" isn't required(e.g. if it's additional course)
                + CourseEntry.COLUMN_TERM_ID + " INTEGER, "
                + " FOREIGN KEY (" + CourseEntry.COLUMN_TERM_ID + ") "
                + " REFERENCES " + TermEntry.TABLE_NAME + " (" + TermEntry._ID + ")); ";
        Log.d(TAG, ">>>  " + SQL_CREATE_COURSES_TABLE);

        String SQL_CREATE_ASSESSMENTS_TABLE = "CREATE TABLE " + AssessmentEntry.TABLE_NAME + " ("
                + AssessmentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AssessmentEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + AssessmentEntry.COLUMN_START_TIME + " TEXT, "
                + AssessmentEntry.COLUMN_END_TIME + " TEXT, "
                // An assessment necessarily belong to a course so "NOT NULL" is required. But for
                // ability firstly to fill assessments and then fill courses with assessments it
                // doesn't use "NOT NULL"
                + AssessmentEntry.COLUMN_COURSE_ID + " INTEGER, "
                + " FOREIGN KEY (" + AssessmentEntry.COLUMN_COURSE_ID + ") "
                + " REFERENCES " + CourseEntry.TABLE_NAME + " (" + CourseEntry._ID + ")); ";
        Log.d(TAG, ">>>  " + SQL_CREATE_ASSESSMENTS_TABLE);

        String SQL_CREATE_MENTORS_TABLE = "CREATE TABLE " + MentorEntry.TABLE_NAME + " ("
                + MentorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MentorEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + MentorEntry.COLUMN_PHONE + " TEXT, "
                + MentorEntry.COLUMN_EMAIL + " TEXT, "
                // A mentor necessarily belong to a course so "NOT NULL" is required. But for
                // ability firstly to fill mentors and then fill courses with mentors it
                // doesn't use "NOT NULL"
                + MentorEntry.COLUMN_COURSE_ID + " INTEGER, "
                + " FOREIGN KEY (" + MentorEntry.COLUMN_COURSE_ID + ") "
                + " REFERENCES " + CourseEntry.TABLE_NAME + " (" + CourseEntry._ID + ")); ";
        Log.d(TAG, ">>>  " + SQL_CREATE_MENTORS_TABLE);

        //Execute the SQL statements
        db.execSQL(SQL_FOREIGN_KEYS_ON);
        db.execSQL(SQL_CREATE_TERMS_TABLE);
        db.execSQL(SQL_CREATE_COURSES_TABLE);
        db.execSQL(SQL_CREATE_ASSESSMENTS_TABLE);
        db.execSQL(SQL_CREATE_MENTORS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }
}