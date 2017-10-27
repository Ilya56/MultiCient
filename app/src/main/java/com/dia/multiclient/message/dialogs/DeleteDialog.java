package com.dia.multiclient.message.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.dia.multiclient.R;
import com.dia.multiclient.fragments.MenuFragment;
import com.dia.multiclient.message.fragments.PreviewListFragment;

/**
 * Created by Ilya on 26.10.2016.
 */
public class DeleteDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] str = {getString(R.string.delete_history)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("").setItems(str, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((PreviewListFragment) MenuFragment.getTabs().get(1)).deleteHistory();
            }
        });

        return builder.create();
    }
}
