package com.amazonaws.mobile.samples.mynotes.services.mock.dbServices;

import android.provider.BaseColumns;

public class NotesContact {

    private NotesContact() {
    }

    public static class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_MODIFIED_TIME = "modified_time";
        public static final String COLUMN_TAG = "tag";
    }
}
