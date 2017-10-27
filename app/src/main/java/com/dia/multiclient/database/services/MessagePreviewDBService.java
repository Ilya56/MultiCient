package com.dia.multiclient.database.services;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dia.multiclient.database.DBResource;
import com.dia.multiclient.database.core.DBCoreService;
import com.dia.multiclient.message.MessagePreview;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 25.10.2016.
 */
public class MessagePreviewDBService extends DBCoreService<MessagePreview> {
    public MessagePreviewDBService(Activity activity) {
        super(DBResource.MessagePreview.TABLE_NAME, DBResource.MessagePreview.ID, activity);
    }

    @Override
    public List<MessagePreview> parseCursor(Cursor cursor) {
        List<MessagePreview> mess = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(DBResource.MessagePreview.ID));
                byte[] temp = cursor.getBlob(cursor.getColumnIndex(DBResource.MessagePreview.IMAGE));
                Bitmap image = BitmapFactory.decodeByteArray(temp, 0, temp.length);
                String fname = cursor.getString(cursor.getColumnIndex(DBResource.MessagePreview.FNAME));
                String lname = cursor.getString(cursor.getColumnIndex(DBResource.MessagePreview.LNAME));
                String lmess = cursor.getString(cursor.getColumnIndex(DBResource.MessagePreview.LMESS));
                long ltime = cursor.getLong(cursor.getColumnIndex(DBResource.MessagePreview.LTIME));
                boolean online = (cursor.getInt(cursor.getColumnIndex(DBResource.MessagePreview.ONLINE)) == 1);
                String userId = cursor.getString(cursor.getColumnIndex(DBResource.MessagePreview.USER_ID));
                boolean read = (cursor.getInt(cursor.getColumnIndex(DBResource.MessagePreview.READ_STATE)) == 1);

                mess.add(new MessagePreview(image, fname, lname, lmess, ltime, online, userId, read));
            } while (cursor.moveToNext());

            return mess;
        }
        return null;
    }

    @Override
    public ContentValues getContentValues(MessagePreview messagePreview) {
        ContentValues contentValues = new ContentValues();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        messagePreview.getImage().compress(Bitmap.CompressFormat.PNG, 100, out);

        contentValues.put(DBResource.MessagePreview.IMAGE, out.toByteArray());
        contentValues.put(DBResource.MessagePreview.FNAME, messagePreview.getFname());
        contentValues.put(DBResource.MessagePreview.LNAME, messagePreview.getLname());
        contentValues.put(DBResource.MessagePreview.LMESS, messagePreview.getLastMess());
        contentValues.put(DBResource.MessagePreview.LTIME, messagePreview.getLastTime());
        contentValues.put(DBResource.MessagePreview.ONLINE, messagePreview.isOnline() ? 1 : 0);
        contentValues.put(DBResource.MessagePreview.USER_ID, messagePreview.getUserID());
        contentValues.put(DBResource.MessagePreview.ONLINE, messagePreview.isRead() ? 1 : 0);

        return contentValues;
    }
}
