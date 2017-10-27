package com.dia.multiclient.database.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dia.multiclient.database.DBResource;

/**
 * Created by Ilya on 12.10.2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBResource.Song.CREATE_TABLE);
        sqLiteDatabase.execSQL(DBResource.MessagePreview.CREATE_TABLE);
        sqLiteDatabase.execSQL(DBResource.Equalizer.CREATE_TABLE);
        sqLiteDatabase.execSQL(DBResource.Friends.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }
}
