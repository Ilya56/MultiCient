package com.dia.multiclient.database.services;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dia.multiclient.database.DBResource;
import com.dia.multiclient.database.core.DBCoreService;
import com.dia.multiclient.profile.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 30.10.2016.
 */
public class FriendsDBService extends DBCoreService<User> {
    public FriendsDBService(Activity activity) {
        super(DBResource.Friends.TABLE_NAME, DBResource.Friends.ID, activity);
    }

    @Override
    public List<User> parseCursor(Cursor cursor) {
        List<User> users = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(DBResource.Friends.ID));
                int from = cursor.getInt(cursor.getColumnIndex(DBResource.Friends.FROM));
                String fname = cursor.getString(cursor.getColumnIndex(DBResource.Friends.FIRST_NAME));
                String lname = cursor.getString(cursor.getColumnIndex(DBResource.Friends.LAST_NAME));
                byte[] temp = cursor.getBlob(cursor.getColumnIndex(DBResource.Friends.IMAGE));
                Bitmap image = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                boolean online = (cursor.getInt(cursor.getColumnIndex(DBResource.Friends.ONLINE)) == 1);

                users.add(new User(activity, from, id, fname, lname, image, online));
            } while (cursor.moveToNext());

            return users;
        }
        return null;
    }

    @Override
    public ContentValues getContentValues(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBResource.Friends.ID, user.getId());
        contentValues.put(DBResource.Friends.FROM, user.getFrom());
        contentValues.put(DBResource.Friends.FIRST_NAME, user.getFirstName());
        contentValues.put(DBResource.Friends.LAST_NAME, user.getLastName());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        user.getImage().compress(Bitmap.CompressFormat.PNG, 100, out);
        contentValues.put(DBResource.Friends.IMAGE, out.toByteArray());
        contentValues.put(DBResource.Friends.ONLINE, user.isOnline() ? 1 : 0);

        return contentValues;
    }
}
