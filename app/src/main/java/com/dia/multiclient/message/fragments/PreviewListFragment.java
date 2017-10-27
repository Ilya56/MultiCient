package com.dia.multiclient.message.fragments;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Toast;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreFragment;
import com.dia.multiclient.cores.CoreListener;
import com.dia.multiclient.database.services.MessagePreviewDBService;
import com.dia.multiclient.message.MessagePreview;
import com.dia.multiclient.message.MessagePreviewAdapter;
import com.dia.multiclient.message.activities.MessageActivity;
import com.dia.multiclient.message.dialogs.DeleteDialog;
import com.dia.multiclient.message.listener.LongPollServer;
import com.dia.multiclient.vk.VKCustom;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiMessages;
import com.vk.sdk.api.methods.VKApiUsers;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 21.08.2016.
 */
public class PreviewListFragment extends CoreFragment {
    private MessagePreviewAdapter adapter;
    private List<MessagePreview> messages;
    private int n = 1;
    private boolean sendReq = false;
    private List<MessageTemp> messageTemps = new ArrayList<>();
    private Activity activity;
    private final String KEY_MESS = "messages";
    private Bundle saveMess = new Bundle();
    private int deleteIndex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_preview_message_list, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list_messages);

        messages = new MessagePreviewDBService(getActivity()).getAll();
        if (savedInstanceState != null) {
            messages = savedInstanceState.getParcelableArrayList(KEY_MESS);
        } else {
            if (messages == null)
                messages = new ArrayList<>();

            getMessages(20, 0, requestListener);
        }

        adapter = new MessagePreviewAdapter(messages, getActivity());
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount - 1 && !sendReq) {
                    getMessages(20, n * 20, new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            new Thread(new Listener(response, 0, true)).start();
                            Log.d("response vk", response.json.toString());
                        }

                        @Override
                        public void onError(VKError error) {
                            G.showError(getActivity());
                        }
                    });
                    sendReq = true;
                    n++;
                    Log.d("message", "N prev " + n);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), MessageActivity.class).putExtra("mess", messages.get(position)));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new DeleteDialog().show(getActivity().getFragmentManager(), "1");
                deleteIndex = i;
                return true;
            }
        });

        VKRequest request = new VKCustom("messages").customRequest("getLongPollServer", VKParameters.from("use_ssl", 1, "need_pts", 1));
        request.secure = true;
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                new Thread(new LongPollServer(response, 0, getActivity())).start();
                Log.d("response_vk", response.json.toString());
            }

            @Override
            public void onError(VKError error) {
                G.showError(getActivity());
            }
        });
        Log.d("request_vk", request.toString());

        activity = getActivity();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onSaveInstanceState(saveMess);
    }

    VKRequest.VKRequestListener requestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            new Thread(new Listener(response, 0, false)).start();
            Log.d("response_vk", response.json.toString());
        }

        @Override
        public void onError(VKError error) {
            G.showError(getActivity());
        }
    };

    private class Listener extends CoreListener {
        public Listener(VKResponse vkResponse, int step, boolean add) {
            super(vkResponse, step, add);
        }

        @Override
        public void run() {
            if (step == 0) {
                //if (!add)
                    messageTemps.clear();
                try {
                    JSONObject o1 = vkResponse.json;
                    JSONObject resp = (JSONObject) o1.get("response");
                    JSONArray arrmess = (JSONArray) resp.get("items");
                    String req = "";
                    for (int i = 0; i < arrmess.length(); i++) {
                        JSONObject temp = (JSONObject) arrmess.get(i);
                        JSONObject temp2 = (JSONObject) temp.get("message");
                        VKApiMessage message = new VKApiMessage().parse(temp2);
                        Log.d("qwe", message.body);
                        if (temp2.has("chat_id"))
                            continue;
                        int id = message.user_id;
                        req += id + ",";
                        messageTemps.add(new MessageTemp(message.body, message.date, message.read_state, message.user_id));
                    }
                    req = req.substring(0, req.length());
                    VKRequest request = new VKApiUsers().get(VKParameters.from(VKApiConst.USER_IDS, req, VKApiConst.FIELDS, "online,photo_50"));
                    if (add)
                        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                new Thread(new Listener(response, 1, true)).start();
                                Log.d("response_vk", response.json.toString());
                            }

                            @Override
                            public void onError(VKError error) {
                                G.showError(getActivity());
                            }
                        });
                    else
                        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                new Thread(new Listener(response, 1, false)).start();
                                Log.d("response_vk", response.json.toString());
                            }

                            @Override
                            public void onError(VKError error) {
                                G.showError(getActivity());
                            }
                        });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (step == 1) {
                if (!add) {
                    adapter.clear();
                    new MessagePreviewDBService(getActivity()).clear();
                }

                try {
                    JSONObject o1 = vkResponse.json;
                    JSONArray arrusers = (JSONArray) o1.get("response");
                    for(int i = 0; i < messageTemps.size(); i++) {
                        JSONObject temp1 = (JSONObject) arrusers.get(i);
                        VKApiUser user = new VKApiUser().parse(temp1);
                        URL url = new URL(user.photo_50);
                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        String id = "" + user.getId();
                        MessageTemp message = messageTemps.get(i);
                        final MessagePreview m = new MessagePreview(image, user.first_name, user.last_name, message.body, message.time, user.online, id, message.readState);

                        if (getActivity() == null) {
                            error = true;
                            break;
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.add(m);
                            }
                        });
                        new MessagePreviewDBService(getActivity()).save(m);
                        Log.d("message", m.toString());
                    }
                } catch (JSONException | IOException | OutOfMemoryError e) {
                    e.printStackTrace();
                }
                if (!error) {
                    Thread t = new Thread(new Timer1());
                    t.start();
                }
            }
        }
    }

    private class MessageTemp {
        public String body;
        public long time;
        public boolean readState;
        public int userId;

        public MessageTemp(String body, long time, boolean readState, int userId) {
            this.body = body;
            this.time = time;
            this.readState = readState;
            this.userId = userId;
        }

        public String toString() {
            return body + " " + userId;
        }
    }

    private void getMessages(int count, int offset, VKRequest.VKRequestListener listener) {
        VKRequest request = new VKApiMessages().getDialogs(VKParameters.from(VKApiConst.COUNT, String.valueOf(count),
                VKApiConst.OFFSET, String.valueOf(offset)));
        request.useSystemLanguage = true;
        Log.d("request_vk", request.toString());
        request.executeSyncWithListener(listener);
    }

    public void reload() {
        getMessages(n * 20, 0, requestListener);
    }

    private class Timer1 implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendReq = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_MESS, (ArrayList<MessagePreview>) messages);
    }

    public void deleteHistory() {
        VKRequest request = VKApi.messages().deleteDialog(VKParameters.from(VKApiConst.USER_ID, messages.get(deleteIndex).getUserID()));
        request.secure = true;
        request.executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.remove(deleteIndex);
                    }
                });
                Toast.makeText(activity, getString(R.string.dialog_deleted), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(VKError error) {
                G.showError(getActivity());
            }
        });
    }

    public MessagePreviewAdapter getAdapter() {
        return adapter;
    }
}
