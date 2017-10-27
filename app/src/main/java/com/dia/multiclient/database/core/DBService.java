package com.dia.multiclient.database.core;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

/**
 * Created by Ilya on 12.10.2016.
 */
public interface DBService<T> {
    boolean save(T t);
    List<T> getAll();
    boolean remove(int i);
    boolean clear();
    List<T> parseCursor(Cursor cursor);
    ContentValues getContentValues(T t);
}
