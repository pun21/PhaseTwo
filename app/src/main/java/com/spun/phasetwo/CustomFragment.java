package com.spun.phasetwo;

import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CustomFragment extends ListFragment implements SwatchLimitDialogFragment.nNoticeDialogListener {

    private static final int NUMBER_OF_COLORS = 1178;
    private static float MAX_SATURATION = 1;
    private static float MAX_VALUE = 1;
    private static float HUE_RANGE;
    private ArrayList<float[]> hsvList;
    private boolean first_created = false;
    private String mTag;
    private int mLimit;
    private String mPrefsKey;
    private LevelTwoAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hsvList = new ArrayList<>();
        Bundle bundle = this.getArguments();
        setVariables(bundle);

        first_created = true;
        adapter = new LevelTwoAdapter(getActivity(), hsvList);
        setListAdapter(adapter);
    }

    private void setPrefsKey() {
        switch(mTag) {
            case "First":
                mPrefsKey = "hue_swatches_limit";
                break;
            case "Second":
                mPrefsKey = "saturation_swatches_limit";
                break;
            case "Third":
                mPrefsKey = "value_swatches_limit";
        }
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
        if (mTag.equals("First")) {
            setButtonGone((View) view.getParent().getParent());
        }
        else if (mTag != "First") {
            setButton((View) view.getParent().getParent());
        }
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }

    private void setButtonGone(View v) {
        Button button = (Button) v.findViewById(R.id.button);
        button.setVisibility(View.GONE);
    }
    private void setButton(View v) {
        Button button = (Button) v.findViewById(R.id.button);
        button.setVisibility(View.VISIBLE);
        button.setText("Configure color swatches");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }
    private void showDialog() {
        DialogFragment swatchLimitDialog = new SwatchLimitDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString("fragmentTag", mTag);
        int which;
        if (mTag.equals("Second"))
            which = 0;
        else
            which = 1;
        bundle.putInt("whichfragment", which);
        bundle.putInt(mPrefsKey, mLimit);
        if (swatchLimitDialog.getArguments() != null)
            swatchLimitDialog.setArguments(null);

        swatchLimitDialog.setArguments(bundle);

        swatchLimitDialog.show(getFragmentManager(), mTag + " swatch_dialog");
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        super.onListItemClick(listView, v, position, id);

        first_created = false;

        ((MainActivity)getActivity()).replaceFragment(position);
    }

    private void setVariables(Bundle bundle) {
        float[] temp;
        if (bundle != null) {
            HUE_RANGE = bundle.getFloat("hRange");
            temp = bundle.getFloatArray("color " + 0);
            mTag = bundle.getString("tag");
            setPrefsKey();

        }else {
            temp = hsvList.get(0);
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mLimit = settings.getInt(mPrefsKey, 256);
        float hsv[][];
        float h = temp[0];
        float s = temp[1];


        if (mTag.equals("First")) {
            mLimit = bundle.getInt("num_rows", 10);
            hsv = new float[mLimit][];
            for (int i = 0; i < hsv.length; i++) {
                hsv[i] = bundle.getFloatArray("color "+i);
            }
        }
        else if (mTag.equals("Second")) {
            double diff = 1.0/mLimit;
            hsv = new float[mLimit][];
            BigDecimal d = new BigDecimal(Double.toString(diff));
            float f = d.floatValue();
            for (int i = 0; i < mLimit; i++) {
                float[] t = {h, 0+(i*(float)diff), 1};
                hsv[i] = t;
            }
        }else if (mTag.equals("Third")) {
            double diff = 1.0/mLimit;
            BigDecimal d = new BigDecimal(Double.toString(diff));
            float f = d.floatValue();
            hsv = new float[mLimit][];
            for (int i = 0; i < mLimit; i++) {
                float[] t = {h, s, 0+(i*(float)diff)};
                hsv[i] = t;
            }
        }else {
            hsv = new float[mLimit][];
        }

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

    @Override
    public void onDialogPositiveClick(int limit) {
        boolean changedLimit = onChangeSwatchLimit(limit);
        mLimit = limit;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(mPrefsKey, mLimit);

        editor.commit();

        if(changedLimit) {
            setVariables(null);
            ((BaseAdapter) ((ListView)getListView()).getAdapter()).notifyDataSetChanged();
            //adapter.notifyDataSetChanged();
        }
    }

    private boolean onChangeSwatchLimit(int limit) {
        if (mLimit == limit)
            return false;

        return true;
    }


}