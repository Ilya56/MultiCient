package com.dia.multiclient.message;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ilya on 21.08.2016.
 */
public class MessagePreview implements Parcelable {
    private Bitmap image;
    private String fname;
    private String lname;
    private String lastMess;
    private long lastTime;
    private boolean online;
    private String userID;
    private boolean isRead = true;

    public MessagePreview(Bitmap image, String fname, String lname, String lastMess, long lastTime, boolean online, String userID, boolean isRead) {
        this.image = image;
        this.fname = fname;
        this.lname = lname;
        this.lastMess = lastMess;
        this.lastTime = lastTime;
        this.online = online;
        this.userID = userID;
        this.isRead = isRead;
    }

    public MessagePreview(Bitmap image, String fname, String lname, boolean online, String userID) {
        this(image, fname, lname, "", 0, online, userID, true);
    }

    public Bitmap getImage() {
        return image;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getLastMess() {
        return lastMess;
    }

    public long getLastTime() {
        return lastTime;
    }

    public boolean isOnline() {
        return online;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isRead() {
        return isRead;
    }

    public String toString() {
        return fname + " " + lname + " " + lastMess + " " + lastTime + " " + online + " " + userID + " " + isRead;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        image.writeToParcel(dest, flags);
        dest.writeString(fname);
        dest.writeString(lname);
        dest.writeString(lastMess);
        dest.writeLong(lastTime);
        dest.writeByte((byte) (online ? 1 : 0));
        dest.writeString(userID);
        dest.writeByte((byte)(isRead ? 1 : 0));
    }

    protected MessagePreview(Parcel in) {
        image = Bitmap.CREATOR.createFromParcel(in);
        fname = in.readString();
        lname = in.readString();
        lastMess = in.readString();
        lastTime = in.readLong();
        online = in.readByte() != 0;
        userID = in.readString();
        isRead = in.readByte() != 0;
    }

    public static final Creator<MessagePreview> CREATOR = new Creator<MessagePreview>() {
        @Override
        public MessagePreview createFromParcel(Parcel in) {
            return new MessagePreview(in);
        }

        @Override
        public MessagePreview[] newArray(int size) {
            return new MessagePreview[size];
        }
    };
}
