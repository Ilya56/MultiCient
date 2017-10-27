package com.dia.multiclient.cores;

import com.lastfm.sdk.lastfm.Track;
import com.facebook.GraphResponse;
import com.vk.sdk.api.VKResponse;

import java.util.Collection;

/**
 * Created by Ilya on 21.10.2016.
 */
public abstract class CoreListener implements Runnable {
    protected VKResponse vkResponse;
    protected GraphResponse fbResponse;
    protected Collection<Track> result;
    protected int step;
    protected boolean add;
    protected boolean error = false;

    public CoreListener(VKResponse vkResponse, int step, boolean add) {
        this.vkResponse = vkResponse;
        this.step = step;
        this.add = add;
    }

    public CoreListener(GraphResponse fbResponse, int step, boolean add) {
        this.fbResponse = fbResponse;
        this.step = step;
        this.add = add;
    }

    public CoreListener(Collection<Track> result, int step, boolean add) {
        this.result = result;
        this.step = step;
        this.add = add;
    }

    public CoreListener(VKResponse vkResponse, int step) {
        this.vkResponse = vkResponse;
        this.step = step;
    }

    public CoreListener(GraphResponse fbResponse, int step) {
        this.fbResponse = fbResponse;
        this.step = step;
    }

    public CoreListener(Collection<Track> result, int step) {
        this.result = result;
        this.step = step;
    }
}
