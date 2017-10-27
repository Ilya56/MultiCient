package com.dia.multiclient.profile.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreFragment;
import com.dia.multiclient.cores.CoreListener;
import com.dia.multiclient.message.activities.MessageActivity;
import com.dia.multiclient.news.Docs;
import com.dia.multiclient.news.News;
import com.dia.multiclient.news.NewsAdapter;
import com.dia.multiclient.profile.User;
import com.dia.multiclient.profile.dialogs.AddFriendDialog;
import com.facebook.AccessToken;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 03.10.2016.
 */
public class ProfileFragment extends CoreFragment {
    private View view;
    private int from;
    private User user;
    private static Activity activity;
    private static String checkNetwork;
    private static String requestSent;
    public static ProfileFragment pf;
    private Button addToFriends;

    private NewsAdapter adapter;
    private List<News> news;
    private boolean sendReq;
    private int n;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        VKRequest request = null;
        checkNetwork = getString(R.string.check_network);
        requestSent = getString(R.string.request_sent);

        String user_id_vk;
        String user_id_fb = "me";
        Intent i = getActivity().getIntent();
        if (i != null && i.hasExtra(G.USER)) {
            user = i.getParcelableExtra(G.USER);
            Log.d("qwe", user.toString());
            if (user.getFrom() == G.VK) {
                user_id_vk = user.getId();
                request = VKApi.users().get(VKParameters.from(VKApiConst.USER_ID, user_id_vk, VKApiConst.FIELDS, "sex, bdate, city, country, home_town, photo_200, online, domain, has_mobile, contacts, site, education, universities, schools, status, last_seen, followers_count, common_count, occupation, nickname, relatives, relation, personal, connections, exports, wall_comments, activities, interests, music, movies, tv, books, games, about, quotes, can_post, can_see_all_posts, can_see_audio, can_write_private_message, can_send_friend_request, is_favorite, is_hidden_from_feed, timezone, screen_name, maiden_name, crop_photo, is_friend, friend_status, career, military, counters"));
                from = G.VK;
            }
            if (user.getFrom() == G.FB) {
                user_id_fb = user.getId();
                from = G.FB;
            }
        } else {
            Bundle args = getArguments();
            from = args.getInt(G.FROM);
        }

        ListView listView = (ListView) view.findViewById(R.id.list_wall);
        news = new ArrayList<>();
        adapter = new NewsAdapter(news, getActivity());
        listView.setAdapter(adapter);

        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        if (from == G.VK) {
            if (request == null)
                request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "sex, bdate, city, country, home_town, photo_200, photo_400, online, domain, has_mobile, contacts, site, education, universities, schools, status, last_seen, followers_count, common_count, occupation, nickname, relatives, relation, personal, connections, exports, wall_comments, activities, interests, music, movies, tv, books, games, about, quotes, can_post, can_see_all_posts, can_see_audio, can_write_private_message, can_send_friend_request, is_favorite, is_hidden_from_feed, timezone, screen_name, maiden_name, crop_photo, is_friend, friend_status, career, military, counters"));
            Log.d("request_vk", request.toString());
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    new Thread(new Listener(response, 0, false)).start();
                    Log.d("response_vk", response.json.toString());
                }

                @Override
                public void onError(VKError error) {
                    Toast.makeText(activity, getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (from == G.FB) {
            Bundle params = new Bundle();
            params.putString("fields", "id, first_name, last_name, birthday, hometown, education, relationship_status, gender, picture.type(large)");

            GraphRequest g = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    user_id_fb,
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            new Thread(new Listener(response, 1)).start();
                            Log.d("response_fb", response.toString());
                        }
                    });
            g.executeAsync();
            Log.d("request_fb", g.toString());

            Bundle params1 = new Bundle();
            params1.putString("fields", "id, from{name, id, picture}, story, message, object_id, created_time");
            GraphRequest g1 = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "me/feed",
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            new Thread(new Listener(response, 3)).start();
                            Log.d("response_fb", response.toString());
                        }
                    }
            );
            g1.executeAsync();
            Log.d("request_fb", g.toString());
        }

        Button messages = (Button) view.findViewById(R.id.messages);
        addToFriends = (Button) view.findViewById(R.id.add_friend);

        if (user == null) {
            messages.setEnabled(false);
            messages.setAlpha((float)0.5);
            addToFriends.setEnabled(false);
            addToFriends.setAlpha((float)0.5);
        }

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MessageActivity.class);
                i.putExtra("user", user);
                getActivity().startActivity(i);
            }
        });

        if (user != null) {
            final VKRequest request1 = VKApi.friends().areFriends(VKParameters.from(VKApiConst.USER_IDS, user.getId()));
            request1.executeSyncWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    Log.d("response_vk", response.json.toString());
                    try {
                        JSONObject o1 = response.json;
                        JSONArray response1 = o1.getJSONArray("response");
                        JSONObject item1 = response1.getJSONObject(0);
                        int status = item1.getInt("friend_status");
                        if (status > 0) {
                            addToFriends.setEnabled(false);
                            addToFriends.setAlpha((float) 0.5);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VKError error) {
                    Toast.makeText(activity, getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                }
            });

            Log.d("request_vk", request1.toString());

            addToFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddFriendDialog afd = new AddFriendDialog();
                    Bundle b = new Bundle();
                    b.putParcelable(G.USER, user);
                    afd.setArguments(b);
                    afd.show(getActivity().getFragmentManager(), "2");
                }
            });
        }

        activity = getActivity();

        pf = this;

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount - 1 && !sendReq) {
                    VKRequest request = VKApi.wall().get(VKParameters.from(VKApiConst.OWNER_ID, user.getId(), VKApiConst.COUNT, 100, VKApiConst.FIELDS, "", VKApiConst.OFFSET, n * 100));
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
                        }
                    });
                    sendReq = true;
                    n++;
                    Log.d("qwe", "N news " + n);
                }
            }
        });

        return view;
    }

    private class Listener extends CoreListener {
        public Listener(VKResponse vkResponse, int step, boolean add) {
            super(vkResponse, step, add);
        }

        public Listener(GraphResponse fbResponse, int step) {
            super(fbResponse, step);
        }

        @Override
        public void run() {
            if (step == 0) {
                try {
                    JSONObject response1 = vkResponse.json;
                    JSONArray users = response1.getJSONArray("response");
                    JSONObject user1 = users.getJSONObject(0);
                    String fname = user1.getString("first_name");
                    String lname = user1.getString("last_name");
                    URL url = new URL(user1.getString("photo_200"));
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    String status = user1.getString("status");
                    boolean online = user1.getString("online") == "1";
                    String city = user1.has("city") ? user1.getJSONObject("city").getString("title") : "";
                    String country = user1.has("country") ? user1.getJSONObject("country").getString("title") : "";
                    String studyPlace = "";
                    if (user1.has("school")) {
                        JSONArray schools = user1.getJSONArray("schools");
                        if (schools.length() > 0)
                            studyPlace = schools.getJSONObject(0).getString("name");
                    }
                    String relation = "";
                    if (user1.has("relation")) {
                        int relation_ind = user1.getInt("relation");
<<<<<<< HEAD
                        relation = getActivity().getResources().getStringArray(R.array.relation)[relation_ind];
=======
                        if (getActivity() != null) {
                            relation = getActivity().getResources().getStringArray(R.array.relation)[relation_ind];
                        }
>>>>>>> origin/master
                    }
                    JSONObject counter = user1.getJSONObject("counters");
                    int friends = counter.getInt("friends");
                    int photos = counter.getInt("photos");
                    int followers = counter.getInt("followers");
                    int groups = counter.has("groups") ? counter.getInt("groups") : 0;
                    String birthday = user1.has("bdate") ? user1.getString("bdate") : "";
                    int sex = user1.getInt("sex");
                    String id = String.valueOf(user1.getInt("id"));

                    final User user = new User(getActivity(), G.VK, id, fname, lname, image, status, online, city, country, studyPlace, relation, friends, photos, followers, groups, birthday, sex);
                    if (getActivity() == null) {
                        error = true;
                    } else
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setData(user);
                                setUser(user);
                            }
                        });

                    VKRequest request = VKApi.wall().get(VKParameters.from(VKApiConst.OWNER_ID, user.getId(), VKApiConst.COUNT, 100, VKApiConst.FIELDS, ""));
                    request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            new Thread(new Listener(response, 2, false)).start();
                        }

                        @Override
                        public void onError(VKError error) {
                            Toast.makeText(activity, getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
            if (step == 1) {
                if (fbResponse.getError() != null) {
                    error = true;
                } else {
                    try {
                        JSONObject response = fbResponse.getJSONObject();
                        String fname = response.getString("first_name");
                        String lname = response.getString("last_name");
                        URL url = new URL(response.getJSONObject("picture").getJSONObject("data").getString("url"));
                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        String status = response.has("about") ? response.getString("about") : "";
                        boolean online = false;
                        String city = response.has("hometown") ? response.getJSONObject("hometown").getString("name") : "";
                        String country = "";
                        String studyPlace = "";
                        String relation = response.has("relationship_status") ? response.getString("relationship_status") : "";
                        /*JSONObject counter = user1.getJSONObject("counters");
                        int friends = counter.getInt("friends");
                        int photos = counter.getInt("photos");
                        int followers = counter.getInt("followers");
                        int groups = counter.getInt("groups");*/
                        String birthday = response.has("birthday") ? response.getString("birthday") : "";
                        String sex = response.getString("gender");
                        String id = response.getString("id");

                        final User user = new User(getActivity(), G.FB, id, fname, lname, image, status, online, city, country, studyPlace, relation, 0, 0, 0, 0, birthday, sex);
                        if (getActivity() == null) {
                            error = true;
                        } else
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setData(user);
                                    setUser(user);
                                }
                            });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (step == 2) {
                if (!add)
                    adapter.clear();

                try {
                    JSONObject o1 = vkResponse.json;
                    JSONObject response = o1.getJSONObject("response");
                    JSONArray items = response.getJSONArray("items");
                    JSONArray profiles = null;
                    JSONArray groups = null;
                    if (response.has("profiles"))
                        profiles = response.getJSONArray("profiles");
                    if (response.has("groups"))
                        groups = response.getJSONArray("groups");
                    for (int i = 0; i < items.length(); i++) {
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
                            String id = String.valueOf(item.getInt("id"));
                            boolean isLiked = jLikes.getInt("user_likes") == 1;
                            boolean isReposted = jReposts.getInt("user_reposted") == 1;

                            if (item2 != item) {
                                text = item2.getString("text");
                            }

                            final News n = new News(G.VK, id, user.getImage(), user.getFirstName() + " " + user.getLastName(), text, images, date, likes, reposts, comments, user.getId(), docs, isLiked, isReposted, name_s);

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.add(n);
                                    }
                                });
                            }

                            Log.d("news", n.toString());
                        } catch (JSONException | IOException | OutOfMemoryError e) {
                        e.printStackTrace();
                        }
                    }
                } catch (JSONException | OutOfMemoryError e) {
                    e.printStackTrace();
                }

                sendReq = false;
            }
        }
    }

    private void setData(User user) {
        ImageView face = (ImageView) view.findViewById(R.id.face);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView status = (TextView) view.findViewById(R.id.status);
        ImageView online = (ImageView) view.findViewById(R.id.online);
        TextView city = (TextView) view.findViewById(R.id.city);
        TextView studyPlace = (TextView) view.findViewById(R.id.study);
        TextView relation = (TextView) view.findViewById(R.id.relation);
        TextView friends = (TextView) view.findViewById(R.id.friends);
        TextView photo = (TextView) view.findViewById(R.id.photos);
        TextView followers = (TextView) view.findViewById(R.id.followers);
        TextView groups = (TextView) view.findViewById(R.id.groups);
        TextView birthday = (TextView) view.findViewById(R.id.birthday);
        TextView sex = (TextView) view.findViewById(R.id.sex);

        face.setImageBitmap(user.getImage());
        String temp = user.getFirstName() + " " + user.getLastName();
        name.setText(temp);
        status.setText(user.getStatus());
        if (user.isOnline()) online.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_online));
        temp = "";
        temp += (user.getCity() != "") ? user.getCity() : "";
        if (user.getCity() != "" && user.getCountry() != "") {
            temp += ", ";
        }
        temp += user.getCountry() != "" ? user.getCountry() : "";

        city.setText(temp);
        studyPlace.setText(user.getStudyPlace());
        relation.setText(user.getRelation());
        temp = getString(R.string.friends) + ": " + user.getFriends();
        friends.setText(temp);
        temp = getString(R.string.photo) + ": " + user.getPhotos();
        photo.setText(temp);
        temp = getString(R.string.followers) + ": " + user.getFollowers();
        followers.setText(temp);
        temp = getString(R.string.groups) + ": " + user.getGroups();
        groups.setText(temp);
        birthday.setText(user.getBirthday());
        sex.setText(user.getSex());
    }

    public static VKRequest.VKRequestListener requestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            Toast.makeText(activity, requestSent, Toast.LENGTH_LONG).show();
            pf.reload();
            pf.addToFriends.setEnabled(false);
            pf.addToFriends.setAlpha((float) 0.5);
        }

        @Override
        public void onError(VKError error) {
            Toast.makeText(activity, checkNetwork, Toast.LENGTH_SHORT).show();
        }
    };

    public void reload() {
        if (G.loginInVK) {
            VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "sex, bdate, city, country, home_town, photo_200, online, domain, has_mobile, contacts, site, education, universities, schools, status, last_seen, followers_count, common_count, occupation, nickname, relatives, relation, personal, connections, exports, wall_comments, activities, interests, music, movies, tv, books, games, about, quotes, can_post, can_see_all_posts, can_see_audio, can_write_private_message, can_send_friend_request, is_favorite, is_hidden_from_feed, timezone, screen_name, maiden_name, crop_photo, is_friend, friend_status, career, military, counters"));
            Log.d("request_vk", request.toString());
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    new Thread(new Listener(response, 0, false)).start();
                    Log.d("response_vk", response.json.toString());
                }

                @Override
                public void onError(VKError error) {
                    Toast.makeText(activity, getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                }
            });

            VKRequest request1 = VKApi.friends().areFriends(VKParameters.from(VKApiConst.USER_IDS, user.getId()));
            request1.executeSyncWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    Log.d("response_vk", response.json.toString());
                    try {
                        JSONObject o1 = response.json;
                        JSONArray response1 = o1.getJSONArray("response");
                        JSONObject item1 = response1.getJSONObject(0);
                        int status = item1.getInt("friend_status");
                        if (status > 0) {
                            addToFriends.setEnabled(false);
                            addToFriends.setAlpha((float) 0.5);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(VKError error) {
                    Toast.makeText(activity, getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                }
            });

            Log.d("request_vk", request1.toString());
        }

        if (G.loginInFB) {
            Bundle params = new Bundle();
            params.putString("fields", "id, first_name, last_name, birthday, hometown, education, relationship_status, gender, picture.type(large)");

            GraphRequest g = new GraphRequest(AccessToken.getCurrentAccessToken(),
                    "me",
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            new Thread(new Listener(response, 1)).start();
                            Log.d("response_fb", response.toString());
                        }
                    });
            g.executeAsync();
            Log.d("request_fb", g.toString());
        }
    }

    private void setUser(User user) {
        if (this.user == null) {
            this.user = user;
        }
    }
}
