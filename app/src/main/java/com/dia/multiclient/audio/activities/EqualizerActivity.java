package com.dia.multiclient.audio.activities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.dia.multiclient.R;
import com.dia.multiclient.audio.EqualizerBandLevel;
import com.dia.multiclient.cores.CoreActivity;
import com.dia.multiclient.database.services.EqualizerDBService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 29.07.2016.
 */
public class EqualizerActivity extends CoreActivity {
    private Equalizer equalizer;
    private boolean firstClick = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //set media player
        MediaPlayer mediaPlayer = AudioActivity.getSLF().getMp();

        if (equalizer == null) {
            if (mediaPlayer == null)
                    mediaPlayer = MediaPlayer.create(this, Uri.parse(""));
            equalizer = new Equalizer(1, mediaPlayer.getAudioSessionId());
            equalizer.setEnabled(true);
        }

        List<EqualizerBandLevel> levels = new EqualizerDBService(this).getAll();
        if (levels != null) {
            Log.d("qwe", levels.toString());
            for (int i = 0; i < levels.size(); i++) {
                equalizer.setBandLevel((short) levels.get(i).getId(), (short) (levels.get(i).getLevel()));
                Log.d("1", equalizer.getBandLevel((short) i) + "");
            }
        }

        short nBands = equalizer.getNumberOfBands();
        final int minBandLevel = equalizer.getBandLevelRange()[0];
        final int maxBandLevel = equalizer.getBandLevelRange()[1];

        //read layout
        LinearLayout LLContainer = (LinearLayout) findViewById(R.id.linear_layout);

        for (short i = 0; i < nBands; i++) {
            //show Hz
            TextView TVHeader = new TextView(this);
            TVHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            TVHeader.setGravity(Gravity.CENTER_HORIZONTAL);
            String header = (equalizer.getCenterFreq(i) / 1000) + "Hz";
            TVHeader.setText(header);
            assert LLContainer != null;
            LLContainer.addView(TVHeader);

            //create new layout for seekbars
            LinearLayout LLSeekBar = new LinearLayout(this);
            LLSeekBar.setOrientation(LinearLayout.HORIZONTAL);

            //show min level
            TextView TVminLevel = new TextView(this);
            TVminLevel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            String text = (minBandLevel / 100) + "dB";
            TVminLevel.setText(text);

            //show max level
            TextView TVmaxLevel = new TextView(this);
            TVmaxLevel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            text = (maxBandLevel / 100) + "dB";
            TVmaxLevel.setText(text);

            //create seekbar
            SeekBar seekBar = new SeekBar(this);
            seekBar.setId(i);
            LinearLayout.LayoutParams LP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LP.weight = 1;
            seekBar.setLayoutParams(LP);
            seekBar.setMax(maxBandLevel - minBandLevel);
            seekBar.setProgress(equalizer.getBandLevel(i));

            //set action for seekbar
            final short finalI = i;
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        equalizer.setBandLevel(finalI, (short) (progress + minBandLevel));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //empty
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    save(finalI, seekBar.getProgress() + minBandLevel);
                }
            });

            //add widgets to layout
            LLSeekBar.addView(TVminLevel);
            LLSeekBar.addView(seekBar);
            LLSeekBar.addView(TVmaxLevel);

            LLContainer.addView(LLSeekBar);
        }

        //create adapter
        ArrayList<String> presetName = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item , presetName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.preset_spinner);

        //set preset names
        for(short i = 0; i < equalizer.getNumberOfPresets(); i++) {
            presetName.add(equalizer.getPresetName(i));
        }

        //set adapter
        assert spinner != null;
        spinner.setAdapter(adapter);

        //create action for click spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstClick)
                    firstClick = false;
                else {
                    equalizer.usePreset((short) position);

                    short nBands = equalizer.getNumberOfBands();
                    final short minLevel = equalizer.getBandLevelRange()[0];

                    for (short i = 0; i < nBands; i++) {
                        SeekBar seekBar = (SeekBar) findViewById(i);
                        assert seekBar != null;
                        seekBar.setProgress(equalizer.getBandLevel(i) - minLevel);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //empty
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Equalizer");
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

    public Equalizer getEqualizer() {
        return equalizer;
    }

    public void setEqualizer(Equalizer equalizer) {
        this.equalizer = equalizer;
    }

    private boolean save(int i, int progress) {
        EqualizerBandLevel ebl = new EqualizerBandLevel(i, progress);
        return new EqualizerDBService(this).update(ebl);
    }
}
