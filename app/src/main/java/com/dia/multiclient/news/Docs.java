package com.dia.multiclient.news;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ilya on 05.12.2016.
 */
public class Docs implements Parcelable {
    private int id;
    private String url;
    private String type;
    private Bitmap image;

    public Docs(int id, String url, String type, Bitmap image) {
        this.id = id;
        this.url = url;
        this.type = type;
        this.image = image;
    }

    protected Docs(Parcel in) {
        id = in.readInt();
        url = in.readString();
        type = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public Bitmap getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(url);
        parcel.writeString(type);
        parcel.writeParcelable(image, i);
    }

    public static final Creator<Docs> CREATOR = new Creator<Docs>() {
        @Override
        public Docs createFromParcel(Parcel in) {
            return new Docs(in);
        }

        @Override
        public Docs[] newArray(int size) {
            return new Docs[size];
        }
    };
}
