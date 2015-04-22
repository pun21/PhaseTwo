package com.spun.phasetwo;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

public class ColorTable {
    public static final String COLOR_TABLE = "ColorItems";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COLOR_NUMBER = "number";
    public static final String COLUMN_HUE = "hue";
    public static final String COLUMN_SATURATION = "saturation";
    public static final String COLUMN_VALUE = "value";
    private static final String SQL_DB_CREATE = "CREATE TABLE " + COLOR_TABLE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_COLOR_NUMBER + " integer not null, "
            + COLUMN_HUE + "  num not null "
            + COLUMN_SATURATION + " num not null "
            + COLUMN_VALUE + " num not null "
            + ")";

    private static HashSet<String> VALID_COLUMN_NAMES;

    public static void onCreate( SQLiteDatabase db) {
        db.execSQL( SQL_DB_CREATE );
    }

    public static void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        Log.w(ColorTable.class.getName(), "Upgrading from version " + oldVersion + " to " + newVersion + " which will destroy all old data)");
        db.execSQL( "DROP TABLE IF EXISTS " + COLOR_TABLE );
        onCreate(db);
    }

    static {
        String[] validNames = {
                COLOR_TABLE,
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_COLOR_NUMBER,
                COLUMN_HUE,
                COLUMN_SATURATION,
                COLUMN_VALUE
        };
        VALID_COLUMN_NAMES = new HashSet<String>(Arrays.asList(validNames));
    }

    public static void validateProjection(String[] projection) {
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));

            if (!VALID_COLUMN_NAMES.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
