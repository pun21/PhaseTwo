package com.spun.phasetwo;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import java.util.ArrayList;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    //region variables
    private static float MAX_SATURATION = 1;
    private static float MAX_VALUE = 1;
    private static float HUE_RANGE = 30;
    private static float SATURATION_DIFF = .1f;
    private static float VALUE_DIFF = .1f;
    private static int NUM_HUE_ROWS = 360/(int)HUE_RANGE;
    private static int NUM_SATURATION_ROWS = (int)(MAX_SATURATION/SATURATION_DIFF);
    private static int NUM_VALUE_ROWS = (int)(MAX_VALUE/VALUE_DIFF)+1;

    private Bundle savedBundle;
    private ArrayList<float[]> hsvList;
    private CustomFragment huesFragment, saturationFragment, valuesFragment;
    private SummaryFragment summaryFragment;
    private NamedColorsFragment namedColorsFragment;
    private String[] mFragmentTag = new String[]{"First", "Second", "Third", "Fourth"};
    private int mFragmentIndex = 0;
    private float h;
    private float s;
    private float v;
    //endregion

    //region Lifecycle and related methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            ArrayList<float[]> hsvList = setHSV(0, 0, 0);

            huesFragment = new CustomFragment();
            Bundle bundle = new Bundle();
            for (int i = 0; i < NUM_HUE_ROWS; i++) {
                bundle.putFloatArray("color "+i, hsvList.get(i));
            }
            bundle.putString("tag", mFragmentTag[0]);
            bundle.putFloat("hRange", HUE_RANGE);
            bundle.putInt("num_rows", NUM_HUE_ROWS);
            huesFragment.setArguments(bundle);

         /*The first fragment contains gradients of pure spectral hues with 100% saturation
        /*100% value.
        */
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(android.R.id.content, huesFragment, mFragmentTag[mFragmentIndex]);
            ft.addToBackStack(null);
            ft.commit();
            mFragmentIndex++;
        }
        savedBundle = savedInstanceState;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (savedBundle != null) {
            mFragmentIndex = savedBundle.getInt("fragment_index");
            h = savedBundle.getFloat("h");
            s = savedBundle.getFloat("s");
            v = savedBundle.getFloat("v");
            mFragmentIndex++;
            restoreFragments();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fragment_index",--mFragmentIndex);
        outState.putFloat("h", h);
        outState.putFloat("s", s);
        outState.putFloat("v", v);
        savedBundle = outState;
    }
    //endregion
    private void restoreFragments() {
        huesFragment = (CustomFragment) getFragmentManager().findFragmentByTag(mFragmentTag[0]);
        saturationFragment = (CustomFragment) getFragmentManager().findFragmentByTag(mFragmentTag[1]);
        valuesFragment = (CustomFragment) getFragmentManager().findFragmentByTag(mFragmentTag[2]);
        namedColorsFragment = (NamedColorsFragment) getFragmentManager().findFragmentByTag(mFragmentTag[3]);
    }

    private ArrayList<float[]> setHSV(float selectedHue, float selectedSaturation, float selectedValue) {
        int i;
        float hue = 0;
        float saturation = MAX_SATURATION;
        float value = MAX_VALUE;
        ArrayList<float[]> hsvList = new ArrayList<>();
        float[][] hsv;
        switch (mFragmentIndex) {
            case 0: hsv = new float[NUM_HUE_ROWS][];
                for (i = 0; i < hsv.length; i++) {
                    float[] temp = new float[]{hue, saturation, value};
                    hsv[i] = temp;
                    hue+=HUE_RANGE;
                }
                break;
            case 1: hsv = new float[NUM_SATURATION_ROWS][];
                hue = selectedHue;
                for (i = 0; i < hsv.length; i++) {
                    float[] temp = new float[]{hue, saturation, value};
                    hsv[i] = temp;
                    saturation-=SATURATION_DIFF;
                }
                break;
            case 2: hsv = new float[NUM_VALUE_ROWS][];
                hue = selectedHue;
                saturation = selectedSaturation;
                for (i = 0; i < hsv.length; i++) {
                    float[] temp = new float[]{hue, saturation, value};
                    hsv[i] = temp;
                    value-=VALUE_DIFF;
                }
                break;
            default: hsv = new float[][]{{0, 0, 0}};
        }

        for (i = 0; i < hsv.length; i++) {
            hsvList.add(hsv[i]);
        }
        return hsvList;
    }

    public void replaceFragment(int position) {
        /*The second fragment displays the same hues at varying saturation levels (100% to 0%)
        /*at a constant 100% value.
         */
        Bundle bundle = new Bundle();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (mFragmentTag[mFragmentIndex] == "Second") {
            if (saturationFragment == null)
                saturationFragment = new CustomFragment();

            h = position*HUE_RANGE;
            ArrayList<float[]> hsvList = setHSV(h, 0, 0);

            for (int i = 0; i < hsvList.size(); i++) {
                bundle.putFloatArray("color "+i, hsvList.get(i));
            }
            bundle.putString("tag", mFragmentTag[mFragmentIndex]);
            bundle.putInt("num_rows", NUM_SATURATION_ROWS);
            if (saturationFragment.getArguments() != null) {
                saturationFragment.setArguments(null);
            }

            saturationFragment.setArguments(bundle);
            ft.replace(android.R.id.content, saturationFragment, mFragmentTag[mFragmentIndex++]);
        }
        else if (mFragmentTag[mFragmentIndex] == "Third") {
            if (valuesFragment == null)
                valuesFragment = new CustomFragment();

            s = MAX_SATURATION-position*SATURATION_DIFF;
            ArrayList<float[]> hsvList = setHSV(h, s, 0);

            for (int i = 0; i < hsvList.size(); i++) {
                bundle.putFloatArray("color "+i, hsvList.get(i));
            }
            bundle.putString("tag", mFragmentTag[mFragmentIndex]);
            bundle.putInt("num_rows", NUM_VALUE_ROWS);
            if (valuesFragment.getArguments() != null)
                valuesFragment.setArguments(null);

            valuesFragment.setArguments(bundle);
            ft.replace(android.R.id.content, valuesFragment, mFragmentTag[mFragmentIndex++]);
        }
        else {
            if (namedColorsFragment == null)
                namedColorsFragment = new NamedColorsFragment();

            v = MAX_VALUE-position*VALUE_DIFF;

            float[] hsv = new float[] {h, s, v};

            bundle.putFloatArray("hsv", hsv);
            bundle.putFloat("hRange", HUE_RANGE);
            bundle.putString("tag", mFragmentTag[mFragmentIndex]);
            if (namedColorsFragment.getArguments() != null)
                namedColorsFragment.setArguments(null);

            namedColorsFragment.setArguments(bundle);
            ft.replace(android.R.id.content, namedColorsFragment, mFragmentTag[mFragmentIndex++]);
        }

        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mFragmentIndex == 2) {
            mFragmentIndex = mFragmentIndex -2;
            huesFragment.setTag(mFragmentIndex);
            mFragmentIndex++;
        }
        else if (mFragmentIndex == 3) {
            mFragmentIndex = mFragmentIndex -2;
            saturationFragment.setTag(mFragmentIndex);
            mFragmentIndex++;
        }
        else if (mFragmentIndex == 4) {
            mFragmentIndex = mFragmentIndex -2;
            valuesFragment.setTag(mFragmentIndex);
            mFragmentIndex++;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}