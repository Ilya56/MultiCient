package com.dia.multiclient.message.listener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dia.multiclient.G;
import com.dia.multiclient.cores.CoreListener;
import com.dia.multiclient.fragments.MenuFragment;
import com.dia.multiclient.message.MessagePreview;
import com.dia.multiclient.message.MessagePreviewAdapter;
import com.dia.multiclient.message.fragments.PreviewListFragment;
import com.dia.multiclient.profile.User;
import com.dia.multiclient.vk.VKCustom;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 11.11.2016.
 */
public class LongPollServer extends CoreListener {
    private Activity activity;
    private int ts;
    private String key;
    private String server;

    public LongPollServer(VKResponse vkResponse, int step, Activity activity) {
        super(vkResponse, step);
        this.activity = activity;
    }

    @Override
    public void run() {
        if (step == 0) {
            try {
                JSONObject response = vkResponse.json;
                JSONObject response1 = response.getJSONObject("response");
                key = response1.getString("key");
                server = response1.getString("server");
                ts = response1.getInt("ts");
                int pts = -1;
                if (response1.has("pts")) {
                    pts = response1.getInt("pts");
                }

                Log.d("lps", "in get server");

                getLongPollServer(ts, server, key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (step == 1) {
            List<User> lProfiles = new ArrayList<>();
            try {
                Log.d("lps", "start");
                JSONObject response = vkResponse.json;

                JSONObject response1 = response.getJSONObject("response");
                JSONObject messages = response1.getJSONObject("messages");
                JSONArray profiles = response1.getJSONArray("profiles");
                for(int i = 0; i < profiles.length(); i++) {
                    JSONObject profile = profiles.getJSONObject(i);
                    String id = profile.getString("id");
                    String firstName = profile.getString("first_name");
                    String lastName = profile.getString("last_name");
                    boolean online = profile.getBoolean("online");
                    URL url = new URL(profile.getString("photo_50"));
                    Bitmap photo = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    User u = new User(activity, G.VK, id, firstName, lastName, photo, online);
                    lProfiles.add(u);
                }

                Log.d("lps", "profiles");

                JSONArray items = messages.getJSONArray("items");
                Log.d("lps", items.toString());
                for(int i = 0; i < items.length(); i++) {
                    JSONObject mess = items.getJSONObject(i);
                    VKApiMessage model = new VKApiMessage().parse(mess);
                    User u = null;
                    Log.d("lps", "1");
                    for(int j = 0; j < lProfiles.size(); j++) {
                        if (lProfiles.get(j).getId() == "" + model.user_id) {
                            u = lProfiles.get(j);
                            break;
                        }
                    }
                    Log.d("lps", "2");
                    MessagePreview message = new MessagePreview(u.getImage(), u.getFirstName(), u.getLastName(), model.body, model.date, u.isOnline(), u.getId(), model.read_state);
                    PreviewListFragment plf = (PreviewListFragment) MenuFragment.getTabs().get(1);
                    MessagePreviewAdapter adapter = plf.getAdapter();
                    MessagePreview messOld = null;
                    Log.d("lps", "3");
                    for(int j = 0; j < adapter.getCount(); j++) {
                        if (((MessagePreview)adapter.getItem(j)).getUserID() == "" + model.user_id) {
                            messOld = (MessagePreview)adapter.getItem(j);
                            break;
                        }
                    }
                    Log.d("lps", "4");
                    adapter.replace(messOld, message);
                }

                int pts = response1.getInt("new_pts");
                getLongPollServer(ts, server, key);

                Log.d("lps", "end");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getLongPollServer(int ts, String server, String key) {
        Log.d("lps", "in call");
        VKRequest request = VKCustom.prepareRequestLPS(server, VKParameters.from("act", "a_check", "key", key, "ts", ts, "wait", 25, "mode", 32, "version", 1));
        request.executeWithListenerLPS(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    JSONObject response1 = response.json;
                    String pts = response1.getString("pts");
                    int ts = response1.getInt("ts");
                    Log.d("qwe", "in response1");
                    VKRequest request = new VKCustom("messages").customRequest("getLongPollHistory", VKParameters.from("ts", ts, "pts", pts, "fields", "photo_50, online"));
                    request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            new Thread(new LongPollServer(response, 1, activity)).start();
                            Log.d("response_vk", response.json.toString());
                            Log.d("qwe", "in response2");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("response_vk", response.json.toString());
            }

            @Override
            public void onError(VKError error) {
                G.showError(activity);
            }
        });
        Log.d("request_vk", request.toString());
    }
}
