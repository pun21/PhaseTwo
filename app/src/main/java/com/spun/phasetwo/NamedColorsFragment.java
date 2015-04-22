package com.spun.phasetwo;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NamedColorsFragment extends ListFragment {
    private static float delta = .2f;
    private static int NUM_ROWS = 10;
    private static float HUE_RANGE;
    private static float HUE_START;
    private static float HUE_END;

    private String mTag;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mTag = bundle.getString("tag");
        HUE_RANGE = bundle.getFloat("hRange");
        ArrayList<NamedColor> colorList = setColorList(bundle.getFloatArray("hsv"));
        setListAdapter(new NamedColorsAdapter(getActivity(), colorList));
    }

    private ArrayList<NamedColor> setColorList(float[] hsv) {
        ArrayList<NamedColor> list = new ArrayList<>();

        HUE_START = hsv[0];
        HUE_END = hsv[0] + HUE_RANGE;
        float hueDiff = (HUE_END - HUE_START)/NUM_ROWS;
        //saturation and value diff will be the same
        float saturationDiff = (2*delta)/NUM_ROWS;
        float saturationStart = hsv[1]-delta;
        float valueStart = hsv[2]-delta;

        for (int i = 0; i < NUM_ROWS; i++) {
            float[] tempHue = new float[] {HUE_START + i*hueDiff, saturationStart + i*saturationDiff, valueStart + i*saturationDiff};
            NamedColor namedColor = new NamedColor(tempHue, "Put Name Here");
            list.add(namedColor);
        }

        return list;
    }
    //empty constructor
    public NamedColorsFragment() {}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        super.onListItemClick(listView, v, position, id);

        NamedColor colorItem = (NamedColor)listView.getItemAtPosition(position);
        float[] hsv = colorItem.getHsv();

        String colorInfo = "Hue: " + (int)hsv[0] + " degrees" + "\n" +
                "Saturation: " + (int)(hsv[1]*100) + "%" + "\n" +
                "Value: " + (int)(hsv[2]*100) + "%" + "\n";
        Toast.makeText(getActivity(), colorInfo, Toast.LENGTH_SHORT).show();
    }


    public class NamedColorsAdapter extends ArrayAdapter<NamedColor> {
        private int size;
        public NamedColorsAdapter(Context context, ArrayList<NamedColor> colorList) {
            super(context, R.layout.list_item, colorList);
            size = colorList.size();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

            NamedColor colorItem = getItem(position);

            float[] hsv = colorItem.getHsv();
            int color = Color.HSVToColor(hsv);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(colorItem.getName());
            textView.setBackgroundColor(color);
            return convertView;
        }
    }
}

