package com.dia.multiclient.audio.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.dia.multiclient.R;
import com.dia.multiclient.audio.activities.AudioActivity;
import com.dia.multiclient.audio.fragments.SongListFragment;

/**
 * Created by Ilya on 07.09.2016.
 */
public class SortDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] sorts = {getString(R.string.names), getString(R.string.singers), getString(R.string.time), getString(R.string.default1)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choose_sort)).setItems(sorts, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SongListFragment slf = AudioActivity.getSLF();
                if (which == 0) {
                    slf.sortNames();
                }
                if (which == 1) {
                    slf.sortSingers();
                }
                if (which == 2) {
                    slf.sortTime();
                }
                if (which == 3) {
                    slf.sortDefault();
                }
            }
        });

        return builder.create();
    }
}
