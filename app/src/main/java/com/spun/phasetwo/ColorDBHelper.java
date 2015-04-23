package com.spun.phasetwo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ColorDBHelper extends SQLiteOpenHelper{

    static private final String DB_NAME = "colors.db";
    static private final int DB_VERSION = 1;

    public ColorDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate( SQLiteDatabase db) {
        ColorTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ColorTable.onUpgrade(db, oldVersion, newVersion);
    }


}
