package com.dia.multiclient.news.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreFragment;
import com.dia.multiclient.cores.CoreListener;
import com.dia.multiclient.news.Docs;
import com.dia.multiclient.news.News;
import com.dia.multiclient.news.NewsAdapter;
import com.dia.multiclient.news.dialogs.DeleteDialogFB;
import com.dia.multiclient.news.dialogs.DeleteDialogVK;
import com.dia.multiclient.profile.User;
import com.dia.multiclient.vk.VKCustom;
import com.dia.multiclient.vk.VKErrorActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDocument;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ilya on 10.09.2016.
 */

    public class NewsFragment extends CoreFragment {
    private NewsAdapter adapter;
    private ArrayList<News> news;
    private boolean sendReq;
    private int n;
    private String nextFrom;
    private final String KEY_NEWS = "news";
    private List<News> saveNews;
    private int deleteIndex;
    private boolean[] filter = new boolean[G.COUNT];

    //fb
    private CallbackManager callbackManager;
    private TempFeed tf;
    private List<User> userTemp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        FacebookSdk.sdkInitialize(this.getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        ListView listView = (ListView) view.findViewById(R.id.list_news);

        if (savedInstanceState != null) {
            news = savedInstanceState.getParcelableArrayList(KEY_NEWS);
        } else {
            news = new ArrayList<>();
            adapter = new NewsAdapter(news, getActivity());
            listView.setAdapter(adapter);

            //vk
            if (G.loginInVK) {
                VKRequest request = new VKCustom("newsfeed").customRequest("get", VKParameters.from(VKApiConst.FILTERS, "post", VKApiConst.COUNT, "10"));
                request.useSystemLanguage = true;
                request.executeSyncWithListener(requestListener);
                Log.d("request_vk", request.toString());
            }

            //fb
            if (G.loginInFB) {
                Bundle params = new Bundle();
                params.putString("fields", "id, from{name, id, picture}, story, message, object_id, created_time");
                GraphRequest g = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "me/feed",
                        params,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                new Thread(new Listener(response, 1)).start();
                                Log.d("response_fb", response.toString());
                            }
                        }
                );
                g.executeAsync();
                Log.d("request_fb", g.toString());
            }
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount - 1 && !sendReq && filter[G.VK]) {
                    if (G.loginInVK) {
                        VKRequest request = new VKCustom("newsfeed").customRequest("get", VKParameters.from(VKApiConst.FILTERS, "post", VKApiConst.COUNT, "10", "start_from", nextFrom));
                        request.useSystemLanguage = true;
                        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                new Thread(new Listener(response, 0, true)).start();
                                Log.d("response_vk", response.json.toString());
                            }

                            @Override
                            public void onError(VKError error) {
                                Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                                startError(error.toString());
                            }
                        });
                        sendReq = true;
                        n++;
                        Log.d("qwe", "N news " + n);
                    }
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (news.get(i).getFrom() == G.VK) {
                    new DeleteDialogVK().show(getActivity().getFragmentManager(), "1");
                    deleteIndex = i;
                    return true;
                }
                else if (news.get(i).getFrom() == G.FB) {
                    new DeleteDialogFB().show(getActivity().getFragmentManager(), "1");
                    deleteIndex = i;
                    return true;
                }
                return false;
            }
        });

        saveNews = new ArrayList<>();
        saveNews.addAll(news);

        for(int i = 0; i < G.COUNT; i++) {
            filter[i] = true;
        }

        return view;
    }

    VKRequest.VKRequestListener requestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            new Thread(new Listener(response, 0, false)).start();
            Log.d("response_vk", response.json.toString());
        }

        @Override
        public void onError(VKError error) {
            Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            startError(error.toString());
        }
    };

    private class Listener extends CoreListener {
        public Listener(VKResponse vkResponse, int step, boolean add) {
            super(vkResponse, step, add);
        }

        public  Listener(GraphResponse fbResponse, int step) {
            super(fbResponse, step);
        }

        @Override
        public void run() {
            if (step == 0) {
                if (!add)
                    adapter.clear();

                try {
                    JSONObject o1 = vkResponse.json;
                    JSONObject response = o1.getJSONObject("response");
                    JSONArray items = response.getJSONArray("items");
                    JSONArray profiles = response.getJSONArray("profiles");
                    JSONArray groups = response.getJSONArray("groups");
                    for(int i = 0; i < items.length(); i++) {
                        try {
                            JSONObject item = items.getJSONObject(i);
                            JSONObject jLikes = item.getJSONObject("likes");
                            JSONObject jReposts = item.getJSONObject("reposts");
                            JSONObject jComments = item.getJSONObject("comments");
                            List<Bitmap> images = new ArrayList<>();
                            List<Docs> docs = new ArrayList<>();
                            JSONObject item2 = item;
                            if (item.has("copy_history")) {
                                item2 = item.getJSONArray("copy_history").getJSONObject(0);
                                Log.d("qwe", "has history");
                            }
                            if (item2.has("attachments")) {
                                JSONArray attachments = item2.getJSONArray("attachments");
                                for (int j = 0; j < attachments.length(); j++) {
                                    JSONObject attachment = attachments.getJSONObject(j);
                                    if (attachment.getString("type").equals("photo")) {
                                        VKApiPhoto photo = new VKApiPhoto(attachment.getJSONObject("photo"));
                                        URL url = new URL(photo.src.getByType('x'));
                                        images.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                                    }
                                    if (attachment.getString("type").equals("doc")) {
                                        VKApiDocument doc1 = new VKApiDocument(attachment.getJSONObject("doc"));
                                        if (doc1.ext.equals("gif")) {
                                            URL imageURL = new URL(doc1.url);
                                            Bitmap image = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                                            Docs doc = new Docs(doc1.id, doc1.url, doc1.ext, image);
                                            docs.add(doc);
                                        }
                                    }
                                    if (attachment.getString("type").equals("video")) {
                                        VKApiVideo video1 = new VKApiVideo(attachment.getJSONObject("video"));
                                        URL imageURL = new URL(video1.photo_320);
                                        Bitmap image = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                                        Docs video = new Docs(video1.id, video1.player, "video", image);
                                        docs.add(video);
                                    }
                                }
                            }

                            Bitmap imageFrom = null;
                            String name = null;
                            int source = item.getInt("source_id");
                            if (source > 0) {
                                for (int j = 0; j < profiles.length(); j++) {
                                    JSONObject profile = profiles.getJSONObject(j);
                                    if (profile.getInt("id") == source) {
                                        String url = profile.getString("photo_50");
                                        imageFrom = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                                        name = profile.getString("first_name") + " " + profile.getString("last_name");
                                        break;
                                    }
                                }
                            } else if (source < 0) {
                                for (int j = 0; j < groups.length(); j++) {
                                    JSONObject group = groups.getJSONObject(j);
                                    if (group.getInt("id") == -source) {
                                        String url = group.getString("photo_50");
                                        imageFrom = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                                        name = group.getString("name");
                                        break;
                                    }
                                }
                            } else {
                                imageFrom = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_online);
                                name = "unknown";
                            }

                            String name_s = "";
                            if (item != item2) {
                                int source2 = item2.getInt("source_id");
                                if (source2 > 0) {
                                    for (int j = 0; j < profiles.length(); j++) {
                                        JSONObject profile = profiles.getJSONObject(j);
                                        if (profile.getInt("id") == source2) {
                                            name_s = profile.getString("first_name") + " " + profile.getString("last_name");
                                            break;
                                        }
                                    }
                                } else if (source2 < 0) {
                                    for (int j = 0; j < groups.length(); j++) {
                                        JSONObject group = groups.getJSONObject(j);
                                        if (group.getInt("id") == -source2) {
                                            name_s = group.getString("name");
                                            break;
                                        }
                                    }
                                } else {
                                    name_s = "unknown";
                                }
                            }

                            String text = item.getString("text");
                            long date = item.getInt("date");
                            int likes = jLikes.getInt("count");
                            int reposts = jReposts.getInt("count");
                            int comments = jComments.getInt("count");
                            String id = String.valueOf(item.getInt("post_id"));
                            boolean isLiked = jLikes.getInt("user_likes") == 1;
                            boolean isReposted = jReposts.getInt("user_reposted") == 1;

                            if (item2 != item) {
                                text = item2.getString("text");
                            }

                            final News n = new News(G.VK, id, imageFrom, name, text, images, date, likes, reposts, comments, "" + source, docs, isLiked, isReposted, name_s);
                            if (getActivity() != null) {
                                error = true;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.add(n);
                                        saveNews.add(n);
                                    }
                                });
                            }

                            Log.d("news", n.toString());
                        } catch (JSONException | IOException | OutOfMemoryError e) {
                            e.printStackTrace();
                        }
                    }

                    nextFrom = response.getString("next_from");
                } catch (JSONException | OutOfMemoryError e) {
                    e.printStackTrace();
                }

                sendReq = false;
            }
            if (step == 1) {
                try {
                    JSONObject o1 = fbResponse.getJSONObject();
                    if (o1 == null)
                        return;
                    if (!o1.has("data")) {
                        return;
                    }
                    JSONArray data = o1.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject feed = data.getJSONObject(i);
                        String id = feed.getString("id");
                        JSONObject from = feed.getJSONObject("from");
                        String name = from.getString("name");
                        String url = from.getJSONObject("picture").getJSONObject("data").getString("url");
                        Bitmap image = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                        String story = "";
                        if (feed.has("story"))
                            story = feed.getString("story");
                        String text = "";
                        if (feed.has("message")) {
                            text = feed.getString("message");
                            if (text == "")
                                text = story;
                        } else
                            text = story;
                        String dateS = feed.getString("created_time");
                        SimpleDateFormat parser = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ssZ");
                        Date dateD = parser.parse(dateS);
                        long date = dateD.getTime();
                        int likes = 0;
                        int reposts = 0;
                        int comments = 0;

                        tf = new TempFeed(id, name, image, story, text, date, likes, reposts, comments, id);

                        if (feed.has("object_id")) {
                            String objectId = feed.getString("object_id");
                            Bundle params = new Bundle();
                            params.putString("fields", "id, images");

                            GraphRequest g = new GraphRequest(AccessToken.getCurrentAccessToken(),
                                    objectId,
                                    params,
                                    HttpMethod.GET,
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            new Thread(new Listener(response, 2)).start();
                                            Log.d("response_fb", response.toString());
                                        }
                                    });
                            g.executeAsync();
                            Log.d("request_fb", g.toString());
                        }
                    }
                } catch (JSONException | IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
            if (step == 2) {
                try {
                    JSONObject o1 = fbResponse.getJSONObject();
                    if (o1.has("images")) {
                        JSONArray images = o1.getJSONArray("images");
                        JSONObject attach = images.getJSONObject(0);
                        String url = attach.getString("source");
                        Bitmap imageAttach = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());

                        List<Bitmap> temp = new ArrayList<>();
                        temp.add(imageAttach);

                        final News n = new News(G.FB, tf.id, tf.image, tf.name, tf.text, temp, tf.date, tf.likes, tf.reposts, tf.comments, tf.userId);
                        if (getActivity() == null) {
                            error = true;
                        } else
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.add(n);
                                    saveNews.add(n);
                                }
                            });

                        Log.d("news", n.toString());
                    }
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            if (step == 3) {
                try {
                    userTemp = new ArrayList<>();
                    JSONObject o1 = fbResponse.getJSONObject();
                    if (o1 == null)
                        return;
                    if (!o1.has("data")) {
                        return;
                    }
                    JSONArray data = o1.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject friend = data.getJSONObject(i);
                        String id = friend.getString("id");
                        String first_name = friend.getString("first_name");
                        String last_name = friend.getString("first_name");
                        JSONObject picture = friend.getJSONObject("picture");
                        JSONObject data1 = picture.getJSONObject("data");
                        String url = data1.getString("url");
                        Bitmap image = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());

                        User u = new User(getActivity(), G.FB, id, first_name, last_name, image, false);
                        userTemp.add(u);
                        Log.d("users", u.toString());
                        new GraphRequest(AccessToken.getCurrentAccessToken(),
                                id + "/feed",
                                null,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        new Thread(new Listener(response, 1)).start();
                                        Log.d("response_fb", response.toString());
                                    }
                                });
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startError(String error) {
        startActivity(new Intent(getActivity(), VKErrorActivity.class).putExtra("from", "preview list fragment: " + error));
    }

    public void reload() {
        saveNews.clear();

        if (G.loginInVK && filter[0]) {
            VKRequest request = new VKCustom("newsfeed").customRequest("get", VKParameters.from(VKApiConst.FILTERS, "post", VKApiConst.COUNT, "20"));
            request.useSystemLanguage = true;
            request.executeSyncWithListener(requestListener);
        }

        if (G.loginInFB && filter[1]) {
            Bundle params = new Bundle();
            params.putString("fields", "id, from{name, id, picture}, story, message, object_id, created_time");
            GraphRequest g = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "me/feed",
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            new Thread(new Listener(response, 1)).start();
                            Log.d("response_fb", response.toString());
                        }
                    }
            );
            g.executeAsync();
            Log.d("request_fb", g.toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_NEWS, news);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        G.news = news;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
        public String userId;

        public TempFeed(String id, String name, Bitmap image, String story, String text, long date, int likes, int reposts, int comments, String userId) {
            this.id = id;
            this.name = name;
            this.image = image;
            this.story = story;
            this.text = text;
            this.date = date;
            this.likes = likes;
            this.reposts = reposts;
            this.comments = comments;
            this.userId = userId;
        }
    }

    public void deletePost() {
        VKRequest request = new VKCustom("newsfeed").customRequest("ignoreItem", VKParameters.from("type", "wall", "item_id", news.get(deleteIndex).getId(), "owner_id", news.get(deleteIndex).getUserId()));
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.remove(deleteIndex);
                    }
                });
                Toast.makeText(getActivity(), getString(R.string.post_deleted), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deletePostFb() {
        GraphRequest g = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                news.get(deleteIndex).getId(),
                null,
                HttpMethod.DELETE,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (response.getJSONObject().getBoolean("success")) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.remove(deleteIndex);
                                    }
                                });
                                Toast.makeText(getActivity(), getString(R.string.post_deleted), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        g.executeAsync();
        Log.d("qwe", g.toString());
    }

    public boolean[] getFilter() {
        return filter;
    }

    public void setFilter(boolean[] filter) {
        this.filter = filter;
        news.clear();
        news.addAll(saveNews);
        Log.d("qwe", saveNews.toString());
        if (!filter[0])
            remove(G.VK);
        if (!filter[1])
            remove(G.FB);
        adapter.set(news);
    }

    private void remove(int j) {
        for(int i = 0; i < news.size(); i++) {
            if (news.get(i).getFrom() == j) {
                news.remove(i);
                i--;
            }
        }
    }

    public void like(ImageButton button, final News n) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKRequest request = new VKCustom("likes").customRequest("add", VKParameters.from("type", "post", VKApiConst.OWNER_ID, n.getUserId(), "item_id", n.getId()));
                request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        Toast.makeText(getActivity(), getString(R.string.like_post), Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject response1 = response.json.getJSONObject("response");
                            int likes = response1.getInt("likes");
                            n.setLikes(likes);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void repost(ImageButton button, final News n) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VKRequest request = VKApi.wall().repost(VKParameters.from("object", "wall" + n.getId() + "_" + n.getUserId()));
                request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        Toast.makeText(getActivity(), getString(R.string.repost_post), Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject response1 = response.json.getJSONObject("response");
                            int likes = response1.getInt("likes_count");
                            n.setLikes(likes);
                            int reposts = response1.getInt("reposts_count");
                            n.setReposts(reposts);

                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
