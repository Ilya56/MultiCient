package com.dia.multiclient.database.services;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

import com.dia.multiclient.audio.Song;
import com.dia.multiclient.database.DBResource;
import com.dia.multiclient.database.core.DBCoreService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 12.10.2016.
 */
public class SongDBService extends DBCoreService<Song> {
    public SongDBService(Activity activity) {
        super(DBResource.Song.TABLE_NAME, DBResource.Song.ID, activity);
    }

    @Override
    public List<Song> parseCursor(Cursor cursor) {
        List<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            do {
                String id = cursor.getString(cursor.getColumnIndex(DBResource.Song.ID));
                String name = cursor.getString(cursor.getColumnIndex(DBResource.Song.NAME));
                String singer = cursor.getString(cursor.getColumnIndex(DBResource.Song.SINGER));
                int time = cursor.getInt(cursor.getColumnIndex(DBResource.Song.TIME));
                String res = cursor.getString(cursor.getColumnIndex(DBResource.Song.RES));

                songs.add(new Song(id, name, singer, time, res));
            } while (cursor.moveToNext());

            return songs;
        }
        return null;
    }

    @Override
    public ContentValues getContentValues(Song song) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBResource.Song.NAME, song.getName());
        contentValues.put(DBResource.Song.SINGER, song.getSinger());
        contentValues.put(DBResource.Song.TIME, song.getTime());
        contentValues.put(DBResource.Song.RES, song.getRes());

        return contentValues;
    }
}
