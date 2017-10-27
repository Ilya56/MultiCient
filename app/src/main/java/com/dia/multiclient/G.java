package com.dia.multiclient;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dia.multiclient.news.News;
import com.lastfm.sdk.lastfm.Session;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Ilya on 30.07.2016.
 */
public class G {
    //audio
    public static MediaPlayer mediaPlayer;
    public static ImageButton playNowButton;
    public static int playNowIndex = 1;
    public static boolean isPlaying;
    public static Session LFMSession;

    //saves
    public static List<News> news;

    //login
    public static boolean loginInVK;
    public static boolean loginInFB;
    public static boolean loginInLFM;

    public static String toReadableTime(int time, boolean m) {
        String result = "";
        if (m)
            time = time / 1000;
        int minutes = time / 60;
        int seconds = time % 60;
        if (minutes < 10) {
            result += "0";
        }
        result += String.valueOf(minutes);
        result += ":";
        if (seconds < 10) {
            result += "0";
        }
        result += String.valueOf(seconds);
        return result;
    }

    public static String toRealTime(long time) {
        Date date = new Date(time * 1000);
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        return df.format(date);
    }

    //const
    public static final int VK = 0;
    public static final int FB = 1;
    public static final String USER = "user";
    public static final String FROM = "from";
    public static final int COUNT = 2;

    public static void showError(Context context) {
        Toast.makeText(context, context.getString(R.string.check_network), Toast.LENGTH_SHORT).show();
        Log.d("error", context.getPackageName());
    }
}
