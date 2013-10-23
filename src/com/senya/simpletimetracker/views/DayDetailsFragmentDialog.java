package com.senya.simpletimetracker.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by sergeykaplun on 10/17/13.
 */
public class DayDetailsFragmentDialog extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle("Day details").create();
    }
}
