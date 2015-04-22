package com.spun.phasetwo;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomFragment extends ListFragment {

    private static int MAX_SATURATION = 1;
    private static int MAX_VALUE = 1;
    private static int HUE_RANGE;
    private static int NUM_ROWS;

    private ArrayList<float[]> hsvList;
    private boolean first_created = false;
    private String mTag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hsvList = new ArrayList<>();
        Bundle bundle = this.getArguments();
        setVariables(bundle);

        first_created = true;
        setListAdapter(new LevelTwoAdapter(getActivity(), hsvList));
    }

    //empty constructor
    public CustomFragment() {}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!first_created) {
            Bundle bundle = this.getArguments();
            setVariables(bundle);
        }
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        super.onListItemClick(listView, v, position, id);

        first_created = false;

        ((MainActivity)getActivity()).replaceFragment(position);
    }

    private void setVariables(Bundle bundle) {
        NUM_ROWS = bundle.getInt("num_rows");
        float hsv[][] = new float[NUM_ROWS][];
        for (int i = 0; i < hsv.length; i++) {
            hsv[i] = bundle.getFloatArray("color "+i);
        }

        mTag = bundle.getString("tag");
        HUE_RANGE = bundle.getInt("hRange");

        if (!hsvList.isEmpty())
            hsvList.clear();

        for (int i = 0; i < hsv.length; i++) {
            hsvList.add(hsv[i]);
        }
    }

    public void setTag(int tag) {
        switch (tag) {
            case 0: mTag = "First";
                break;
            case 1: mTag = "Second";
                break;
            case 2: mTag = "Third";
                break;
            case 3: mTag = "Fourth";
        }
    }

    public class LevelTwoAdapter extends ArrayAdapter<float[]> {
        private int size;
        public LevelTwoAdapter(Context context, ArrayList<float[]> hsvList) {
            super(context, R.layout.list_item, hsvList);
            size = hsvList.size();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            float[] hsv = getItem(position);
            int colorLeft = Color.WHITE;
            int colorRight = Color.WHITE;

            if (mTag == "First") {
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
                textView.setBackground(drawable);
            }
            else if (mTag == "Second") {
                /*keep the hue range the same, vary the saturation, constant 100%value*/
                colorLeft = Color.HSVToColor(hsv);
                if (position < size - 1)
                    colorRight = Color.HSVToColor(getItem(position + 1));
                else {
                    float[] color = getItem(position);
                    color[0] = color[0] - 30;
                    color[1] = color[1] - MAX_SATURATION/size;
                    colorRight = Color.HSVToColor(color);
                }
                GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colorLeft, colorRight});
                textView.setBackground(drawable);
            }
            else if (mTag == "Third") {
                /*keep the hue range and the saturation range the same, vary the value*/
                colorLeft = Color.HSVToColor(hsv);
                if (position < size - 1)
                    colorRight = Color.HSVToColor(getItem(position + 1));
                else {
                    float[] color = getItem(position);
                    color[0] = color[0] - 30;
                    color[2] = color[2] - MAX_VALUE/size;
                    colorRight = Color.HSVToColor(color);
                }
                GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{colorLeft, colorRight});
                textView.setBackground(drawable);
            }

            return convertView;
        }
    }
}