package com.dia.multiclient.database.core;

import android.app.Activity;
import android.database.Cursor;

import java.util.List;

/**
 * Created by Ilya on 22.10.2016.
 */
public abstract class DBCoreService<T> extends DBOpenHelper implements DBService<T> {
    protected String id;

    public DBCoreService(String tableName, String id, Activity activity) {
        super(tableName, activity);
        this.id = id;
    }

    @Override
    public boolean save(T t) {
        try {
            if (!isOpen())
                open(activity);
            return getSqLiteDatabase().insert(tableName, null, getContentValues(t)) != -1;
        } finally {
            if (isOpen())
                close();
        }
    }

    @Override
    public List<T> getAll() {
        try {
            if (!isOpen())
                open(activity);
            Cursor cursor = getSqLiteDatabase().rawQuery("select * from " + tableName, null);
            return parseCursor(cursor);
        } finally {
            if (isOpen())
                close();
        }
    }

    @Override
    public boolean remove(int i) {
        try {
            if (!isOpen())
                open(activity);
            return getSqLiteDatabase().delete(tableName, id + " = " + i, null) != 0;
        } finally {
            if (isOpen())
                close();
        }
    }

    @Override
    public boolean clear() {
        try {
            if (!isOpen())
                open(activity);
            return getSqLiteDatabase().delete(tableName, "1", null) != 0;
        } finally {
            if (isOpen())
                close();
        }
    }
}
