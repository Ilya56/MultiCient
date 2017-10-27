package com.dia.multiclient.message.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreListener;
import com.dia.multiclient.message.MessageHistoryAdapter;
import com.dia.multiclient.profile.User;
import com.dia.multiclient.vk.VKCustom;
import com.dia.multiclient.cores.CoreActivity;
import com.dia.multiclient.message.Message;
import com.dia.multiclient.message.MessagePreview;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiMessages;
import com.vk.sdk.api.methods.VKApiUsers;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ilya on 23.08.2016.
 */
public class MessageActivity extends CoreActivity {
    private MessagePreview messagePreview;
    private List<Message> messages;
    private int n = 1;
    private boolean sendReq = false;
    private String userId;
    private MessageHistoryAdapter adapter;
    private ListView listView;
    private boolean Bsend = false;
    private Timer timer;
    private final String KEY_MESS = "messages";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        if (getIntent().hasExtra("mess"))
            messagePreview = getIntent().getParcelableExtra("mess");
        else if (getIntent().hasExtra("user")) {
            user = getIntent().getParcelableExtra("user");
            messagePreview = new MessagePreview(user.getImage(), user.getFirstName(), user.getLastName(), user.isOnline(), user.getId());
        }
        if (savedInstanceState != null) {
            messages = savedInstanceState.getParcelableArrayList(KEY_MESS);
        } else {
            messages = new ArrayList<>();

            userId = messagePreview.getUserID();

            listView = (ListView) findViewById(R.id.list_messages);
            adapter = new MessageHistoryAdapter(messages, this);
            listView.setAdapter(adapter);

            VKRequest request = new VKApiUsers().get(VKParameters.from(VKApiConst.FIELDS, "online,photo_50"));
            request.useSystemLanguage = true;
            Log.d("request_vk", request.toString());
            request.executeWithListener(new VKRequest.VKRequestListener () {
                @Override
                public void onComplete(VKResponse response) {
                    new Thread(new Listener(response, 0, false)).start();
                    Log.d("response_vk", response.json.toString());
                }

                @Override
                public void onError(VKError error) {
                    showError();
                }
            });
        }

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount - 1 && !sendReq) {
                    VKRequest request = new VKApiMessages().getHistory(VKParameters.from(VKApiConst.COUNT, "20", VKApiConst.OFFSET, String.valueOf(n * 20), VKApiConst.USER_ID, userId));
                    request.useSystemLanguage = true;
                    Log.d("request_vk", request.toString());
                    request.executeWithListener(new VKRequest.VKRequestListener () {
                        @Override
                        public void onComplete(VKResponse response) {
                            new Thread(new Listener(response, 1, true)).start();
                            Log.d("response_vk", response.json.toString());
                        }

                        @Override
                        public void onError(VKError error) {
                            showError();
                        }
                    });
                    sendReq = true;
                    n++;
                    Log.d("messageH", "N mess " + n);
                }
            }
        });

        final EditText toSend = (EditText) findViewById(R.id.textSend);
        ImageButton send = (ImageButton) findViewById(R.id.send);

        assert send != null;
        assert toSend != null;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = toSend.getText().toString();
                VKRequest request = new VKCustom("messages").customRequest("send", VKParameters.from(VKApiConst.USER_ID, userId, VKApiConst.MESSAGE, message));
                request.useSystemLanguage = true;
                Log.d("request_vk", request.toString());
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        Log.d("response_vk", response.json.toString());
                        getHistory();
                        Bsend = false;
                    }

                    @Override
                    public void onError(VKError error) {
                        showError();
                    }
                });
                toSend.setText("");
                Bsend = true;
            }
        });

        timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (!Bsend)
                    getHistory();
            }
        };
        timer.schedule(tt, 3000, 7000);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private static class User1 {
        static String Fname;
        static String Lname;
        static Bitmap image;

        public static String getFname() {
            return Fname;
        }

        public static Bitmap getImage() {
            return image;
        }

        public String getLname() {
            return Lname;
        }

        public static void setFname(String fname) {
            Fname = fname;
        }

        public static void setLname(String lname) {
            Lname = lname;
        }

        public static void setImage(Bitmap image) {
            User1.image = image;
        }

        public String toString() {
            return Fname + " " + Lname + " " + image.toString();
        }
    }

    private class Listener extends CoreListener {
        public Listener(VKResponse vkResponse, int step, boolean add) {
            super(vkResponse, step, add);
        }

        @Override
        public void run() {
            if (step == 0) {
                try {
                    JSONObject o1 = vkResponse.json;
                    JSONArray users = o1.getJSONArray("response");
                    VKApiUser user1 = new VKApiUser().parse((JSONObject) users.get(0));
                    String Fname = user1.first_name;
                    String Lname = user1.last_name;
                    URL url = new URL(user1.photo_50);
                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    User1.setFname(Fname);
                    User1.setLname(Lname);
                    User1.setImage(image);
                    Log.d("qwe", new User1().toString());

                    getHistory();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else if (step == 1) {
                try {
                    JSONObject o1 = vkResponse.json;
                    JSONObject o2 = (JSONObject) o1.get("response");
                    JSONArray o3 = (JSONArray) o2.get("items");

                    if (!add)
                        messages.clear();

                    for(int i = 0; i < o3.length(); i++) {
                        JSONObject mess = (JSONObject) o3.get(i);
                        String body = mess.getString("body");
                        int date = mess.getInt("date");
                        int fromId = mess.getInt("from_id");
                        int userId = mess.getInt("user_id");
                        boolean read = (mess.getInt("read_state") == 1);
                        final Message m;
                        if (fromId != userId)
                            m = new Message(body, User1.getFname(), User1.getImage(), date, true, read);
                        else
                            m = new Message(body, messagePreview.getFname(), messagePreview.getImage(), date, false, read);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.add(m);
                                //listView.smoothScrollToPosition(0);
                            }
                        });

                        Log.d("messageH", m.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sendReq = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_messages, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getHistory() {
        VKRequest request = new VKApiMessages().getHistory(VKParameters.from(VKApiConst.COUNT, "20", VKApiConst.USER_ID, userId));
        request.useSystemLanguage = false;
        Log.d("request_vk", request.toString());
        request.executeWithListener(new VKRequest.VKRequestListener () {
            @Override
            public void onComplete(VKResponse response) {
                new Thread(new Listener(response, 1, false)).start();
                Log.d("response_vk", response.json.toString());
            }

            @Override
            public void onError(VKError error) {
                showError();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelableArrayList(KEY_MESS, (ArrayList<Message>) messages);
    }

    private void showError() {
        G.showError(this);
    }
}
