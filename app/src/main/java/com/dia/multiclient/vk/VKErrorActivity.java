package com.dia.multiclient.vk;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreActivity;

/**
 * Created by Ilya on 12.08.2016.
 */
public class VKErrorActivity extends CoreActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vkerror);

        TextView tv = ((TextView) findViewById(R.id.mess));
        String text = getIntent().getStringExtra("from");
        assert tv != null;
        tv.setText(text);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}