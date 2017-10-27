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
 * Created by Ilya on 08.11.2016.
 */
public class FilterDialog extends DialogFragment {
    private boolean[] ch;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] str = {getString(R.string.VK), getString(R.string.facebook)};

        final NewsFragment nf = (NewsFragment) MenuFragment.getTabs().get(0);
        ch = nf.getFilter();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choose_filters))
                .setMultiChoiceItems(str, ch, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                ch[i] = b;
                nf.setFilter(ch);
            }
        }).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }
}
