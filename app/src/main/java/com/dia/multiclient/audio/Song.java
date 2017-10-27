package com.dia.multiclient.audio;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by Ilya on 25.07.2016.
 */
public class Song implements Parcelable {
    private String id;
    private String name;
    private String singer;
    private int time;
    private String res;

    public Song(String id, String name, String singer, int time, String res) {
        this.id = id;
        this.name = name;
        this.singer = singer;
        this.time = time;
        this.res = res;
        //this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public String getSinger() {
        return singer;
    }

    public String getRes() {
        return res;
    }

    public int getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    /*public int getOwner() {
        return owner;
    }*/

    public String toString() {
        return id + " " + name + "  " + singer + "  " + time + "  " + res;
    }

    public static class ComparatorByName implements Comparator<Song> {
        @Override
        public int compare(Song song, Song t1) {
            return song.getName().compareTo(t1.getName());
        }
    }
    public static class ComparatorBySinger implements Comparator<Song> {
        @Override
        public int compare(Song song, Song t1) {
            return song.getSinger().compareTo(t1.getSinger());
        }
    }
    public static class ComparatorByTime implements Comparator<Song> {
        @Override
        public int compare(Song song, Song t1) {
            return song.getTime() - t1.getTime();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(singer);
        parcel.writeInt(time);
        parcel.writeString(res);
    }

    protected Song(Parcel in) {
        id = in.readString();
        name = in.readString();
        singer = in.readString();
        time = in.readInt();
        res = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
