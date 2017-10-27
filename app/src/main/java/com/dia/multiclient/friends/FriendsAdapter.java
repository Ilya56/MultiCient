package com.dia.multiclient.friends;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreAdapter;
import com.dia.multiclient.message.activities.MessageActivity;
import com.dia.multiclient.profile.User;

import java.util.List;

/**
 * Created by Ilya on 11.10.2016.
 */
public class FriendsAdapter extends CoreAdapter<User> {
    public FriendsAdapter(List<User> list, Activity activity) {
        super(list, activity);
    }

    private class ViewHolder {
        public ImageView image;
        public TextView name;
        public ImageView online;
        public Button messages;
        public ImageView from;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder h;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_friends, viewGroup, false);

            h = new ViewHolder();
            h.image = (ImageView) view.findViewById(R.id.image);
            h.name = (TextView) view.findViewById(R.id.name);
            h.online = (ImageView) view.findViewById(R.id.online);
            h.messages = (Button) view.findViewById(R.id.message);
            h.from = (ImageView) view.findViewById(R.id.from);

            view.setTag(h);
        } else {
            h = (ViewHolder) view.getTag();
        }

        final User user = (User) getItem(i);
        if (user != null) {

            Bitmap image = user.getImage();
            String name = user.getFirstName() + " " + user.getLastName();
            boolean online = user.isOnline();
            int from = user.getFrom();

            h.image.setImageBitmap(image);
            h.name.setText(name);
            if (online)
                h.online.setImageBitmap(BitmapFactory.decodeResource(view.getResources(), R.mipmap.ic_online));
            else
                h.online.setImageBitmap(null);
            h.messages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(activity, MessageActivity.class);
                    i.putExtra("user", user);
                    activity.startActivity(i);
                }
            });

            if (from == G.VK) {
                h.from.setImageDrawable(activity.getResources().getDrawable(R.drawable.from_vk));
            } else if (from == G.FB) {
                h.from.setImageDrawable(activity.getResources().getDrawable(R.drawable.from_fb));
            }
        }

        return view;
    }
}
