package com.dia.multiclient.news;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreAdapter;
import com.dia.multiclient.fragments.MenuFragment;
import com.dia.multiclient.news.fragments.NewsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 22.10.2016.
 */
public class NewsAdapter extends CoreAdapter<News> {
    public NewsAdapter(List<News> newses, Activity activity) {
        super(newses, activity);
    }

    private class ViewHolder {
        ImageView image;
        TextView name;
        TextView date;
        TextView text;
        List<ImageView> imageNew = new ArrayList<>();
        ImageView from;
        ImageButton likes;
        ImageButton reposts;
        ImageButton comments;
        TextView likes_c;
        TextView reposts_c;
        TextView comments_c;
        List<VideoView> videos = new ArrayList<>();
        List<Integer> imageNewsType = new ArrayList<>();
        LinearLayout ll;
        TextView source;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        final News n = (News) getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_news, parent, false);

            viewHolder.image = (ImageView) convertView.findViewById(R.id.image_from);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name_from);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text_new);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.from = (ImageView) convertView.findViewById(R.id.from);
            viewHolder.likes = (ImageButton) convertView.findViewById(R.id.likes);
            viewHolder.reposts = (ImageButton) convertView.findViewById(R.id.reposts);
            viewHolder.comments = (ImageButton) convertView.findViewById(R.id.commects);
            viewHolder.likes_c = (TextView) convertView.findViewById(R.id.likes_c);
            viewHolder.reposts_c = (TextView) convertView.findViewById(R.id.reposts_c);
            viewHolder.comments_c = (TextView) convertView.findViewById(R.id.comments_c);
            viewHolder.ll = (LinearLayout) convertView.findViewById(R.id.image_container);
            viewHolder.source = (TextView) convertView.findViewById(R.id.source);

            //images
            for (int i = 0; i < n.getImageNews().size(); i++) {
                ImageView imageView = new ImageView(convertView.getContext());
                viewHolder.ll.addView(imageView);
                viewHolder.imageNew.add(imageView);
                viewHolder.imageNewsType.add(0);
            }

            //video
            for(int i = 0; i < n.getDocs().size(); i++) {
                if (n.getDocs().get(i).getType().equals("video")) {
                    VideoView videoView = new VideoView(convertView.getContext());
                    viewHolder.ll.addView(videoView);
                    viewHolder.videos.add(videoView);
                }
                if (n.getDocs().get(i).getType().equals("gif")) {
                    ImageView imageView = new ImageView(convertView.getContext());
                    viewHolder.ll.addView(imageView);
                    viewHolder.imageNew.add(imageView);
                    viewHolder.imageNewsType.add(1);
                }
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Bitmap image = n.getImage();
        String name = n.getName();
        String text = n.getText();
        String date = G.toRealTime(n.getDate());
        List<Bitmap> imageNews = n.getImageNews();
        List<Docs> docs = n.getDocs();
        int from = n.getFrom();
        int likes = n.getLikes();
        boolean isLiked = n.isLiked();
        if (isLiked)
            viewHolder.likes.setBackgroundColor(ContextCompat.getColor(viewHolder.image.getContext(), R.color.notRead));
        int reposts = n.getReposts();
        boolean isReposted = n.isReposted();
        if (isReposted)
            viewHolder.reposts.setBackgroundColor(ContextCompat.getColor(viewHolder.image.getContext(), R.color.notRead));
        int comments = n.getComments();

        viewHolder.image.setImageBitmap(image);
        viewHolder.name.setText(name);
        viewHolder.text.setText(text);
        viewHolder.date.setText(date);

        for (int i = 0; i < viewHolder.imageNew.size(); i++) {
            ImageView imageView = viewHolder.imageNew.get(i);
            if (viewHolder.imageNewsType.get(i) == 0) {
                if (imageNews.size() > i) {
                    imageView.setImageBitmap(imageNews.get(i));
                }
            } else if (viewHolder.imageNewsType.get(i) == 1) {
                if (docs.size() > i) {
                    imageView.setImageBitmap(docs.get(i).getImage());
                    Log.d("qwe", "gif");
                }
            }
        }
<<<<<<< HEAD
        for(int i = 0; i < viewHolder.videos.size(); i++) {
            VideoView videoView = viewHolder.videos.get(i);
            videoView.setMediaController(new MediaController(convertView.getContext()));
            videoView.setVideoURI(Uri.parse(docs.get(i).getUrl()));
        }
=======
//        for(int i = 0; i < viewHolder.videos.size(); i++) {
//            VideoView videoView = viewHolder.videos.get(i);
//            videoView.setMediaController(new MediaController(convertView.getContext()));
//            videoView.setVideoURI(Uri.parse(docs.get(i).getUrl()));
//        }
>>>>>>> origin/master

        if (from == G.VK) {
            viewHolder.from.setImageDrawable(activity.getResources().getDrawable(R.drawable.from_vk));
        } else if (from == G.FB) {
            viewHolder.from.setImageDrawable(activity.getResources().getDrawable(R.drawable.from_fb));
        }

        viewHolder.likes_c.setText("" + likes);
        viewHolder.reposts_c.setText("" + reposts);
        viewHolder.comments_c.setText("" + comments);

        viewHolder.likes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                NewsFragment nf = (NewsFragment) MenuFragment.getTabs().get(0);
                nf.like(viewHolder.likes, n);
                viewHolder.likes.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.notRead));
            }
        });
        viewHolder.reposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsFragment nf = (NewsFragment) MenuFragment.getTabs().get(0);
                nf.repost(viewHolder.reposts, n);
                viewHolder.reposts.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.notRead));
            }
        });

        if (n.getSource() != "") {
            String text2 = convertView.getContext().getString(R.string.reposted_from) + n.getSource();
            viewHolder.source.setText(text2);
        } else
            viewHolder.source.setText("");

        return convertView;
    }
}
