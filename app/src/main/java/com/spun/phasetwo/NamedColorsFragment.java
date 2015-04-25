package com.spun.phasetwo;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;

public class NamedColorsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, SortingOptionsDialogFragment.NoticeDialogListener {
    //region variables
    private static float delta = .1f;
    private static float HUE_RANGE = 10;
    private final String[] orderOptions  = {"hsv", "hvs", "shv", "svh", "vhs", "vsh"};
    private final String[] projection = {
            ColorTable.COLUMN_ID,
            ColorTable.COLUMN_NAME,
            ColorTable.COLUMN_COLOR_NUMBER,
            ColorTable.COLUMN_HUE,
            ColorTable.COLUMN_SATURATION,
            ColorTable.COLUMN_VALUE};

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private String selection = null;
    private String[] selectionArgs = null;
    private CustomCursorAdapter adapter;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private String mTag;
    float[] hsv;
    private String mSortingOption;
    private HashMap<String, String> ORDER_BY;
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initOrderByMap();
        Bundle bundle = this.getArguments();
        mTag = bundle.getString("tag");
        //HUE_RANGE = bundle.getFloat("hRange");
        hsv = bundle.getFloatArray("hsv");
        getLoaderManager().initLoader(0, null, this);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSortingOption = settings.getString("sortOrder", "hsv");

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

        float hueStart = hsv[0]-HUE_RANGE;
        float hueStop = hsv[0]+HUE_RANGE;
        float saturationStart = 100*(hsv[1]-delta);
        float saturationStop = 100*(hsv[1]+delta);
        float valueStart = 100*(hsv[2]-delta);
        float valueStop = 100*(hsv[2]+delta);

        selection = ColorTable.COLUMN_HUE+" >= "+ hueStart+ " and "+ColorTable.COLUMN_HUE + " <= "+hueStop+" and "+(ColorTable.COLUMN_SATURATION + " between " + String.valueOf(saturationStart) + " and " + String.valueOf(saturationStop))
        + " and "+(ColorTable.COLUMN_VALUE + " between " + String.valueOf(valueStart) + " and " + String.valueOf(valueStop));

        setSelectionSaturationBetween(15, 20);
        CursorLoader cursorLoader = new CursorLoader(getActivity(), ColorContentProvider.CONTENT_URI, projection, selection, selectionArgs, ORDER_BY.get(mSortingOption));

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

    private void showDialog() {
        DialogFragment sortingDialog = new SortingOptionsDialogFragment();

        Bundle bundle = new Bundle();
        int index = Arrays.asList(orderOptions).indexOf(mSortingOption);
        bundle.putInt("index", index);
        if (sortingDialog.getArguments() != null)
            sortingDialog.setArguments(null);

        sortingDialog.setArguments(bundle);

        sortingDialog.show(getFragmentManager(), "sortingDialog");
    }

    private void initOrderByMap() {
        ORDER_BY = new HashMap<>();
        ORDER_BY.put("hsv", ColorTable.COLUMN_HUE + "," +
                ColorTable.COLUMN_SATURATION + "," +
                ColorTable.COLUMN_VALUE);
        ORDER_BY.put("hvs", ColorTable.COLUMN_HUE + "," +
                ColorTable.COLUMN_VALUE+ "," +
                ColorTable.COLUMN_SATURATION);
        ORDER_BY.put("shv", ColorTable.COLUMN_SATURATION + "," +
                ColorTable.COLUMN_HUE + "," +
                ColorTable.COLUMN_VALUE);
        ORDER_BY.put("svh", ColorTable.COLUMN_SATURATION + "," +
                ColorTable.COLUMN_VALUE + "," +
                ColorTable.COLUMN_HUE);
        ORDER_BY.put("vhs", ColorTable.COLUMN_VALUE + "," +
                ColorTable.COLUMN_HUE + "," +
                ColorTable.COLUMN_SATURATION);
        ORDER_BY.put("vsh", ColorTable.COLUMN_VALUE + "," +
                ColorTable.COLUMN_SATURATION + "," +
                ColorTable.COLUMN_HUE);
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

        setOptionsButton((View) view.getParent().getParent());
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }

    private void setOptionsButton(View v) {
        Button button = (Button) v.findViewById(R.id.button);
        button.setVisibility(View.VISIBLE);
        button.setText("Configure sorting options");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
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

    @Override
    public void onDialogPositiveClick(int optionSelected) {
        boolean sortOrderChanged = onSortOrderChanged(orderOptions[optionSelected]);
        mSortingOption = orderOptions[optionSelected];

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("sortOrder", mSortingOption);

        editor.commit();

        if (sortOrderChanged)
            getLoaderManager().restartLoader(0, null, this);
    }

    private boolean onSortOrderChanged(String sort) {
        if (mSortingOption.equals(sort))
            return false;

        return true;
    }
}

