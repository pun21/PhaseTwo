package com.spun.phasetwo;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class ColorContentProvider extends ContentProvider {

    private ColorDBHelper db;
    static private final String AUTHORITY = "com.spun.phasetwo.provider";
    static private final String BASE_PATH = "colors";
    /*MIME type for a content: URI containing a Cursor of zero or more items.*/
    static private final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/colors";
    static private final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/colors";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    //set up the URIMatcher
    static private final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static private final int TASKS = 1;
    static private final int TASK_ID = 2;

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, TASKS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TASK_ID);
    }
    @Override
    public boolean onCreate() {
        db = new ColorDBHelper( getContext() );
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //projection tells what columns to return 
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        //verify that all the columns that were requested exist
        checkColumns(projection);
        queryBuilder.setTables(ColorTable.COLOR_TABLE);

        int uriType = sURIMatcher.match(uri);
        switch(uriType) {
            case TASKS:
                break;
            case TASK_ID:
                queryBuilder.appendWhere(ColorTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase wdb = db.getWritableDatabase();
        Cursor cursor = queryBuilder.query(wdb, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType =  sURIMatcher.match(uri);
        SQLiteDatabase wdb = db.getWritableDatabase();
        int rowsDeleted = 0;

        switch(uriType) {
            case TASKS:
                rowsDeleted = wdb.delete(ColorTable.COLOR_TABLE, selection, selectionArgs);
                break;
            case TASK_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = wdb.delete(ColorTable.COLOR_TABLE, ColorTable.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = wdb.delete(ColorTable.COLOR_TABLE, ColorTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return rowsDeleted;
    }


    //this or insert [''
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType =  sURIMatcher.match(uri);
        SQLiteDatabase wdb = db.getWritableDatabase();
        int rowsUpdated = 0;
        String id;
        switch(uriType) {
            case TASKS:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = wdb.update(ColorTable.COLOR_TABLE, values, selection, selectionArgs);
                }
                break;
            case TASK_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = wdb.update(ColorTable.COLOR_TABLE, values, ColorTable.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = wdb.update(ColorTable.COLOR_TABLE, values, ColorTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //URIMatcher will tell you what type of URI you were given
        int uriType =  sURIMatcher.match(uri);
        SQLiteDatabase wdb = db.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;

        switch(uriType) {
            case TASKS:
                id = wdb.insert(ColorTable.COLOR_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(AUTHORITY + "/" + BASE_PATH + "/" + id);
    }

    private void checkColumns(String[] projection) {
        String[] available = {ColorTable.COLUMN_ID, ColorTable.COLUMN_NAME, ColorTable.COLUMN_COLOR, ColorTable.COLUMN_HUE, ColorTable.COLUMN_SATURATION, ColorTable.COLUMN_VALUE};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));

            if (!availableColumns.containsAll(requestedColumns))
                throw new IllegalArgumentException("Unknown columns in projection");
        }
    }
}

