package com.amazonaws.mobile.samples.mynotes.services.mock.dbServices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.amazonaws.mobile.samples.mynotes.models.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class NoteDbAdapter {
    private NoteDbHelper dbHelper;
    private SQLiteDatabase database;

    public NoteDbAdapter(Context context) {
        dbHelper = new NoteDbHelper(context.getApplicationContext());
    }

    public NoteDbAdapter open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private Cursor getAllEntries(){
        String[] columns = new String[] {
                NotesContact.NoteEntry.COLUMN_ID,
                NotesContact.NoteEntry.COLUMN_TITLE,
                NotesContact.NoteEntry.COLUMN_CONTENT,
                NotesContact.NoteEntry.COLUMN_MODIFIED_TIME,
                NotesContact.NoteEntry.COLUMN_TAG
        };
        return  database.query(NotesContact.NoteEntry.TABLE_NAME, columns, null, null, null, null, null);
    }

    public List<ShoppingList> getNotes(){
        ArrayList<ShoppingList> Notes = new ArrayList<>();
        Cursor cursor = getAllEntries();
        if(cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_CONTENT));
                String modifiedDate = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_MODIFIED_TIME));
                String tag = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_TAG));
                Notes.add(new ShoppingList(id, title, content, modifiedDate, tag));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return  Notes;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, NotesContact.NoteEntry.TABLE_NAME);
    }

    public ShoppingList getNote(String id){
        ShoppingList Note = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",NotesContact.NoteEntry.TABLE_NAME, NotesContact.NoteEntry.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            String title = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_CONTENT));
            String modifiedDate = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_MODIFIED_TIME));
            String tag = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_TAG));

            Note = new ShoppingList(id, title, content, modifiedDate, tag);
        }
        cursor.close();
        return  Note;
    }

    public long insert(ShoppingList Note){

        ContentValues cv = new ContentValues();
        cv.put(NotesContact.NoteEntry.COLUMN_TITLE, Note.getTitle());
        cv.put(NotesContact.NoteEntry.COLUMN_CONTENT, Note.getContent());
        cv.put(NotesContact.NoteEntry.COLUMN_MODIFIED_TIME, Note.getDateModified());
        cv.put(NotesContact.NoteEntry.COLUMN_TAG, Note.getTag());

        return  database.insert(NotesContact.NoteEntry.TABLE_NAME, null, cv);
    }

    public long delete(long NoteId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(NoteId)};
        return database.delete(NotesContact.NoteEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public long update(ShoppingList Note){

        String whereClause = NotesContact.NoteEntry.COLUMN_ID + "=" + String.valueOf(Note.getNoteId());
        ContentValues cv = new ContentValues();
        cv.put(NotesContact.NoteEntry.COLUMN_TITLE, Note.getTitle());
        cv.put(NotesContact.NoteEntry.COLUMN_CONTENT, Note.getContent());
        cv.put(NotesContact.NoteEntry.COLUMN_MODIFIED_TIME, Note.getDateModified());
        cv.put(NotesContact.NoteEntry.COLUMN_TAG, Note.getTag());
        return database.update(NotesContact.NoteEntry.TABLE_NAME, cv, whereClause, null);
    }
}
