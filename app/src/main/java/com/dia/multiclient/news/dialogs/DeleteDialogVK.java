package com.dia.multiclient.news.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.dia.multiclient.R;
import com.dia.multiclient.fragments.MenuFragment;
import com.dia.multiclient.news.fragments.NewsFragment;

/**
 * Created by Ilya on 01.11.2016.
 */
public class DeleteDialogVK extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] str = {getString(R.string.delete_post)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("").setItems(str, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((NewsFragment) MenuFragment.getTabs().get(0)).deletePost();
            }
        });

        return builder.create();
    }
}
