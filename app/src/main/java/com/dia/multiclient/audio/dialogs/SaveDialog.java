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
 * Created by Ilya on 19.09.2016.
 */
public class SaveDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] sorts = {getString(R.string.add_to_playlist), getString(R.string.remove)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("").setItems(sorts, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SongListFragment slf = AudioActivity.getSLF();
                if (which == 0) {
                    slf.saveSong();
                }
                if (which == 1) {
                    slf.deleteSong();
                }
            }
        });

        return builder.create();
    }
}
