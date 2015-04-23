package com.spun.phasetwo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ColorCursorAdapter extends CursorAdapter {

    private static final int ID = 0;
    private static final int COLOR_NAME = 1;
    private static final int COLOR_NUMBER = 2;

    static public final String[] PROJECTION = new String [] {
            ColorTable.COLUMN_ID,
            ColorTable.COLUMN_NAME,
            ColorTable.COLUMN_COLOR_NUMBER,
            ColorTable.COLUMN_HUE,
            ColorTable.COLUMN_SATURATION,
            ColorTable.COLUMN_VALUE
    };

    static public final String ORDER_BY = ColorTable.COLUMN_HUE + "," +
                                          ColorTable.COLUMN_SATURATION + "," +
                                          ColorTable.COLUMN_VALUE;

    static private final int TRUE = 1;

    static private class ViewHolder {
        TextView colorName;
    }

    private LayoutInflater mInflater;

    public ColorCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
