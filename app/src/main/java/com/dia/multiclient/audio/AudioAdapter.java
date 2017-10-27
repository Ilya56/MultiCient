package com.dia.multiclient.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.audio.activities.AudioActivity;
import com.dia.multiclient.audio.fragments.PlayNowSongFragment;
import com.dia.multiclient.cores.CoreActivity;
import com.dia.multiclient.cores.CoreAdapter;

import java.util.List;

/**
 * Created by Ilya on 13.10.2016.
 */
public class AudioAdapter extends CoreAdapter<Song> {
    private int playNowIndex = -1;
    private ImageButton playNowButton;
    private boolean isPlaying;

    public AudioAdapter(List<Song> songs, Activity activity) {
        super(songs, activity);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder h;
        if (view == null) {
            h = new Holder();
            view = layoutInflater.inflate(R.layout.item_audio, viewGroup, false);

            h.name = (TextView) view.findViewById(R.id.item_name);
            h.singer = (TextView) view.findViewById(R.id.item_singer);
            h.time = (TextView) view.findViewById(R.id.time);
            h.playStop = (ImageButton) view.findViewById(R.id.item_play_stop);
            h.playStop.setTag(0);

            view.setTag(h);
        } else {
            h = (Holder) view.getTag();
        }

        final Song song = (Song) getItem(i);

        if (song != null) {
            String name = song.getName();
            String singer = song.getSinger();
            String time = G.toReadableTime(song.getTime(), false);
            h.name.setText(name);
            h.singer.setText(singer);
            h.time.setText(time);

            ImageButton button = h.playStop;

            if (i == playNowIndex && isPlaying) {
                button.setImageResource(R.drawable.ic_pause_black_36dp);
                button.setTag(1);
                playNowButton = button;
            }
            else {
                button.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                button.setTag(0);
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    int status = (int) view.getTag();
                    MediaPlayer mp = AudioActivity.getSLF().getMp();
                    ImageButton button = (ImageButton) view;

                    if (status == 0) {
                        if (playNowIndex != i) {
                            /*try {
                                if (mp != null) {
                                    mp.reset();
                                    mp.setDataSource(view.getContext(), Uri.parse(song.getRes()));
                                } else {
                                    mp = MediaPlayer.create(view.getContext(), Uri.parse(song.getRes()));
                                }
                                mp.prepare();
                            } catch (IOException | IllegalStateException e) {
                                e.printStackTrace();
                            }*/
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(song.getRes())));
                        }
                        /*if (mp != null) {
                            mp.start();
                        }*/

                        //button.setImageResource(R.drawable.ic_pause_black_36dp);
                        //button.setTag(1);

                        if (playNowButton != null && playNowIndex != i) {
                            playNowButton.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                            playNowButton.setTag(0);
                        }

                        playNowButton = button;
                        playNowIndex = i;

                        PlayNowSongFragment fragment = AudioActivity.getPNSF();
                        if (fragment != null) {
                            PlayNowSongFragment newFragment = new PlayNowSongFragment();
                            ((CoreActivity) view.getContext()).getSupportFragmentManager().beginTransaction().remove(fragment).add(R.id.container, newFragment).commit();
                            AudioActivity.setPNSF(newFragment);
                        }

                        isPlaying = true;
                    } else {
                        //mp.pause();

                        if (AudioActivity.getPNSF() != null) {
                            AudioActivity.getPNSF().setPause();
                        }

                        button.setTag(0);
                        button.setImageResource(R.drawable.ic_play_arrow_black_36dp);

                        isPlaying = false;
                    }
                }
            });
        }

        return view;
    }

    private class Holder {
        public TextView name;
        public TextView singer;
        public TextView time;
        public ImageButton playStop;
    }

    public void nextSong(MediaPlayer mp, Context context) {
        int lastSong = list.size() - 1;
        int playNext;

        if (playNowIndex == lastSong) {
            playNext = 0;
        } else {
            playNext = playNowIndex + 1;
        }

        Song song = list.get(playNext);

        playNowButton.setImageResource(R.drawable.ic_play_arrow_black_36dp);
        playNowButton.setTag(0);

        playNowButton.setImageResource(R.drawable.ic_pause_black_36dp);
        playNowButton.setTag(1);

        /*mp.stop();
        mp.reset();
        try {
            mp.setDataSource(context, Uri.parse(song.getRes()));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();*/
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(song.getRes())));

        notifyDataSetChanged();
    }

    public int getPlayNowIndex() {
        return playNowIndex;
    }

    public void setPlayNowIndex(int playNowIndex) {
        this.playNowIndex = playNowIndex;
    }

    public ImageButton getPlayNowButton() {
        return playNowButton;
    }

    public void setPlayNowButton(ImageButton playNowButton) {
        this.playNowButton = playNowButton;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public void reset() {
        playNowButton = null;
        playNowIndex = -1;
        isPlaying = false;
        notifyDataSetChanged();
    }
}
