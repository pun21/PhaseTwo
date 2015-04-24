package com.spun.phasetwo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SwatchLimitDialogFragment extends DialogFragment {
    private static final int MAX_SWATCHES = 256;
    private int limit;
    private int max;
    private TextView text;
    nNoticeDialogListener mListener;

    public interface nNoticeDialogListener {
        void onDialogPositiveClick(int swatchLimit);
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        Bundle bundle = getArguments();
        int fragment = bundle.getInt("whichfragment");
        if (fragment == 0)
            limit = bundle.getInt("saturation_swatches_limit");
        else
            limit = bundle.getInt("value_swatches_limit");

        String tag = bundle.getString("fragmentTag");
        try {
            mListener = (nNoticeDialogListener) getFragmentManager().findFragmentByTag(tag);
        } catch (ClassCastException e) {
            throw new ClassCastException("NamedColorsFragment must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.swatch_limit_dialog, null);

        text = (TextView) v.findViewById(R.id.swatch_text);

        SeekBar slider = (SeekBar) v.findViewById(R.id.slider);
        slider.setMax(MAX_SWATCHES);
        slider.setProgress(limit);
        text.setText(String.valueOf(limit));
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                limit = progress;
                text.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle("Swatch Limit")
                .setMessage("Choose the maximum number of color swatches you wish to see displayed")
                .setView(v)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        max = limit;
                        mListener.onDialogPositiveClick(max);
                        dialog.dismiss();
                    }
                })
        .create();
    }
}