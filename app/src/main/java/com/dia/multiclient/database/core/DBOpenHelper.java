package com.dia.multiclient.database.core;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;

import com.dia.multiclient.database.DBResource;

/**
 * Created by Ilya on 12.10.2016.
 */
public class DBOpenHelper {
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    protected String tableName;
    protected Activity activity;

    public DBOpenHelper(String tableName, Activity activity) {
        this.tableName = tableName;
        this.activity = activity;
    }

    public boolean isOpen() {
        return sqLiteDatabase != null && dbHelper != null && sqLiteDatabase.isOpen();
    }

    public void open(Activity activity) {
        dbHelper = new DBHelper(activity, tableName, DBResource.BD_VERSION);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    public void close() {
        sqLiteDatabase.close();
    }
}
