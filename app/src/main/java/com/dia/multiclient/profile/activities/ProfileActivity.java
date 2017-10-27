package com.dia.multiclient.profile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreActivity;
import com.dia.multiclient.profile.User;
import com.dia.multiclient.profile.fragments.ProfileFragment;
import com.dia.multiclient.profile.fragments.ProfileTabsFragment;

/**
 * Created by Ilya on 03.10.2016.
 */
public class ProfileActivity extends CoreActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        Intent i = getIntent();
        if (i.hasExtra(G.USER)) {
            User user = i.getParcelableExtra(G.USER);
            ProfileFragment pf = new ProfileFragment();
            Bundle b = new Bundle();
            b.putParcelable(G.USER, user);
            pf.setArguments(b);
            getSupportFragmentManager().beginTransaction().add(R.id.container, pf).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new ProfileTabsFragment()).commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

