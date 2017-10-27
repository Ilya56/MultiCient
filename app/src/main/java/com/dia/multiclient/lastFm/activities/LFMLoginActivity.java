package com.dia.multiclient.lastFm.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.activities.MainActivity;
import com.dia.multiclient.cores.CoreActivity;
import com.dia.multiclient.lastFm.SessionSaver;
import com.lastfm.sdk.lastfm.Authenticator;
import com.lastfm.sdk.lastfm.Session;

/**
 * Created by Ilya on 03.01.2017.
 */
public class LFMLoginActivity extends CoreActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lastfm_login);

        final EditText ETName = (EditText) findViewById(R.id.lfm_username);
        final EditText ETPass = (EditText) findViewById(R.id.lfm_password);
        final TextView wrongPass = (TextView) findViewById(R.id.wrong_pass);

        Button BLogin = (Button) findViewById(R.id.lfm_login);
        assert BLogin != null;
        assert wrongPass != null;
        assert ETName != null;
        assert ETPass != null;
        BLogin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View view) {
                final String username = ETName.getText().toString();
                final String password = ETPass.getText().toString();
                final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.file_name), Context.MODE_PRIVATE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Session session = Authenticator.getMobileSession(username, password, getString(R.string.lfm_api_key), getString(R.string.lfm_api_sig));
                        G.LFMSession = session;
                        if (session != null) {
                            SessionSaver.save(G.LFMSession, sharedPref);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("lfm", true);
                            editor.commit();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.getMainFragment().getLfmLoginButton().setLogined(sharedPref);
                                    wrongPass.setText("");
                                }
                            });
                            finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    wrongPass.setText(getString(R.string.wrong_pass));
                                    ETPass.setText("");
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.login));
    }
}
