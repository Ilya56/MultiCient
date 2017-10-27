package com.dia.multiclient.message;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ilya on 26.08.2016.
 */
public class Message implements Parcelable {
    private String body;
    private int time;
    private String name;
    private Bitmap image;
    private boolean fromMe;
    private boolean read = true;

    public Message(String body, String name, Bitmap image, int time, boolean fromMe, boolean read) {
        this.body = body;
        this.time = time;
        this.name = name;
        this.image = image;
        this.fromMe = fromMe;
        this.read = read;
    }

    public String getBody() {
        return body;
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }

    public boolean isFromMe() {
        return fromMe;
    }

    public boolean isRead() {
        return read;
    }

    public String toString() {
        return body + " " + time + " " + name + " " + fromMe + " " + read;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(body);
        parcel.writeInt(time);
        parcel.writeString(name);
        parcel.writeParcelable(image, i);
        parcel.writeByte((byte) (fromMe ? 1 : 0));
        parcel.writeByte((byte) (read ? 1 : 0));
    }

    protected Message(Parcel in) {
        body = in.readString();
        time = in.readInt();
        name = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        fromMe = in.readByte() != 0;
        read = in.readByte() != 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}
