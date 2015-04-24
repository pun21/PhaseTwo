package com.spun.phasetwo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.SeekBar;

public class SwatchLimitDialogFragment extends DialogFragment {
    private static final int NUMBER_OF_COLORS = 1178;

    public

        Bundle args = new Bundle();
    public SwatchLimitDialogFragment() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        final SeekBar slider = new SeekBar(getActivity());
        slider.setMax(NUMBER_OF_COLORS);
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }
}