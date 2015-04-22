package com.spun.phasetwo;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ColorTable {
    static public final String COLOR_TABLE = "Color";
    static public final String COLUMN_ID = "_id";
    static public final String COLUMN_NAME = "name";
    static public final String COLUMN_COLOR = "color";
    static public final String COLUMN_HUE = "hue";
    static public final String COLUMN_SATURATION = "saturation";
    static public final String COLUMN_VALUE = "value";
    static private final String SQL_DB_CREATE = "CREATE TABLE " + COLOR_TABLE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_COLOR + " integer not null, "
            + COLUMN_HUE + "  num not null "
            + COLUMN_SATURATION + " num not null "
            + COLUMN_VALUE + " num not null "
            + ")";

    static public void onCreate( SQLiteDatabase db) {
        db.execSQL( SQL_DB_CREATE );
    }

    static public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
        Log.w(ColorTable.class.getName(), "Upgrading from version " + oldVersion + " to " + newVersion + " (All data destroyed)");
        db.execSQL( "DROP TABLE IF EXISTS " + COLOR_TABLE );
        onCreate(db);
    }
}
