package com.dia.multiclient.database.services;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

import com.dia.multiclient.audio.EqualizerBandLevel;
import com.dia.multiclient.database.DBResource;
import com.dia.multiclient.database.core.DBCoreService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 21.10.2016.
 */
public class EqualizerDBService extends DBCoreService<EqualizerBandLevel> {

    public EqualizerDBService(Activity activity) {
        super(DBResource.Equalizer.TABLE_NAME, DBResource.Equalizer.ID, activity);
    }

    @Override
    public List<EqualizerBandLevel> parseCursor(Cursor cursor) {
        List<EqualizerBandLevel> levels = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(DBResource.Equalizer.ID));
                int level = cursor.getInt(cursor.getColumnIndex(DBResource.Equalizer.BAND_LEVEL));
                EqualizerBandLevel ebl = new EqualizerBandLevel(id, level);

                levels.add(ebl);
            } while (cursor.moveToNext());

            return levels;
        }
        return null;
    }

    @Override
    public ContentValues getContentValues(EqualizerBandLevel equalizerBandLevel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBResource.Equalizer.ID, equalizerBandLevel.getId());
        contentValues.put(DBResource.Equalizer.BAND_LEVEL, equalizerBandLevel.getLevel());

        return contentValues;
    }

    public boolean update(EqualizerBandLevel equalizerBandLevel) {
        try {
            if (!isOpen())
                open(activity);
            return getSqLiteDatabase().update(tableName, getContentValues(equalizerBandLevel), id + " = ?", new String[] {"" + equalizerBandLevel.getId()}) > 0;
        } finally {
            if (isOpen())
                close();
        }
    }
}
