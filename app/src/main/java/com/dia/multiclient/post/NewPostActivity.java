package com.dia.multiclient.post;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import static android.view.View.*;

/**
 * Created by Ilya on 26.09.2016.
 */
public class NewPostActivity extends CoreActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText ETText = (EditText) findViewById(R.id.post_text);

        final CheckBox vk = (CheckBox) findViewById(R.id.cb_vk);
        final CheckBox fb = (CheckBox) findViewById(R.id.cb_fb);
        final CheckBox fr_only = (CheckBox) findViewById(R.id.cb_fr_only);

        if (!G.loginInVK) {
            assert vk != null;
            vk.setChecked(false);
            vk.setEnabled(false);
            vk.setAlpha((float)0.5);
        }

        if (!G.loginInFB) {
            assert fb != null;
            fb.setChecked(false);
            fb.setEnabled(false);
            fb.setAlpha((float)0.5);
        }

        Button BPost = (Button) findViewById(R.id.post_new);
        assert BPost != null;
        BPost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("qwe", "Clicked");
                assert ETText != null;
                final String text = ETText.getText().toString();
                final boolean[] okey = {false, false};
                assert vk != null;
                assert fb != null;

                if ((!G.loginInVK && !fb.isChecked()) || (!G.loginInFB && !vk.isChecked()) || (!vk.isChecked() && !fb.isChecked())) {
                    Toast.makeText(NewPostActivity.this, getString(R.string.choose_some), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (vk.isChecked()) {
                    Log.d("qwe", "In vk");
                    VKApi.users().get().executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            int id = 0;
                            try {
                                id = response.json.getJSONArray("response").getJSONObject(0).getInt("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            assert fr_only != null;
                            VKRequest post = VKApi.wall().post(VKParameters.from(VKApiConst.OWNER_ID, id,
                                    VKApiConst.FRIENDS_ONLY, (fr_only.isChecked() ? "1" : "0"), VKApiConst.MESSAGE, text));
                            post.executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    okey[0] = true;
                                    if (okey[0] || okey[1]) {
                                        Toast.makeText(NewPostActivity.this, getString(R.string.posted), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }

                                @Override
                                public void onError(VKError error) {
                                    Toast.makeText(NewPostActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                if (fb.isChecked()) {
                    ShareApi share = new ShareApi(new ShareLinkContent.Builder().setContentTitle("Title").build());
                    share.setMessage(text);
                    share.share(new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                            okey[1] = true;
                            if (okey[0] || okey[1]) {
                                Toast.makeText(NewPostActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancel() {}

                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(NewPostActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
