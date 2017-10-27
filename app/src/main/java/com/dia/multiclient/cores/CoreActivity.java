package com.dia.multiclient.cores;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ilya on 25.07.2016.
 */
public class CoreActivity extends AppCompatActivity {
    private OnBackPressed onBackPressed;

    public void setOnBackPressed(OnBackPressed onBackPressed) {
        this.onBackPressed = onBackPressed;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
