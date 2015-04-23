package com.spun.phasetwo;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NamedColorsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static float delta = .2f;
    private static int NUM_ROWS = 10;
    private static float HUE_RANGE;
    private static float HUE_START;
    private static float HUE_END;

    private CustomCursorAdapter adapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private String mTag;
    private String selection;
    private String orderBy;
    float[] hsv;
    //region ORDER_BY_
    static public final String ORDER_BY_HSV = ColorTable.COLUMN_HUE + "," +
                                              ColorTable.COLUMN_SATURATION + "," +
                                              ColorTable.COLUMN_VALUE;
    static public final String ORDER_BY_HVS = ColorTable.COLUMN_HUE + "," +
                                              ColorTable.COLUMN_VALUE+ "," +
                                              ColorTable.COLUMN_SATURATION;
    static public final String ORDER_BY_SHV = ColorTable.COLUMN_SATURATION + "," +
                                              ColorTable.COLUMN_HUE + "," +
                                              ColorTable.COLUMN_VALUE;
    static public final String ORDER_BY_SVH = ColorTable.COLUMN_SATURATION + "," +
                                              ColorTable.COLUMN_VALUE + "," +
                                              ColorTable.COLUMN_HUE;
    static public final String ORDER_BY_VHS = ColorTable.COLUMN_VALUE + "," +
                                              ColorTable.COLUMN_HUE + "," +
                                              ColorTable.COLUMN_SATURATION;
    static public final String ORDER_BY_VSH = ColorTable.COLUMN_VALUE + "," +
                                              ColorTable.COLUMN_SATURATION + "," +
                                              ColorTable.COLUMN_HUE;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        mTag = bundle.getString("tag");
        HUE_RANGE = bundle.getFloat("hRange");
        hsv = bundle.getFloatArray("hsv");
        //ArrayList<NamedColor> colorList = setColorList(bundle.getFloatArray("hsv"));
        getLoaderManager().initLoader(0, null, this);

        ListView lv = (ListView)getActivity().findViewById(R.id.list);
        adapter = new CustomCursorAdapter(getActivity(), null);

        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader(0, null, this);
    }

//    private ArrayList<NamedColor> setColorList(float[] hsv) {
//        ArrayList<NamedColor> list = new ArrayList<>();
//
//        HUE_START = hsv[0];
//        HUE_END = hsv[0] + HUE_RANGE;
//        float hueDiff = (HUE_END - HUE_START)/NUM_ROWS;
//        //saturation and value diff will be the same
//        float saturationDiff = (2*delta)/NUM_ROWS;
//        float saturationStart = hsv[1]-delta;
//        float valueStart = hsv[2]-delta;
//
//        for (int i = 0; i < NUM_ROWS; i++) {
//            float[] tempHue = new float[] {HUE_START + i*hueDiff, saturationStart + i*saturationDiff, valueStart + i*saturationDiff};
//            NamedColor namedColor = new NamedColor(tempHue, "Put Name Here");
//            list.add(namedColor);
//        }
//
//        return list;
//    }
//    //empty constructor
//    public NamedColorsFragment() {}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ColorTable.COLUMN_ID,
                ColorTable.COLUMN_NAME,
                ColorTable.COLUMN_COLOR_NUMBER,
                ColorTable.COLUMN_HUE,
                ColorTable.COLUMN_SATURATION,
                ColorTable.COLUMN_VALUE};
        String selection = ColorTable.COLUMN_HUE + " > " + (hsv[0]-HUE_RANGE) + " and " + ColorTable.COLUMN_HUE + " < " + (hsv[0]+HUE_RANGE + " and " +
                ColorTable.COLUMN_SATURATION + " > " +(hsv[1]-delta) + " and " + ColorTable.COLUMN_SATURATION + " < " + (hsv[1]+delta)+ " and "+
                ColorTable.COLUMN_VALUE + " > " +(hsv[2]-delta) + " and " + ColorTable.COLUMN_VALUE + " < " + (hsv[2]+delta));
        String orderBy = ColorTable.COLUMN_HUE;

        CursorLoader cursorLoader = new CursorLoader(getActivity(), ColorContentProvider.CONTENT_URI, projection, null, null, orderBy);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        super.onListItemClick(listView, v, position, id);

        Cursor cursor = (Cursor)listView.getItemAtPosition(position);
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(ColorTable.COLUMN_COLOR_NUMBER));
        float[] hsv = new float[3];

        Color.colorToHSV(color, hsv);

        String colorInfo = "Hue: " + (int)hsv[0] + " degrees" + "\n" +
                "Saturation: " + (int)(hsv[1]*100) + "%" + "\n" +
                "Value: " + (int)(hsv[2]*100) + "%" + "\n";
        Toast.makeText(getActivity(), colorInfo, Toast.LENGTH_SHORT).show();
    }


    public class CustomCursorAdapter extends CursorAdapter {
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
            TextView textView = (TextView) view.findViewById(R.id.text);

            int color = cursor.getInt(cursor.getColumnIndexOrThrow(ColorTable.COLUMN_COLOR_NUMBER));
            String text = cursor.getString(cursor.getColumnIndexOrThrow(ColorTable.COLUMN_NAME));

            textView.setBackgroundColor(color);
            textView.setText(text);
            textView.setTextSize(18);
        }
    }
//    public class NamedColorsAdapter extends ArrayAdapter<NamedColor> {
//        private int size;
//        public NamedColorsAdapter(Context context, ArrayList<NamedColor> colorList) {
//            super(context, R.layout.list_item, colorList);
//            size = colorList.size();
//        }
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
//            }
//
//            NamedColor colorItem = getItem(position);
//
//            float[] hsv = colorItem.getHsv();
//            int color = Color.HSVToColor(hsv);
//            TextView textView = (TextView) convertView.findViewById(R.id.text);
//            textView.setText(colorItem.getName());
//            textView.setBackgroundColor(color);
//            return convertView;
//        }
//    }
}

