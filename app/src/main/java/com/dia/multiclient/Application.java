package com.dia.multiclient;

import android.content.Intent;
import android.widget.Toast;

import com.dia.multiclient.vk.VKErrorActivity;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Created by Ilya on 04.08.2016.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //vk
        new VKAccessTokenTracker() {
            @Override
            public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
                if (newToken == null) {
                    Toast.makeText(Application.this, "AccessToken invalidated", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Application.this, VKErrorActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("from", "application");
                    startActivity(intent);
                }
            }
        }.startTracking();
        VKSdk.initialize(this);
        //fb
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
    }
}
