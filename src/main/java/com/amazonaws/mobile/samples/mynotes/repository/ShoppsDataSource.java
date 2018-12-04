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
package com.amazonaws.mobile.samples.mynotes.repository;

import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.mobile.samples.mynotes.models.ShoppingList;
import com.amazonaws.mobile.samples.mynotes.models.PagedListConnectionResponse;
import com.amazonaws.mobile.samples.mynotes.models.ResultCallback;
import com.amazonaws.mobile.samples.mynotes.services.DataService;

/**
 * A DataSource implements a paging system for a RecyclerView.  This one uses the DataService
 * to provide a paged view into the Notes API.
 */
public class ShoppsDataSource extends PageKeyedDataSource<String,ShoppingList> {
    private static final String TAG = "ShoppsDataSource";
    private DataService dataService;

    ShoppsDataSource(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * Part of the PageKeyedDataSource - load the first page of a list.  This is called when
     * the data source is first created or invalidated and assumes no nextToken has been
     * provided
     * @param params information about what to load
     * @param callback callback that receives the response
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull final LoadInitialCallback<String, ShoppingList> callback) {
        Log.d(TAG, String.format("loadInitial(%d)", params.requestedLoadSize));
        dataService.loadNotes(params.requestedLoadSize, null, (PagedListConnectionResponse<ShoppingList> result) -> {
            callback.onResult(result.getItems(), null, result.getNextToken());
        });
    }

    /**
     * Part of the PageKeyedDataSource - load the next page of a list.  This is called after
     * the loadInitial() has returned a response that includes a nextToken to load the next
     * page of items.
     */
    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull final LoadCallback<String, ShoppingList> callback) {
        Log.d(TAG, String.format("loadAfter(%d, %s)", params.requestedLoadSize, params.key));
        dataService.loadNotes(params.requestedLoadSize, params.key, (PagedListConnectionResponse<ShoppingList> result) -> {
            callback.onResult(result.getItems(), result.getNextToken());
        });
    }

    /**
     * Part of the PageKeyedDataSource - normally used to load the previous page, but this
     * version does not support paging backwards, so it becomes an invalidation.
     */
    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, ShoppingList> callback) {
        Log.d(TAG, String.format("loadBefore(%d, %s)", params.requestedLoadSize, params.key));
        invalidate();
    }

    /**
     * The notes repository needs to do non-paged list type operations as well.  These operations
     * are passed through directly to the data service.  However, deletions (and saves) affect
     * the list, so we must ensure that the list is invalidated when they succeed.  This call is
     * to delete an item.
     */
    public void deleteItem(String noteId, @NonNull final ResultCallback<Boolean> callback) {
        dataService.deleteNote(noteId, (Boolean result) -> {
            if (result) invalidate();
            callback.onResult(result);
        });
    }

    /**
     * Obtain a single item from the data service.
     */
    public void getItem(String noteId, @NonNull ResultCallback<ShoppingList> callback) {
        dataService.getNote(noteId, callback);
    }

    /**
     * The notes repository needs to do non-paged list type operations as well.  These operations
     * are passed through directly to the data service.  However, deletions (and saves) affect
     * the list, so we must ensure that the list is invalidated when they succeed.  This call is
     * to save an item.
     */
    public void createItem(@NonNull String title, @NonNull String content, @NonNull final ResultCallback<ShoppingList> callback) {
        dataService.createNote(title, content, (ShoppingList result) -> {
            if (result != null) invalidate();
            callback.onResult(result);
        });
    }

    public void updateItem(@NonNull ShoppingList note, @NonNull final ResultCallback<ShoppingList> callback) {
        dataService.updateNote(note, (ShoppingList result) -> {
            if (result != null) invalidate();
            callback.onResult(result);
        });
    }
}
