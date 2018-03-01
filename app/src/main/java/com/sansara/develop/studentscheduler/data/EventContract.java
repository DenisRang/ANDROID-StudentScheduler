package com.sansara.develop.studentscheduler.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by den on 27.01.2018.
 */

public final class EventContract {

    private EventContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.sansara.develop.studentscheduler";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_TERMS = "terms";
    public static final String PATH_COURSES = "courses";
    public static final String PATH_ASSESSMENTS = "assessments";
    public static final String PATH_MENTORS = "mentors";

    /**
     * Inner class that defines constant values for the terms database table.
     * Each entry in the table represents a single term.
     */
    public static final class TermEntry implements BaseColumns {

        /**
         * The content URI to access the term data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TERMS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of terms.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single term.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERMS;

        public static final String TABLE_NAME = "terms";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_START_TIME = "start";
        public static final String COLUMN_END_TIME = "end";
        public static final String COLUMN_TIME_STAMP = "stamp";
    }

    /**
     * Inner class that defines constant values for the courses database table.
     * Each entry in the table represents a single course.
     */
    public static final class CourseEntry implements BaseColumns {

        /**
         * The content URI to access the course data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_COURSES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of courses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single course.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COURSES;

        public static final String TABLE_NAME = "courses";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_START_TIME = "start";
        public static final String COLUMN_END_TIME = "end";
        public static final String COLUMN_TIME_STAMP = "stamp";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_NOTE = "note";
        // Term which contains this course
        public static final String COLUMN_TERM_ID = "term_id";

        /**
         * Possible values for status.
         */
        public static final int STATUS_UNKNOWN = 0;
        public static final int STATUS_IN_PROGRESS = 1;
        public static final int STATUS_COMPLETED = 2;
        public static final int STATUS_DROPPED = 3;
        public static final int STATUS_PLAN_TO_TAKE = 4;

        /**
         * Returns whether or not the given status is {@link #STATUS_UNKNOWN}, {@link #STATUS_IN_PROGRESS},
         * {@link #STATUS_COMPLETED}, {@link #STATUS_DROPPED} or {@link #STATUS_PLAN_TO_TAKE}.
         */
        public static boolean isValidStatus(int status) {
            if (status == STATUS_UNKNOWN || status == STATUS_IN_PROGRESS || status == STATUS_COMPLETED || status == STATUS_DROPPED || status == STATUS_PLAN_TO_TAKE) {
                return true;
            }
            return false;
        }
    }

    /**
     * Inner class that defines constant values for the assessments database table.
     * Each entry in the table represents a single assessment.
     */
    public static final class AssessmentEntry implements BaseColumns {

        /**
         * The content URI to access the assessments data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ASSESSMENTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of assessments.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSESSMENTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single assessment.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ASSESSMENTS;

        public static final String TABLE_NAME = "assessments";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_START_TIME = "start";
        public static final String COLUMN_END_TIME = "end";
        public static final String COLUMN_TIME_STAMP = "stamp";
        // Course for the assessment
        public static final String COLUMN_COURSE_ID = "course_id";

    }

    /**
     * Inner class that defines constant values for the mentors database table.
     * Each entry in the table represents a single mentor.
     */
    public static final class MentorEntry implements BaseColumns {

        /**
         * The content URI to access the mentor data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MENTORS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of mentors.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MENTORS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single mentor.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MENTORS;

        public static final String TABLE_NAME = "mentors";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
        // Course taught by the mentor
        public static final String COLUMN_COURSE_ID = "course_id";
    }
}