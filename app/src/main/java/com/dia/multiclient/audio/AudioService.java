package com.dia.multiclient.audio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dia.multiclient.R;
import com.dia.multiclient.activities.MainActivity;

/**
 * Created by Ilya on 09.09.2016.
 */
public class AudioService extends Service {
    private NotificationManager notificationManager;
    private Song song;

    public AudioService(Song song) {
        this.song = song;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new Notification(R.mipmap.ic_launcher, song.getName(),  System.currentTimeMillis());
        Intent intent1 = new Intent(this, MainActivity.class);

        return super.onStartCommand(intent, flags, startId);
    }
}
