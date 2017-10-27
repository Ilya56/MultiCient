package com.dia.multiclient.friends.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreFragment;
import com.dia.multiclient.cores.CoreListener;
import com.dia.multiclient.database.services.FriendsDBService;
import com.dia.multiclient.friends.FriendsAdapter;
import com.dia.multiclient.profile.User;
import com.dia.multiclient.profile.activities.ProfileActivity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 11.10.2016.
 */
public class FriendsFragment extends CoreFragment {
    private ListView listView;
    private List<User> friends;
    private FriendsAdapter adapter;
    private static final String KEY_FR = "friends";
    private List<User> saveFriends;
    private boolean[] filter = new boolean[G.COUNT];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        if (savedInstanceState != null)
            friends = savedInstanceState.getParcelableArrayList(KEY_FR);
        else {
            friends = new FriendsDBService(getActivity()).getAll();
            if (friends == null)
                friends = new ArrayList<>();

            listView = (ListView) view.findViewById(R.id.list_friends);
            adapter = new FriendsAdapter(friends, getActivity());
            listView.setAdapter(adapter);

            if (G.loginInVK) {
                VKRequest request = VKApi.friends().get(VKParameters.from("order", "hints", VKApiConst.FIELDS, "name, photo_50"));
                request.useSystemLanguage = true;
                Log.d("request_vk", request.toString());
                request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        new Thread(new Listener(response, 0)).start();
                        Log.d("response_vk", response.json.toString());
                    }

                    @Override
                    public void onError(VKError error) {
                        Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (G.loginInFB) {
                Bundle params = new Bundle();
                params.putString("fields", "id, first_name, last_name, picture");
                GraphRequest g = new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "me/taggable_friends",
                        params,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                new Thread(new Listener(response)).start();
                                Log.d("response_fb", response.toString());
                            }
                        }
                );
                g.executeAsync();
                Log.d("request_fb", g.toString());
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (friends.get(i).getFrom() == G.VK) {
                        startActivity(new Intent(getActivity(), ProfileActivity.class).putExtra(G.USER, friends.get(i)));
                    } else if (friends.get(i).getFrom() == G.FB) {
                        Toast.makeText(getActivity(), getString(R.string.cant_show_fb_profile), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    if (G.loginInVK) {
                        if (newText != "") {
                            VKRequest request = VKApi.users().search(VKParameters.from(VKApiConst.Q, newText,
                                    VKApiConst.SORT, "0", VKApiConst.COUNT, "20", "fields", "photo_50, online"));
                            Log.d("request_vk", request.toString());
                            request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    new Thread(new Listener(response, 2)).start();
                                    Log.d("response_vk", response.json.toString());
                                }

                                @Override
                                public void onError(VKError error) {
                                    Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            reset();
                        }
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (G.loginInVK) {
                        reset();
                    }
                    return true;
                }
            });

            searchView.setOnQueryTextListener(queryTextListener);
            searchView.setIconified(false);
        }

        saveFriends = new ArrayList<>();
        saveFriends.addAll(friends);

        for(int i = 0; i < G.COUNT; i++) {
            filter[i] = true;
        }

        return view;
    }

    private class Listener extends CoreListener {
        public Listener(VKResponse vkResponse, int step) {
            super(vkResponse, step);
        }

        public Listener(GraphResponse fbResponse) {
            super(fbResponse, 1);
        }

        @Override
        public void run() {
            if (step == 0 || step == 2) {
                try {
                    adapter.clear();
                    JSONObject json = vkResponse.json;
                    JSONObject response = json.getJSONObject("response");
                    JSONArray items = response.getJSONArray("items");
                    if (step == 0)
                        new FriendsDBService(getActivity()).clear();
                    for(int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        int id = item.getInt("id");
                        String firstName = item.getString("first_name");
                        String lastName = item.getString("last_name");
                        URL url = new URL(item.getString("photo_50"));
                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        boolean online = item.getInt("online") == 1;

                        final User u = new User(getActivity(), G.VK, "" + id, firstName, lastName, image, online);

                        if (getActivity() == null) {
                            error = true;
                            break;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.add(u);
                            }
                        });
                        if (step == 0)
                            new FriendsDBService(getActivity()).save(u);

                        Log.d("friends", u.toString());
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
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
                        JSONObject friend = data.getJSONObject(i);
                        String id = friend.getString("id");
                        String first_name = friend.getString("first_name");
                        String last_name = friend.getString("last_name");
                        JSONObject picture = friend.getJSONObject("picture");
                        JSONObject data1 = picture.getJSONObject("data");
                        String url = data1.getString("url");
                        Bitmap image = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());

                        final User u = new User(getActivity(), G.FB, id, first_name, last_name, image, "", false, "", "", "", "", 0, 0, 0, 0, "", 0);
                        if (getActivity() == null) {
                            error = true;
                            break;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.add(u);
                            }
                        });
                        new FriendsDBService(getActivity()).save(u);

                        Log.d("friends", u.toString());
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_FR, (ArrayList<User>) friends);
    }

    public void reload() {
        if (G.loginInVK && filter[0]) {
            VKRequest request = VKApi.friends().get(VKParameters.from("order", "hints", VKApiConst.FIELDS, "name, photo_50"));
            request.useSystemLanguage = true;
            Log.d("request_vk", request.toString());
            request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    new Thread(new Listener(response, 0)).start();
                    Log.d("response_vk", response.json.toString());
                }

                @Override
                public void onError(VKError error) {
                    Toast.makeText(getActivity(), getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (G.loginInFB && filter[1]) {
            Bundle params = new Bundle();
            params.putString("fields", "id, first_name, last_name, picture");
            GraphRequest g = new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "me/taggable_friends",
                    params,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            new Thread(new Listener(response)).start();
                            Log.d("response_fb", response.toString());
                        }
                    }
            );
            g.executeAsync();
            Log.d("request_fb", g.toString());
        }
    }

    public boolean[] getFilter() {
        return filter;
    }

    public void setFilter(boolean[] filter) {
        this.filter = filter;
        reset();
        Log.d("qwe", saveFriends.toString());
        if (!filter[0])
            remove(G.VK);
        if (!filter[1])
            remove(G.FB);
        adapter.set(friends);
    }

    private void remove(int j) {
        for(int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getFrom() == j) {
                friends.remove(i);
                i--;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        View view = listView;
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void reset() {
        friends.clear();
        friends.addAll(saveFriends);
    }
}
