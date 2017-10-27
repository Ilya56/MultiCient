package com.dia.multiclient.news;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 12.09.2016.
 */
public class News implements Parcelable {
    private String id;
    private Bitmap image;
    private String name;
    private String text;
    private List<Bitmap> imageNews;
    private long date;
    private int likes;
    private int reposts;
    private int comments;
    private int from;
    private String userId;
    private List<Docs> docs;
    private boolean isLiked;
    private boolean isReposted;
    private String source;

    public News(int from, String id, Bitmap image, String name, String text, List<Bitmap> imageNews, long date, int likes, int reposts, int comments, String userId, List<Docs> docs, boolean isLiked, boolean isReposted, String source) {
        this.from = from;
        this.id = id;
        this.image = image;
        this.name = name;
        this.text = text;
        this.imageNews = imageNews;
        this.date = date;
        this.likes = likes;
        this.reposts = reposts;
        this.comments = comments;
        this.userId = userId;
        this.docs = docs;
        this.isLiked = isLiked;
        this.isReposted = isReposted;
        this.source = source;
    }

    public News(int from, String id, Bitmap image, String name, String text, List<Bitmap> imageNews, long date, int likes, int reposts, int comments, String userId, List<Docs> docs, boolean isLiked, boolean isReposted) {
        this(from, id, image, name, text, imageNews, date, likes, reposts, comments, userId, docs, isLiked, isReposted, "");
    }

    public News(int from, String id, Bitmap image, String name, String text, List<Bitmap> imageNews, long date, int likes, int reposts, int comments, String userId) {
        this(from, id, image, name, text, imageNews, date, likes, reposts, comments, userId, new ArrayList<Docs>(), false, false, "");
    }

    public News(int from, String id, Bitmap image, String name, String text, List<Bitmap> imageNews, long date, int likes, int reposts, int comments) {
        this(from, id, image, name, text, imageNews, date, likes, reposts, comments, "", new ArrayList<Docs>(), false, false, "");
    }

    public String getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public List<Bitmap> getImageNews() {
        return imageNews;
    }

    public long getDate() {
        return date;
    }

    public int getLikes() {
        return likes;
    }

    public int getReposts() {
        return reposts;
    }

    public int getComments() {
        return comments;
    }

    public int getFrom() {
        return from;
    }

    public String getUserId() {
        return userId;
    }

    public List<Docs> getDocs() {
        return docs;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public boolean isReposted() {
        return isReposted;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setReposts(int reposts) {this.reposts = reposts;}

    public String getSource() {
        return source;
    }

    public String toString() {
        return from + " " + name + " " + text + " " + id + " " + date + " " + imageNews + " " + likes + " " + isLiked + " " + reposts + " " + isReposted + " " + comments + " " + source;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(from);
        parcel.writeString(id);
        parcel.writeParcelable(image, i);
        parcel.writeString(name);
        parcel.writeString(text);
        parcel.writeTypedList(imageNews);
        parcel.writeLong(date);
        parcel.writeInt(likes);
        parcel.writeInt(reposts);
        parcel.writeInt(comments);
        parcel.writeTypedList(docs);
        parcel.writeString(source);
    }

    protected News(Parcel in) {
        from = in.readInt();
        id = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
        text = in.readString();
        imageNews = in.createTypedArrayList(Bitmap.CREATOR);
        date = in.readLong();
        likes = in.readInt();
        reposts = in.readInt();
        comments = in.readInt();
        docs = in.createTypedArrayList(Docs.CREATOR);
        source =in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
