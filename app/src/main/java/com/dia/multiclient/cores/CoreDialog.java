package com.dia.multiclient.cores;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Ilya on 26.10.2016.
 */
public class CoreDialog extends DialogFragment {
    private String title;
    private String[] list;
    private DialogInterface.OnClickListener listener;

    /*public CoreDialog(String title, String[] list, DialogInterface.OnClickListener listener) {
        this.title = title;
        this.list = list;
        this.listener = listener;
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setItems(list, listener);
        return builder.create();
    }
}
