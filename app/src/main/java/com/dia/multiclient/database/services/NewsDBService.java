package com.dia.multiclient.database.services;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

import com.dia.multiclient.database.DBResource;
import com.dia.multiclient.database.core.DBCoreService;
import com.dia.multiclient.news.News;

import java.util.List;

/**
 * Created by Ilya on 01.11.2016.
 */
public class NewsDBService extends DBCoreService<News> {
    public NewsDBService(Activity activity) {
        super(DBResource.News.TABLE_NAME, DBResource.News.ID, activity);
    }

    @Override
    public List<News> parseCursor(Cursor cursor) {
        return null;
    }

    @Override
    public ContentValues getContentValues(News news) {
        return null;
    }
}
