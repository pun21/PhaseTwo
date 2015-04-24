package com.spun.phasetwo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SortingOptionsDialogFragment extends DialogFragment {
    private int selectedIndex;
    private int clickedIndex = selectedIndex;
    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(int optionSelected);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        selectedIndex = bundle.getInt("index");

        try {
            mListener = (NoticeDialogListener) getFragmentManager().findFragmentByTag("Fourth");
        } catch (ClassCastException e) {
            throw new ClassCastException("NamedColorsFragment must implement NoticeDialogListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] options = {"Hue, Saturation, Value",
                                        "Hue, Value, Saturation",
                                        "Saturation, Hue, Value",
                                        "Saturation, Value, Hue",
                                        "Value, Hue, Saturation",
                                        "Value, Saturation, Hue"};

        return new AlertDialog.Builder(getActivity())
                .setTitle("Sorting Options")
                .setSingleChoiceItems(options, selectedIndex, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // dialog box
                        clickedIndex = id;
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedIndex = clickedIndex;
                        mListener.onDialogPositiveClick(selectedIndex);
                        dialog.dismiss();
                    }
                })
        .create();
    }
}