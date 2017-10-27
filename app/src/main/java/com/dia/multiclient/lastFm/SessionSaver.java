package com.dia.multiclient.lastFm;

import android.content.SharedPreferences;

import com.lastfm.sdk.lastfm.Session;

/**
 * Created by Ilya on 04.01.2017.
 */
public class SessionSaver {
    private final static String API = "api_key";
    private final static String KEY = "key";
    private final static String SECRET = "secret";
    private final static String USERNAME = "username";
    private final static String SUBSCRIBERS = "subscribers";

    public static void save(Session session, SharedPreferences sharPref) {
        SharedPreferences.Editor editor = sharPref.edit();
        String api = session.getApiKey();
        String key = session.getKey();
        String secret = session.getSecret();
        String username = session.getUsername();
        boolean subscribers = session.isSubscriber();
        editor.putString(API, api);
        editor.putString(KEY, key);
        editor.putString(SECRET, secret);
        editor.putString(USERNAME, username);
        editor.putBoolean(SUBSCRIBERS, subscribers);
        editor.apply();
    }

    public static Session load(SharedPreferences sharPref) {
        String api = sharPref.getString(API, "");
        String key = sharPref.getString(KEY, "");
        String secret = sharPref.getString(SECRET, "");
        String username = sharPref.getString(USERNAME, "");
        boolean subscribers = sharPref.getBoolean(SUBSCRIBERS, false);
        return Session.createSession(api, secret, key, username, subscribers);
    }

    public static void reset(SharedPreferences sharPref) {
        SharedPreferences.Editor editor = sharPref.edit();
        editor.putString(API, "");
        editor.putString(KEY, "");
        editor.putString(SECRET, "");
        editor.putString(USERNAME, "");
        editor.putBoolean(SUBSCRIBERS, false);
        editor.apply();
    }
}
