package com.dia.multiclient.lastFm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.lastFm.activities.LFMLoginActivity;

/**
 * Created by Ilya on 03.01.2017.
 */
public class LFMLoginButton extends Button {
    public LFMLoginButton(Context context) {
        super(context);
        create();
    }

    public LFMLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        create();
    }

    public LFMLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create();
    }

    @SuppressLint("NewApi")
    private void create() {
        setBackground(getResources().getDrawable(R.drawable.lfm_button));
        setMaxWidth(70);
        setMaxHeight(70);
        setPadding(0, 0, 0, 0);
        setLayoutParams(new LinearLayout.LayoutParams(70, 70));

        final SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.file_name), Context.MODE_PRIVATE);
        boolean LFMLogined = sharedPref.getBoolean("lfm", false);
        if (LFMLogined) {
            setLogined(sharedPref);
        } else {
            G.loginInLFM = false;
            setText(getResources().getText(R.string.log_in_lfm));
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    getContext().startActivity(new Intent(getContext(), LFMLoginActivity.class));
                }
            });
        }
    }

    public void setLogined(final SharedPreferences sharedPref) {
        G.loginInLFM = true;
        setText(getResources().getText(R.string.log_out));
        G.LFMSession = SessionSaver.load(sharedPref);
        Log.d("qwe", G.LFMSession.toString());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                G.loginInLFM = false;
                setText(getResources().getText(R.string.log_in_lfm));
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("lfm", false);
                editor.apply();
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getContext().startActivity(new Intent(getContext(), LFMLoginActivity.class));
                    }
                });
                SessionSaver.reset(sharedPref);
            }
        });
    }
}
