package com.dia.multiclient.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ilya on 03.10.2016.
 */
public class User implements Parcelable {
    private String id;
    private String firstName;
    private String lastName;
    private Bitmap image;
    private String status;
    private boolean online;
    private String city;
    private String country;
    private String studyPlace;
    private String relation;
    private int friends;
    private int photos;
    private int followers;
    private int groups;
    private String birthday;
    private String sex;
    private int from;

    public User(Context context, int from, String id, String firstName, String lastName, Bitmap image, String status, boolean online, String city, String country, String studyPlace, String relation, int friends, int photos, int followers, int groups, String birthday, int sex) {
        this(context, from, id, firstName, lastName, image, status, online, city, country, studyPlace, relation, friends, photos, followers, groups, birthday);
        this.sex = (sex == 0) ? "" : (sex == 1) ? "Female" : "Male";
    }

    public User(Context context, int from, String id, String firstName, String lastName, Bitmap image, String status, boolean online, String city, String country, String studyPlace, String relation, int friends, int photos, int followers, int groups, String birthday, String sex) {
        this(context, from, id, firstName, lastName, image, status, online, city, country, studyPlace, relation, friends, photos, followers, groups, birthday);
        this.sex = sex.substring(0, 1).toUpperCase() + sex.substring(1, sex.length());
    }

    public User(Context context, int from, String id, String firstName, String lastName, Bitmap image, String status, boolean online, String city, String country, String studyPlace, String relation, int friends, int photos, int followers, int groups, String birthday) {
        this.from = from;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.status = status;
        this.online = online;
        this.city = city;
        this.country = country;
        this.studyPlace = studyPlace;
        this.relation = relation;
        this.friends = friends;
        this.photos = photos;
        this.followers = followers;
        this.groups = groups;
        this.birthday = birthday;
    }

    public User(Context context, int from, String id, String firstName, String lastName, Bitmap image, boolean online) {
        this(context, from, id, firstName, lastName, image, "", online, "", "", "", "", 0, 0, 0, 0, "", 0);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOnline() {
        return online;
    }

    public String getCity() {
        return city;
    }

    public String getStudyPlace() {
        return studyPlace;
    }

    public String getRelation() {
        return relation;
    }

    public int getFriends() {
        return friends;
    }

    public int getPhotos() {
        return photos;
    }

    public int getFollowers() {
        return followers;
    }

    public int getGroups() {
        return groups;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getSex() {
        return sex;
    }

    public String getCountry() {
        return country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return id + " " + firstName + " " + lastName + " " + status + " " + online + " " + city + " " + country + " " + studyPlace + " " + relation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeInt(from);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeParcelable(image, i);
        parcel.writeString(status);
        parcel.writeByte((byte) (online ? 1 : 0));
        parcel.writeString(city);
        parcel.writeString(country);
        parcel.writeString(studyPlace);
        parcel.writeString(relation);
        parcel.writeInt(friends);
        parcel.writeInt(photos);
        parcel.writeInt(followers);
        parcel.writeInt(groups);
        parcel.writeString(birthday);
        parcel.writeString(sex);
    }

    protected User(Parcel in) {
        id = in.readString();
        from = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        status = in.readString();
        online = in.readByte() != 0;
        city = in.readString();
        country = in.readString();
        studyPlace = in.readString();
        relation = in.readString();
        friends = in.readInt();
        photos = in.readInt();
        followers = in.readInt();
        groups = in.readInt();
        birthday = in.readString();
        sex = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
