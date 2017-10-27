package com.dia.multiclient.audio.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.audio.AudioAdapter;
import com.dia.multiclient.audio.Song;
import com.dia.multiclient.audio.dialogs.SaveDialog;
import com.dia.multiclient.cores.CoreListener;
import com.dia.multiclient.database.services.SongDBService;
import com.dia.multiclient.cores.CoreFragment;
import com.lastfm.sdk.lastfm.PaginatedResult;
import com.lastfm.sdk.lastfm.Result;
import com.lastfm.sdk.lastfm.Track;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ilya on 13.10.2016.
 */
public class SongListFragment extends CoreFragment {
    private List<Song> songs;
    private MediaPlayer mp;
    private final String KEY_SONGS = "songs";
    private AudioAdapter adapter;
    private int savePosition;
    private List<Song> saveSongs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_audio_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list_songs);
        if (savedInstanceState != null) {
            songs = savedInstanceState.getParcelableArrayList(KEY_SONGS);
            saveSongs = songs;
            adapter = new AudioAdapter(songs, getActivity());
            listView.setAdapter(adapter);
        } else {
            songs = new SongDBService(getActivity()).getAll();
            if (songs == null) {
                songs = new ArrayList<>();
            }
            adapter = new AudioAdapter(songs, getActivity());
            listView.setAdapter(adapter);

            if (G.loginInLFM)
                loadAudio();
        }

        if (G.isPlaying) {
            adapter.setPlayNowButton(G.playNowButton);
            adapter.setPlayNowIndex(G.playNowIndex);
            adapter.setPlaying(G.isPlaying);
            mp = G.mediaPlayer;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int playNowIndex = adapter.getPlayNowIndex();
                Song song = songs.get(i);
                ImageButton playNowButton = adapter.getPlayNowButton();
                ImageButton button = (ImageButton) view.findViewById(R.id.item_play_stop);

                if (playNowIndex == i) {
                    /*if (AudioActivity.getPNSF() != null) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(AudioActivity.getPNSF()).commit();
                        AudioActivity.setPNSF(null);
                    } else {
                        PlayNowSongFragment pnsf = new PlayNowSongFragment();
                        if (AudioActivity.getPNSF() != null)
                            getFragmentManager().beginTransaction().remove(AudioActivity.getPNSF()).commit();
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, pnsf).commit();
                        AudioActivity.setPNSF(pnsf);
                    }*/
                } else {
                   /* try {
                        if (mp == null) {
                            mp = MediaPlayer.create(view.getContext(), Uri.parse(song.getRes()));
                            AudioActivity.getSLF().setMp(mp);
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    adapter.nextSong(mediaPlayer, getContext());
                                }
                            });
                        } else {
                            mp.reset();
                            mp.setDataSource(view.getContext(), Uri.parse(song.getRes()));
                            mp.prepare();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (mp != null)
                        mp.start();*/
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(song.getRes())));

                    if (playNowButton != null) {
                        playNowButton.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                        playNowButton.setTag(0);
                    }

                    //button.setImageResource(R.drawable.ic_pause_black_36dp);
                    //button.setTag(1);

                    adapter.setPlayNowButton(button);
                    adapter.setPlayNowIndex(i);
                    adapter.setPlaying(true);

                    /*PlayNowSongFragment pnsf = new PlayNowSongFragment();
                    if (AudioActivity.getPNSF() != null)
                        getFragmentManager().beginTransaction().remove(AudioActivity.getPNSF()).commit();
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.container, pnsf).commit();
                    AudioActivity.setPNSF(pnsf);*/
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                savePosition = position;
                new SaveDialog().show(getActivity().getFragmentManager(), "1");
                return true;
            }
        });

        /*if (mp == null) {
            try {
                mp = MediaPlayer.create(getContext(), R.raw.default_media);
                if (songs.size() > 0) {
                    mp = MediaPlayer.create(getContext(), Uri.parse(songs.get(0).getRes()));
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mp != null) {
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    adapter.nextSong(mediaPlayer, getContext());
                }
            });
        }*/

        return view;
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public void setMp(MediaPlayer mp) {
        this.mp = mp;
    }

    private void startListener(Collection<Track> result, int step) {
        new Thread(new Listener(result, step)).start();
    }

    private void loadAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PaginatedResult<Track> result = com.lastfm.sdk.lastfm.User.getLovedTracks(G.LFMSession.getUsername(), G.LFMSession.getApiKey());
                startListener(result.getPageResults(), 0);
            }
        }).start();
    }

    private class Listener extends CoreListener {
        public Listener(Collection<Track> result, int step) {
            super(result, step);
        }

        @Override
        public void run() {
            adapter.clear();
            new SongDBService(getActivity()).clear();
            for (Track track : result) {
                final Song song = new Song(track.getId(), track.getName(), track.getArtist(), track.getDuration(), track.getUrl());
                Log.d("songs", song.toString());
                if (getActivity() == null) {
                    error = true;
                    break;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.add(song);
                    }
                });
                if (step == 0)
                    new SongDBService(getActivity()).save(song);
            }

            if (!error) {
                if (step == 0) {
                    songs = adapter.getAll();
                    saveSongs = songs;
                }
            }
        }
    }

    private void sort(Comparator<Song> comparator) {
        int pni = adapter.getPlayNowIndex();
        if (pni >= 0) {
            Song pn = songs.get(pni);

            Song temp[] = new Song[songs.size()];
            temp = songs.toArray(temp);
            Arrays.sort(temp, comparator);
            songs = Arrays.asList(temp);
            adapter.set(songs);

            int newInd = songs.indexOf(pn);
            adapter.setPlayNowIndex(newInd);
        } else {
            Song temp[] = new Song[songs.size()];
            temp = songs.toArray(temp);
            Arrays.sort(temp, comparator);
            songs = Arrays.asList(temp);
            adapter.set(songs);
        }
        adapter.notifyDataSetChanged();
    }

    public void sortNames() {
        sort(new Song.ComparatorByName());
    }

    public void sortDefault() {
        int pni = adapter.getPlayNowIndex();
        if (pni >= 0) {
            Song pn = songs.get(pni);

            songs = saveSongs;
            adapter.set(songs);

            int newInd = songs.indexOf(pn);
            adapter.setPlayNowIndex(newInd);
        } else {
            songs = saveSongs;
            adapter.set(songs);
        }
        adapter.notifyDataSetChanged();
    }

    public void sortSingers() {
        sort(new Song.ComparatorBySinger());
    }

    public void sortTime() {
        sort(new Song.ComparatorByTime());
    }

    public void reloadAudio() {
        if (G.loginInLFM)
            loadAudio();
    }

    public void search(Collection<Track> result) {
        startListener(result, 1);
    }

    public void saveSong() {
        final Song song = songs.get(savePosition);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Result result = Track.love(song.getSinger(), song.getName(), G.LFMSession);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.isSuccessful()) {
                            Toast.makeText(getContext(), getString(R.string.added_to_list), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_SONGS, (ArrayList<Song>) songs);
    }

    public AudioAdapter getAdapter() {
        return adapter;
    }

    public void deleteSong() {
        Song song = songs.get(savePosition);
        Result result = Track.unlove(song.getSinger(), song.getName(), G.LFMSession);
        if (result.isSuccessful()) {
            Toast.makeText(getContext(), getString(R.string.added_to_list), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sortDefault();
        G.playNowButton = adapter.getPlayNowButton();
        G.playNowIndex = adapter.getPlayNowIndex();
        G.isPlaying = adapter.isPlaying();
        G.mediaPlayer = mp;
    }
}
