package com.spun.phasetwo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;



public class CustomCursorAdapter extends CursorAdapter {

    //static public class ViewHolder { TextView tv; }
    public CustomCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View row = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        return row;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //ViewHolder viewHolder = updateValues(view, cursor);
        TextView textView = (TextView) view.findViewById(R.id.text);

        String color = cursor.getString(cursor.getColumnIndexOrThrow(ColorTable.COLUMN_COLOR_NUMBER));
        String text = cursor.getString(cursor.getColumnIndexOrThrow(ColorTable.COLUMN_NAME));

        int count = cursor.getCount();
        Log.d("cursor count = ", String.valueOf(count));
        textView.setBackgroundColor(Color.parseColor(color));
        textView.setText(text);
        textView.setTextSize(18);
    }

//    private ViewHolder updateValues(View view, Cursor cursor) {
//        ViewHolder viewHolder = (ViewHolder) view.getTag();
//
//
//        String text = cursor.getString(cursor.getColumnIndexOrThrow(ColorTable.COLUMN_NAME));
//        int count = cursor.getCount();
//        Log.d("cursor count", String.valueOf(count));
//        viewHolder.tv.setText(text);
//        viewHolder.tv.setTextSize(22);
//        viewHolder.tv.setBackgroundColor(Color.parseColor(color));
//
//        return viewHolder;
//    }
}