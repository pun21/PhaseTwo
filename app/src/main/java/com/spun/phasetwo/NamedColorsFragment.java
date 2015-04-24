package com.spun.phasetwo;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class NamedColorsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static float delta = .2f;
    private static float HUE_RANGE = 15;

    private String selection = null;
    private String[] selectionArgs = null;
    private CustomCursorAdapter adapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private String mTag;
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
        //HUE_RANGE = bundle.getFloat("hRange");
        hsv = bundle.getFloatArray("hsv");
        getLoaderManager().initLoader(0, null, this);

        adapter = new CustomCursorAdapter(getActivity(), null);

        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader(0, null, this);
    }

    public NamedColorsFragment() {}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ColorTable.COLUMN_ID,
                ColorTable.COLUMN_NAME,
                ColorTable.COLUMN_COLOR_NUMBER,
                ColorTable.COLUMN_HUE,
                ColorTable.COLUMN_SATURATION,
                ColorTable.COLUMN_VALUE};
        float hueStart = hsv[0]-HUE_RANGE;
        float hueStop = hsv[0]+HUE_RANGE;
        float saturationStart = 100*(hsv[1]-delta);
        float saturationStop = 100*(hsv[1]+delta);
        float valueStart = 100*(hsv[2]-delta);
        float valueStop = 100*(hsv[2]+delta);
        selection = ColorTable.COLUMN_HUE+" >= "+ hueStart+ " and "+ColorTable.COLUMN_HUE + " <= "+hueStop+" and "+(ColorTable.COLUMN_SATURATION + " between " + String.valueOf(saturationStart) + " and " + String.valueOf(saturationStop))
        + " and "+(ColorTable.COLUMN_VALUE + " between " + String.valueOf(valueStart) + " and " + String.valueOf(valueStop));

        String orderBy = ColorTable.COLUMN_HUE;
        setSelectionSaturationBetween(15, 20);
        CursorLoader cursorLoader = new CursorLoader(getActivity(), ColorContentProvider.CONTENT_URI, projection, selection, selectionArgs, orderBy);

        return cursorLoader;
    }
    private void setSelectionSubstring(String substring) {
        selection =  (  ColorTable.COLUMN_NAME + " like '%" + substring+"'") + " or " +
                        ColorTable.COLUMN_NAME + " like '"+substring+"%'" + " or " +
                        ColorTable.COLUMN_NAME + " like '%"+substring+"%'";
    }
    private void setSelectionHue(float hue) {
        selection = ColorTable.COLUMN_HUE + " = " + String.valueOf(hue);
    }
    private void setSelectionSaturationBelow(float percent) {
        selection = ColorTable.COLUMN_SATURATION + " < " + String.valueOf(percent);
    }
    private void setSelectionSaturationBetween(float lowPercent, float highPercent) {
        selection = ColorTable.COLUMN_SATURATION + " between "+String.valueOf(lowPercent)+" and "+String.valueOf(highPercent);
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
        String color = cursor.getString(cursor.getColumnIndexOrThrow(ColorTable.COLUMN_COLOR_NUMBER));
        float[] hsv = new float[3];

        Color.colorToHSV(Color.parseColor(color), hsv);

        String colorInfo = "Hue: " + (int)hsv[0] + " degrees" + "\n" +
                "Saturation: " + (int)(hsv[1]*100) + "%" + "\n" +
                "Value: " + (int)(hsv[2]*100) + "%" + "\n";
        Toast.makeText(getActivity(), colorInfo, Toast.LENGTH_SHORT).show();
    }

}

