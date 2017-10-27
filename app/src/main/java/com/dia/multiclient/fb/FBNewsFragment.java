package com.dia.multiclient.fb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreFragment;
import com.dia.multiclient.news.News;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 20.09.2016.
 */
public class FBNewsFragment extends CoreFragment {
    private CallbackManager callbackManager;
    private List<News> news;
    private TextView LogV;
    private TempFeed tf;
    private String res = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_test_fb, container, false);
        LogV = (TextView) view.findViewById(R.id.log);

        news = new ArrayList<>();

        FacebookSdk.sdkInitialize(this.getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        Bundle params = new Bundle();
        params.putString("fields", "id, first_name, last_name, picture");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "me/taggable_friends",
                params,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        LogV.setText(response.toString());
                    }
                }
        ).executeAsync();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class Listener implements Runnable {
        GraphResponse response;
        int step;

        public Listener(GraphResponse response, int step) {
            this.response = response;
            this.step = step;
        }

        @Override
        public void run() {
            if (step == 0) {
                try {
                    JSONObject o1 = response.getJSONObject();
                    JSONArray data = o1.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject feed = data.getJSONObject(i);
                        String id = feed.getString("id");
                        JSONObject from = feed.getJSONObject("from");
                        String name = from.getString("name");
                        String url = from.getJSONObject("picture").getJSONObject("data").getString("url");
                        Bitmap image = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                        String story = feed.getString("story");
                        String text = "";
                        if (feed.has("message")) {
                            text = feed.getString("message");
                            if (text == "")
                                text = story;
                        }
                        String dateS = feed.getString("created_time");
                        //long date = Date.parse(dateS);
                        long date = 0;
                        int likes = 0;
                        int reposts = 0;
                        int comments = 0;

                        tf = new TempFeed(id, name, image, story, text, date, likes, reposts, comments);

                        String objectId = feed.getString("object_id");
                        Bundle params = new Bundle();
                        params.putString("fields", "id, images");

                        new GraphRequest(AccessToken.getCurrentAccessToken(),
                                objectId,
                                params,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        new Thread(new Listener(response, 1)).start();
                                    }
                                }).executeAsync();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
            if (step == 1) {
                try {
                    JSONObject o1 = response.getJSONObject();
                    JSONArray images = o1.getJSONArray("images");
                    JSONObject attach = images.getJSONObject(0);
                    String url = attach.getString("source");
                    Bitmap imageAttach = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());

                    List<Bitmap> temp = new ArrayList<>();
                    temp.add(imageAttach);

                    News n = new News(G.FB, tf.id, tf.image, tf.name, tf.text, temp, tf.date, tf.likes, tf.reposts, tf.comments);
                    news.add(n);

                    res += n.toString() + "\n";

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LogV.setText(res);
                        }
                    });
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class TempFeed {
        public String id;
        public String name;
        public Bitmap image;
        public String story;
        public String text;
        public long date;
        public int likes;
        public int reposts;
        public int comments;

        public TempFeed(String id, String name, Bitmap image, String story, String text, long date, int likes, int reposts, int comments) {
            this.id = id;
            this.name = name;
            this.image = image;
            this.story = story;
            this.text = text;
            this.date = date;
            this.likes = likes;
            this.reposts = reposts;
            this.comments = comments;
        }
    }
}
