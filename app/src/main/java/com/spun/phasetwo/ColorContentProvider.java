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
import android.util.Log;

public class ColorContentProvider extends ContentProvider {

    private ColorDBHelper db;
    private static final String AUTHORITY = "com.spun.phasetwo.provider";
    private static final String BASE_PATH = "colors";
    private static final String HUE_PATH = "hue";
    /*MIME type for a content: URI containing a Cursor of zero or more items.*/
    private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/colors";
    private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/color";
    public static final String CONTENT_URI_PREFIX = "content://" + AUTHORITY + "/" + BASE_PATH + "/";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);


    //set up the URIMatcher
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int ALL_COLORS = 1;
    private static final int COLOR_NUMBER = 2;
    private static final int COLOR_HUE = 3;


    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, ALL_COLORS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", COLOR_NUMBER);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/"+HUE_PATH+"/#/#", COLOR_HUE);
    }

    @Override
    public boolean onCreate() {
        db = new ColorDBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //projection tells what columns to return
        ColorTable.validateProjection(projection);
        int uriType = sURIMatcher.match(uri);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        //sets the list of tables to query
        queryBuilder.setTables(ColorTable.COLOR_TABLE);
        String segment = uri.getLastPathSegment();
        switch(uriType) {
            case ALL_COLORS:
                break;
            case COLOR_NUMBER:
                queryBuilder.appendWhere(ColorTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case COLOR_HUE:
                Log.d("insert", "before append");
                queryBuilder.appendWhere((ColorTable.COLUMN_HUE+" >= "+uri.getLastPathSegment()));
                Log.d("insert", "after append");
                //queryBuilder.appendWhere((ColorTable.COLUMN_HUE+" <= "+uri.getLastPathSegment()));
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase database = db.getWritableDatabase();
        Log.d("insert", "selection = " + selection);
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        int uriType =  sURIMatcher.match(uri);
        int rowsDeleted = 0;

        switch(uriType) {
            case ALL_COLORS:
                rowsDeleted = database.delete(ColorTable.COLOR_TABLE, selection, selectionArgs);
                break;
            case COLOR_NUMBER:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = database.delete(ColorTable.COLOR_TABLE, ColorTable.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = database.delete(ColorTable.COLOR_TABLE, ColorTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase database = db.getWritableDatabase();
        int uriType =  sURIMatcher.match(uri);
        int rowsUpdated = 0;

        switch(uriType) {
            case ALL_COLORS:
                    rowsUpdated = database.update(ColorTable.COLOR_TABLE, values, selection, selectionArgs);
                break;
            case COLOR_NUMBER:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = database.update(ColorTable.COLOR_TABLE, values, ColorTable.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = database.update(ColorTable.COLOR_TABLE, values, ColorTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //URIMatcher will tell you what type of URI you were given
        SQLiteDatabase database = db.getWritableDatabase();
        int uriType =  sURIMatcher.match(uri);

        long id = 0;

        switch(uriType) {
            case ALL_COLORS:
                id = database.insert(ColorTable.COLOR_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(CONTENT_URI_PREFIX + id);
    }
}

