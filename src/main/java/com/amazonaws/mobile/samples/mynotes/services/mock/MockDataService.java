/*
Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy of this
software and associated documentation files (the "Software"), to deal in the Software
without restriction, including without limitation the rights to use, copy, modify,
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.amazonaws.mobile.samples.mynotes.services.mock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.amazonaws.mobile.samples.mynotes.models.ShoppingList;
import com.amazonaws.mobile.samples.mynotes.models.PagedListConnectionResponse;
import com.amazonaws.mobile.samples.mynotes.models.ResultCallback;
import com.amazonaws.mobile.samples.mynotes.services.DataService;
import com.amazonaws.mobile.samples.mynotes.services.mock.dbServices.NoteDbHelper;
import com.amazonaws.mobile.samples.mynotes.services.mock.dbServices.NotesContact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A mock data store.  This will create 30 notes so you can see the scrolling action, but
 * otherwise acts as a data service.  This should be easily rewritten to use an actual cloud API
 */
public class MockDataService implements DataService {
    private ArrayList<ShoppingList> items;
    private NoteDbHelper dbHelper;
    private SQLiteDatabase database;

    public MockDataService(Context context) {
        dbHelper = new NoteDbHelper(context.getApplicationContext());
        database = dbHelper.getWritableDatabase();

        items = new ArrayList<>();
        Cursor cursor = getAllEntries();
        if(cursor.moveToFirst()){
            do{
                String id = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_TITLE));
                String content = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_CONTENT));
                String modifiedDate = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_MODIFIED_TIME));
                String tag = cursor.getString(cursor.getColumnIndex(NotesContact.NoteEntry.COLUMN_TAG));
                ShoppingList item = new ShoppingList(id, title, content, modifiedDate, tag);
                items.add(item);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
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

    /**
     * Simulate an API call to a network service that returns paged data.
     *
     * @param limit the requested number of items
     * @param after the "next token" from a prior call
     * @param callback the response from the server
     */
    @Override
    public void loadNotes(int limit, String after, ResultCallback<PagedListConnectionResponse<ShoppingList>> callback) {
        if (limit < 1 || limit > 100) throw new IllegalArgumentException("Limit must be between 1 and 100");

        int firstItem = 0;
        if (after != null) {
            firstItem = indexOfFirst(after);
            if (firstItem < 0) {
                callback.onResult(new PagedListConnectionResponse<>(Collections.<ShoppingList>emptyList(), null));
                return;
            }
            firstItem++;
        }
        if (firstItem > items.size() - 1) {
            callback.onResult(new PagedListConnectionResponse<>(Collections.<ShoppingList>emptyList(), null));
            return;
        }
        int nItems = Math.min(limit, items.size() - firstItem);
        if (nItems == 0) {
            callback.onResult(new PagedListConnectionResponse<>(Collections.<ShoppingList>emptyList(), null));
            return;
        }

        List<ShoppingList> sublist = new ArrayList<>(items.subList(firstItem, firstItem + nItems));
        String nextToken = (firstItem + nItems - 1 == items.size()) ? null : sublist.get(sublist.size() - 1).getNoteId();
        callback.onResult(new PagedListConnectionResponse<>(sublist, nextToken));
    }

    /**
     * Load a single note from the current list of notes
     *
     * @param noteId the request ID
     * @param callback the response from the server
     */
    @Override
    public void getNote(String noteId, ResultCallback<ShoppingList> callback) {
        if (noteId == null || noteId.isEmpty()) throw new IllegalArgumentException();

        int idx = indexOfFirst(noteId);
        callback.onResult(idx >= 0 ? items.get(idx) : null);
    }

    /**
     * Create a new note a note to the backing store
     *
     * @param title the title of the new note
     * @param content the content for the new note
     * @param callback the response from the server (null would indicate that the operation failed)
     */
    @Override
    public void createNote(String title, String content, ResultCallback<ShoppingList> callback) {
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.mm.yy hh:mm:ss");
        ShoppingList note = new ShoppingList();
        note.setTitle(formatForDateNow.format(dateNow));
        note.setContent(content);
        note.setTag("5");
        note.setDateModified(formatForDateNow.format(dateNow));

        ContentValues cv = new ContentValues();
        cv.put(NotesContact.NoteEntry.COLUMN_TITLE, note.getTitle());
        cv.put(NotesContact.NoteEntry.COLUMN_CONTENT, note.getContent());
        cv.put(NotesContact.NoteEntry.COLUMN_MODIFIED_TIME, note.getDateModified());
        cv.put(NotesContact.NoteEntry.COLUMN_TAG, note.getTag());

        long id = database.insert(NotesContact.NoteEntry.TABLE_NAME, null, cv);
        note.setNoteId(Long.toString(id));
        items.add(note);
        callback.onResult(note);
    }

    /**
     * Update an existing note in the backing store
     *
     * @param note the new contents of the note
     * @param callback the response from the server (null would indicate that the operation failed)
     */
    @Override
    public void updateNote(ShoppingList note, ResultCallback<ShoppingList> callback) {
        int idx = indexOfFirst(note.getNoteId());
        if (idx >= 0) {
            String whereClause = NotesContact.NoteEntry.COLUMN_ID + "=" + String.valueOf(note.getNoteId());
            ContentValues cv = new ContentValues();
            cv.put(NotesContact.NoteEntry.COLUMN_TITLE, note.getTitle());
            cv.put(NotesContact.NoteEntry.COLUMN_CONTENT, note.getContent());
            cv.put(NotesContact.NoteEntry.COLUMN_MODIFIED_TIME, note.getDateModified());
            cv.put(NotesContact.NoteEntry.COLUMN_TAG, note.getTag());
            database.update(NotesContact.NoteEntry.TABLE_NAME, cv, whereClause, null);

            items.set(idx, note);
            callback.onResult(note);
        } else {
            callback.onResult(null);
        }
    }

    /**
     * Delete a note from the backing store
     *
     * @param noteId the ID of the note to be deleted
     * @param callback the response from the server (Boolean = true indicates success)
     */
    @Override
    public void deleteNote(String noteId, ResultCallback<Boolean> callback) {
        if (noteId == null || noteId.isEmpty()) throw new IllegalArgumentException();

        int idx = indexOfFirst(noteId);
        if (idx >= 0) {
            items.remove(idx);
            String whereClause = "_id = ?";
            String[] whereArgs = new String[]{String.valueOf(noteId)};
            database.delete(NotesContact.NoteEntry.TABLE_NAME, whereClause, whereArgs);
        }
        callback.onResult(idx >= 0);
    }

    /**
     * Returns the index of the first note that matches
     * @param noteId the note to match
     * @return the index of the note, or -1 if not found
     */
    private int indexOfFirst(String noteId) {
        if (items.isEmpty()) throw new IndexOutOfBoundsException();
        for (int i = 0 ; i < items.size() ; i++) {
            if (items.get(i).getNoteId().equals(noteId))
                return i;
        }
        return -1;
    }
}
