package com.dia.multiclient.message;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreAdapter;

import java.util.List;

/**
 * Created by Ilya on 26.10.2016.
 */
public class MessageHistoryAdapter extends CoreAdapter<Message> {
    public MessageHistoryAdapter(List<Message> list, Activity activity) {
        super(list, activity);
    }

    private class ViewHolder {
        ImageView image;
        TextView name;
        TextView body;
        TextView time;
        LinearLayout back;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_message_hystory, parent, false);

            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.body = (TextView) convertView.findViewById(R.id.body);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.back = (LinearLayout) convertView.findViewById(R.id.background);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message mess = list.get(position);
        Bitmap image = mess.getImage();
        String name = mess.getName();
        String body = mess.getBody();
        String time = G.toRealTime(mess.getTime());
        boolean read = mess.isRead();

        viewHolder.image.setImageBitmap(image);
        viewHolder.name.setText(name);
        viewHolder.body.setText(body);
        viewHolder.time.setText(time);
        if (!read)
            viewHolder.back.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), R.color.notRead));
        else
            viewHolder.back.setBackgroundColor(0);

        return convertView;
    }
}
