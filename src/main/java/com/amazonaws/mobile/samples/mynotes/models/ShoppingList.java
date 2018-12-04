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
package com.amazonaws.mobile.samples.mynotes.models;

import java.util.UUID;

/**
 * Model for a single ShoppingList
 */
public class ShoppingList {
    private String noteId;
    private String title;
    private String content;
    private String date;
    private String tag;

    public ShoppingList() {
        noteId = UUID.randomUUID().toString();
        title = "";
        content = "";
    }

    public ShoppingList(String noteId) {
        this.noteId = noteId;
        title = "";
        content = "";
    }

    public ShoppingList(String noteId, String title, String content) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
    }

    public ShoppingList(String noteId, String title, String content, String date, String tag) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.date = date;
        this.tag = tag;
    }


    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTag(String content) {
        this.tag = content;
    }

    public String getTag() {
        return tag;
    }

    public String getDateModified() {
        return date;
    }

    public void setDateModified(String dateModified) {
        this.date = dateModified;
    }

    public void add (String noteId, String title, String content, String date, String tag) {
        this.noteId = noteId;
        this.title = title;
        this.content = content;
        this.date = date;
        this.tag = tag;
    }
}
