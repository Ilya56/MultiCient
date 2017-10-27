package com.dia.multiclient.audio;

/**
 * Created by Ilya on 22.10.2016.
 */
public class EqualizerBandLevel {
    private long id;
    private int level;

    public EqualizerBandLevel(long id, int level) {
        this.id = id;
        this.level = level;
    }

    public long getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public String toString() {
        return id + " " + level;
    }
}
