package com.amazonaws.mobile.samples.mynotes.services.mock.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 3;
    private static final String SQL_CREATE_TABLE_NOTES = "create table "
            + NotesContact.NoteEntry.TABLE_NAME
            + "("
            + NotesContact.NoteEntry.COLUMN_ID + " integer primary key autoincrement, "
            + NotesContact.NoteEntry.COLUMN_TITLE + " text, "
            + NotesContact.NoteEntry.COLUMN_CONTENT + " text, "
            + NotesContact.NoteEntry.COLUMN_MODIFIED_TIME + " integer, "
            + NotesContact.NoteEntry.COLUMN_TAG + " text)";

    private static final String SQL_DELETE_TABLE_NOTES = "drop table if exists " + NotesContact.NoteEntry.TABLE_NAME;

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_NOTES);

        ContentValues cv = new ContentValues();
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.mm.yy hh:mm:ss");
        for (int i = 0 ; i < 10 ; i++) {
            cv.put(NotesContact.NoteEntry.COLUMN_TITLE, String.format(Locale.US, "Запись %d", i));
            cv.put(NotesContact.NoteEntry.COLUMN_CONTENT, String.format(Locale.US, "Содержимое записи %d", i));
            cv.put(NotesContact.NoteEntry.COLUMN_MODIFIED_TIME, formatForDateNow.format(dateNow));
            cv.put(NotesContact.NoteEntry.COLUMN_TAG, i);
            db.insert(NotesContact.NoteEntry.TABLE_NAME, null, cv);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_NOTES);
        onCreate(db);
    }
}
