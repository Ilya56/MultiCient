package com.dia.multiclient.profile.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.profile.User;
import com.dia.multiclient.profile.fragments.ProfileFragment;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

/**
 * Created by Ilya on 04.11.2016.
 */
public class AddFriendDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final User user = getArguments().getParcelable(G.USER);
        if (user != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.write_message));

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            input.setHint(getString(R.string.message));
            builder.setView(input)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    VKRequest request = VKApi.friends().add(VKParameters.from(VKApiConst.USER_ID, user.getId(), input.getText().toString()));
                    request.executeSyncWithListener(ProfileFragment.requestListener);
                }
            })
            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            return builder.create();
        }

        return null;
    }
}
