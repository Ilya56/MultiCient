package com.dia.multiclient.audio.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.audio.AudioAdapter;
import com.dia.multiclient.audio.Song;
import com.dia.multiclient.audio.activities.AudioActivity;
import com.dia.multiclient.cores.CoreFragment;

import java.util.List;

/**
 * Created by Ilya on 13.10.2016.
 */
public class PlayNowSongFragment extends CoreFragment {
    private int timeNow = 0;
    private TextView TVTimeNow;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private int playNext;
    private ImageButton playStop;
    private Song song;
    private List<Song> songs;
    private TextView prevSong;
    private TextView nextSong;
    private String thisName;
    private AudioAdapter adapter;
    private SongListFragment slf;
    private MediaPlayer mp;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_play_now, container, false);

        ImageButton prev = (ImageButton) view.findViewById(R.id.prev);
        playStop = (ImageButton) view.findViewById(R.id.play_stop);
        ImageButton next = (ImageButton) view.findViewById(R.id.next);
        seekBar = (SeekBar) view.findViewById(R.id.time_seek_bar);
        final TextView name = (TextView) view.findViewById(R.id.name);
        final TextView singer = (TextView) view.findViewById(R.id.singer);
        TVTimeNow = (TextView) view.findViewById(R.id.time_now);
        final TextView time_full = (TextView) view.findViewById(R.id.time_full);
        prevSong = (TextView) view.findViewById(R.id.prev_name);
        nextSong = (TextView) view.findViewById(R.id.next_name);

        slf = AudioActivity.getSLF();
        adapter = slf.getAdapter();
        mp = slf.getMp();

        ImageButton button = adapter.getPlayNowButton();
        songs = adapter.getAll();
        song = songs.get(adapter.getPlayNowIndex());

        thisName = (String) getActivity().getTitle();
        getActivity().setTitle(song.getName());

        //set action for play-stop button
        if ((int)button.getTag() == 1) {
            playStop.setTag(1);
            playStop.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
        } else {
            playStop.setTag(0);
            playStop.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        }
        playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int temp = (int) playStop.getTag();

                if (temp == 0) {
                    /*try {
                        if (mp != null) {
                            //mp.prepare();
                        } else {
                            mp = MediaPlayer.create(getActivity(), Uri.parse(song.getRes()));
                            mp.prepare();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.start();*/
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(song.getRes())));

                    //start equalizer

                    //set this button like pressed
                    ImageButton thisButton  = adapter.getPlayNowButton();
                    thisButton.setImageResource(R.drawable.ic_pause_black_36dp);
                    thisButton.setTag(1);

                    playStop.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
                    playStop.setTag(1);

                    adapter.setPlaying(true);
                } else {
                    //mp.pause();

                    ImageButton thisButton = adapter.getPlayNowButton();;
                    thisButton.setTag(0);
                    thisButton.setImageResource(R.drawable.ic_play_arrow_black_36dp);

                    playStop.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
                    playStop.setTag(0);
                    adapter.setPlaying(false);
                }
            }
        });

        name.setText(song.getName());
        singer.setText(song.getSinger());
        time_full.setText(G.toReadableTime(song.getTime(), false));
        TVTimeNow.setText(G.toReadableTime(timeNow, true));

        seekBar.setMax(song.getTime() * 1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    slf.getMp().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastSong = songs.size() - 1;
                ImageButton buttonPlayNow = adapter.getPlayNowButton();
                int playNow = adapter.getPlayNowIndex();

                if (playNow == lastSong) {
                    playNext = 0;
                } else {
                    playNext = playNow + 1;
                }

                changeSong(songs, buttonPlayNow, name, singer, time_full, mp);
                playStop.setTag(1);
                playStop.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            }
        });

        //set action fon prev button
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton buttonPlayNow = adapter.getPlayNowButton();
                int playNow = adapter.getPlayNowIndex();

                if (playNow == 0) {
                    return;
                } else {
                    playNext = playNow - 1;
                }

                changeSong(songs, buttonPlayNow, name, singer, time_full, mp);
                playStop.setTag(1);
                playStop.setImageResource(R.drawable.ic_pause_circle_outline_black_48dp);
            }
        });

        updateTime.run();

        setNextPrevName();

        return view;
    }

    private Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            timeNow = mp.getCurrentPosition() - 1;
            TVTimeNow.setText(G.toReadableTime(timeNow, true));
            seekBar.setProgress(timeNow);
            handler.postDelayed(this, 100);
        }
    };

    @Override
    public void onBack() {
        if (getActivity() != null)
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public void setPause() {
        playStop.setImageResource(R.drawable.ic_play_circle_outline_black_48dp);
        playStop.setTag(0);
    }

    private void changeSong(final List<Song> songs, ImageButton buttonPlayNow, final TextView name, final TextView singer, final TextView time_full, MediaPlayer mediaPlayer) {
        song = songs.get(playNext);

        buttonPlayNow.setImageResource(R.drawable.ic_play_arrow_black_36dp);
        buttonPlayNow.setTag(0);

        buttonPlayNow = adapter.getPlayNowButton();
        buttonPlayNow.setImageResource(R.drawable.ic_pause_black_36dp);
        buttonPlayNow.setTag(1);

       /* mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(getActivity(), Uri.parse(song.getRes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();*/
        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(song.getRes())));

        adapter.setPlayNowIndex(playNext);
        adapter.setPlayNowButton(buttonPlayNow);

        name.setText(song.getName());
        singer.setText(song.getSinger());
        time_full.setText(G.toReadableTime(song.getTime(), false));
        TVTimeNow.setText(G.toReadableTime(timeNow, true));
        seekBar.setMax(song.getTime() * 1000);

        setNextPrevName();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setNextPrevName() {
        int n = 20;
        if (adapter.getPlayNowIndex() > 0) {
            Song song = songs.get(adapter.getPlayNowIndex() - 1);
            String name = song.getName();
            String singer = song.getSinger();
            String tempStr = name.substring(0, Math.min(n, name.length())) + (n < name.length() ? "..." : "" ) + "\n" + singer.substring(0, Math.min(n, singer.length())) + (n < name.length() ? "..." : "" );
            prevSong.setText(tempStr);
        } else {
            String tempName = "";
            prevSong.setText(tempName);
        }

        if (adapter.getPlayNowIndex() < songs.size() - 1) {
            Song song = songs.get(adapter.getPlayNowIndex() + 1);
            String name = song.getName();
            String singer = song.getSinger();
            String tempStr = name.substring(0, Math.min(n, name.length())) + (n < name.length() ? "..." : "" ) + "\n" + singer.substring(0, Math.min(n, singer.length())) + (n < name.length() ? "..." : "" );
            nextSong.setText(tempStr);
        } else {
            String tempName = "";
            nextSong.setText(tempName);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().setTitle(thisName);
    }
}
