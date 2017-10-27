package com.dia.multiclient.audio.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.audio.fragments.PlayNowSongFragment;
import com.dia.multiclient.cores.CoreActivity;
import com.dia.multiclient.audio.dialogs.SortDialog;
import com.dia.multiclient.audio.fragments.SongListFragment;
import com.lastfm.sdk.lastfm.Track;

import java.util.Collection;

/**
 * Created by Ilya on 13.10.2016.
 */
public class AudioActivity extends CoreActivity {
    private static SongListFragment SLF;
    private static PlayNowSongFragment PNSF;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);
        setTitle(getString(R.string.audio));
        SLF = new SongListFragment();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, SLF).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_audio, menu);
        getSupportActionBar().show();
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    newText = newText.replaceAll(" ", "");
                    if (G.loginInLFM && newText != "") {
                        final String finalNewText = newText;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final Collection<Track> result = Track.search(finalNewText, G.LFMSession.getApiKey());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SLF.search(result);
                                    }
                                });
                            }
                        }).start();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };

            SearchView.OnCloseListener onCloseListener = new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    close();
                    return true;
                }
            };

            searchView.setOnQueryTextListener(queryTextListener);
            searchView.setOnCloseListener(onCloseListener);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.menu_equalizer :
            //    startActivity(new Intent(this, EqualizerActivity.class));
            //    return true;
            case R.id.menu_sort :
                new SortDialog().show(getFragmentManager(), "0");
                return true;
            case R.id.menu_reload :
                SLF.reloadAudio();
                return true;
            case android.R.id.home :
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static SongListFragment getSLF() {
        return SLF;
    }

    public static PlayNowSongFragment getPNSF() {
        return PNSF;
    }

    public static void setPNSF(PlayNowSongFragment PNSF) {
        AudioActivity.PNSF = PNSF;
    }

    private void close() {
        SLF.sortDefault();
        if (!searchView.isIconified())
            searchView.setIconified(true);
    }
}
