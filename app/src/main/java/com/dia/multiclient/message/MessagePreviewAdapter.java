package com.dia.multiclient.message;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * Created by Ilya on 24.10.2016.
 */
public class MessagePreviewAdapter extends CoreAdapter<MessagePreview> {
    public MessagePreviewAdapter(List<MessagePreview> list, Activity activity) {
        super(list, activity);
    }

    private class ViewHolder {
        ImageView image;
        TextView name;
        TextView lastMess;
        TextView time;
        ImageView online;
        LinearLayout back;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.item_message_preview, parent, false);

            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.lastMess = (TextView) convertView.findViewById(R.id.last_message);
            viewHolder.time = (TextView) convertView.findViewById(R.id.last_time);
            viewHolder.online = (ImageView) convertView.findViewById(R.id.online);
            viewHolder.back = (LinearLayout) convertView.findViewById(R.id.background);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MessagePreview messp = (MessagePreview) getItem(position);
        if (messp != null) {

            Bitmap image = messp.getImage();
            int n = 15;
            String name = messp.getFname() + " " + messp.getLname().substring(0, Math.min(messp.getLname().length(), n)) + (messp.getLname().length() > n ? "..." : "");
            String lastMess = messp.getLastMess();
            String time = G.toRealTime(messp.getLastTime());
            boolean online = messp.isOnline();
            boolean read = messp.isRead();

            viewHolder.image.setImageBitmap(image);
            viewHolder.name.setText(name);
            viewHolder.lastMess.setText(lastMess);
            viewHolder.time.setText(time);
            if (online)
                viewHolder.online.setImageBitmap(BitmapFactory.decodeResource(convertView.getResources(), R.mipmap.ic_online));
            else
                viewHolder.online.setImageBitmap(null);

            if (!read)
                viewHolder.back.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), R.color.notRead));
            else
                viewHolder.back.setBackgroundColor(0);
        }

        return convertView;
    }
}
