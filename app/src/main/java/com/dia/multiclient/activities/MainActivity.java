package com.dia.multiclient.activities;

import android.os.Bundle;

import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreActivity;
import com.dia.multiclient.G;
import com.dia.multiclient.fragments.MainFragment;

public class MainActivity extends CoreActivity {
    private static MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainFragment = new MainFragment();
        getFragmentManager().beginTransaction().add(R.id.container, mainFragment).commit();

        G.loginInVK = false;
        G.loginInFB = false;
        G.loginInLFM = false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static MainFragment getMainFragment() {
        return mainFragment;
    }
}
