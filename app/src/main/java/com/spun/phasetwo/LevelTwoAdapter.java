package com.spun.phasetwo;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class LevelTwoAdapter extends ArrayAdapter<float[]> {
    private int size;
    private float HUE_RANGE = 60;
    private String mTag = "First";
    public LevelTwoAdapter(Context context, ArrayList<float[]> hsvList) {
        super(context, R.layout.list_item, hsvList);
        size = hsvList.size();
    }
    static class ViewHolder{TextView textView;}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        float[] hsv = getItem(position);
        int colorLeft;
        int colorRight;

        if (mTag.equals("First")) {
            //vary the hue, constant 100% saturation, constant 100%value*/
            colorLeft = Color.HSVToColor(hsv);
            if (position < size - 1)
                colorRight = Color.HSVToColor(getItem(position + 1));
            else {
                float[] color = getItem(position);
                color[0] = color[0] + HUE_RANGE;
                colorRight = Color.HSVToColor(color);
            }
            GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colorLeft, colorRight});
            holder.textView.setBackground(drawable);
        }
        else if (mTag.equals("Second")) {
                /*keep the hue range the same, vary the saturation, constant 100%value*/
            colorLeft = Color.HSVToColor(hsv);
            float[] color = hsv;
            color[0] = color[0] + HUE_RANGE;
            colorRight = Color.HSVToColor(color);
            GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colorLeft, colorRight});
            holder.textView.setBackground(drawable);
        }
        else if (mTag.equals("Third")) {
                /*keep the hue range and the saturation range the same, vary the value*/
            colorLeft = Color.HSVToColor(hsv);
            float[] color = hsv;
            color[0] = color[0] + HUE_RANGE;
            colorRight = Color.HSVToColor(color);
            GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colorLeft, colorRight});
            holder.textView.setBackground(drawable);
        }
        else {
            holder.textView.setBackgroundColor(00000000);
        }

        return convertView;
    }
}