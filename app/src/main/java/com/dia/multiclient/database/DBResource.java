package com.dia.multiclient.database;

/**
 * Created by Ilya on 22.08.2016.
 */
public class DBResource {
    public static final String DB_NAME = "bd_kval";
    public static final int BD_VERSION = 1;

    public static final class Song {
        public static final String ID  = "id";
        public static final String NAME = "name";
        public static final String SINGER = "singer";
        public static final String TIME = "time";
        public static final String RES = "res";

        public static final String TABLE_NAME = "songs";
        public static final String CREATE_TABLE = "create table "+ TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT, " + SINGER + " TEXT, " + TIME + " INTEGER, " + RES + " TEXT " + ");";
    }

    public static final class MessagePreview {
        public static final String ID  = "id";
        public static final String IMAGE = "image";
        public static final String FNAME = "first_name";
        public static final String LNAME = "last_name";
        public static final String LMESS = "last_message";
        public static final String LTIME = "last_time";
        public static final String ONLINE = "online";
        public static final String USER_ID = "user_id";
        public static final String READ_STATE = "read_state";

        public static final String TABLE_NAME = "messages_preview";
        public static final String CREATE_TABLE = "create table "+ TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IMAGE + " BLOB, " + FNAME + " TEXT, " + LNAME + " TEXT, " + LMESS + " TEXT, " + LTIME + " INTEGER, "
                + ONLINE + " NUMERIC, " + USER_ID + " INTEGER, " + READ_STATE + " NUMERIC " + ");";
    }

    public static final class Equalizer {
        public static final String ID  = "id";
        public static final String BAND_LEVEL = "level";

        public static final String TABLE_NAME = "equalizer";
        public static final String CREATE_TABLE = "create table "+ TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, "
                + BAND_LEVEL + " INTEGER " + ");";
    }

    public static final class Friends {
        public static final String ID = "id";
        public static final String FROM = "from_";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String IMAGE = "image";
        public static final String ONLINE = "online";

        public static final String TABLE_NAME = "friends";
        public static final String CREATE_TABLE = "create table "+ TABLE_NAME + " ( " + ID + " TEXT PRIMARY KEY, "
                + FROM + " INTEGER, " + FIRST_NAME + " TEXT, " + LAST_NAME + " TEXT, " + IMAGE + " BLOB, "
                + ONLINE + " NUMERIC " + ");";
    }

    public static final class News {
        public static final String ID = "id";
        public static final String FROM = "from_";
        public static final String IMAGE = "image";
        public static final String NAME = "name";
        public static final String TEXT = "text";
        public static final String[] IMAGES = new String[10];
        static {
            for(int i = 0; i < 10; i++) {
                IMAGES[i] = "images_" + i;
            }
        }
        public static final String DATE = "date";
        public static final String LIKES = "likes";
        public static final String REPOSTS = "reposts";
        public static final String COMMENTS = "comments";

        public static final String TABLE_NAME = "messages_preview";
        public static String CREATE_TABLE = "create table "+ TABLE_NAME + " ( " + ID + " INTEGER PRIMARY KEY, "
                + FROM + " INTEGER, " + IMAGE + " BLOB, " + NAME + " TEXT, " + TEXT + " TEXT, " + DATE + " INTEGER, "
                + LIKES + " INTEGER, " + REPOSTS + " INTEGER, " + COMMENTS + " NUMERIC ";

        static {
            for (int i = 0; i < 10; i++) {
                CREATE_TABLE += IMAGES[i] + " BLOB ";
                if (i < 9) {
                    CREATE_TABLE += " ,";
                }
            }
            CREATE_TABLE += " );";
        }
    }
}
