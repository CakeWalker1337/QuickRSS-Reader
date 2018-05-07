package com.example.maxim.rss_parser.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.model.Channel;
import com.example.maxim.rss_parser.model.Item;

import java.util.ArrayList;


public class DatabaseHelper {

    public static final String DB_NAME = "rssParser";
    public static final String ITEMS_TABLE_NAME = "rssItems";
    public static final String CHANNELS_TABLE_NAME = "rssChannels";
    public static final String TITLE_COLUMN = "title";
    public static final String ID_COLUMN = "id";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String LINK_COLUMN = "link";
    public static final String CHANNEL_ID_COLUMN = "channelId";
    public static final String PUB_DATE_COLUMN = "pubDate";
    public static final String END_OF_QUERY = ";";
    public static final String SELECT_ALL_FROM = "SELECT * FROM ";


    private static SQLiteDatabase database;

    @SuppressLint("StaticFieldLeak")
    private static Context currentContext;

    public static void openDB(Context context) {
        currentContext = context;
        try {
            database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        } catch (SQLiteException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static void createTables() {

        pushNonResultQuery("DROP TABLE IF EXISTS " + CHANNELS_TABLE_NAME + END_OF_QUERY);
        pushNonResultQuery("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME + END_OF_QUERY);


        pushNonResultQuery("CREATE TABLE IF NOT EXISTS " +
                CHANNELS_TABLE_NAME + " (" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE_COLUMN + " TEXT NOT NULL, " +
                DESCRIPTION_COLUMN + " TEXT NOT NULL, " +
                LINK_COLUMN + " TEXT NOT NULL)" +
                END_OF_QUERY);

        pushNonResultQuery("CREATE TABLE IF NOT EXISTS " +
                ITEMS_TABLE_NAME + " (" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CHANNEL_ID_COLUMN + " INTEGER NOT NULL DEFAULT '0', " +
                TITLE_COLUMN + " TEXT NOT NULL, " +
                DESCRIPTION_COLUMN + " TEXT NOT NULL, " +
                LINK_COLUMN + " TEXT NOT NULL, " +
                PUB_DATE_COLUMN + " TEXT NOT NULL)" +
                END_OF_QUERY);
    }

    public static void closeDB() {
        database.close();
    }

    public static void pushNonResultQuery(String query) {
        try {
            database.execSQL(query);
        } catch (SQLException e) {
            Toast.makeText(currentContext, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static ArrayList<Channel> selectAllChannels() {
        ArrayList<Channel> result = new ArrayList<>();
        try {

            Cursor query = database.rawQuery(SELECT_ALL_FROM + CHANNELS_TABLE_NAME + END_OF_QUERY,
                    null);

            if (!query.moveToFirst()) {
                query.close();
                return result;
            }

            do {
                Channel channel = new Channel();
                channel.setId(query.getInt(0));
                channel.setTitle(query.getString(1));
                channel.setDescription(query.getString(2));
                channel.setLink(query.getString(3));
                result.add(channel);
            }
            while (query.moveToNext());

            query.close();

        } catch (SQLException e) {
            Toast.makeText(currentContext, e.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public static ArrayList<Item> selectAllItems() {
        try {
            Cursor query = database.rawQuery(SELECT_ALL_FROM +
                    ITEMS_TABLE_NAME + END_OF_QUERY, null);
            if (!query.moveToFirst()) {
                query.close();
                return new ArrayList<>();
            }

            ArrayList<Item> result = new ArrayList<>();

            do {
                Item item = new Item();
                item.setId(query.getInt(0));
                item.setChannelId(query.getInt(1));
                item.setTitle(query.getString(2));
                item.setDescription(query.getString(3));
                item.setLink(query.getString(4));
                item.setPubDate(query.getString(5));
                result.add(item);
            }
            while (query.moveToNext());

            query.close();
            return result;

        } catch (SQLException e) {
            Toast.makeText(currentContext, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @SuppressLint("ShowToast")
    public static void insertChannel(Channel channel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE_COLUMN, channel.getTitle());
        contentValues.put(DESCRIPTION_COLUMN, channel.getDescription());
        contentValues.put(LINK_COLUMN, channel.getLink());

        long id = database.insert(CHANNELS_TABLE_NAME, null, contentValues);
        if (id != -1)
            channel.setId((int) id);
        else {
            Toast.makeText(currentContext, R.string.insertChannelError, Toast.LENGTH_LONG);
            Log.w("DatabaseHelper", "Error while inserting channel occurred!");
        }
    }

    @SuppressLint("ShowToast")
    public static void insertItem(Item item) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(CHANNEL_ID_COLUMN, item.getChannelId());
        contentValues.put(TITLE_COLUMN, item.getTitle());
        contentValues.put(DESCRIPTION_COLUMN, item.getDescription());
        contentValues.put(LINK_COLUMN, item.getLink());
        contentValues.put(PUB_DATE_COLUMN, item.getPubDate());

        long id = database.insert(ITEMS_TABLE_NAME, null, contentValues);
        if (id != -1)
            item.setId((int) id);
        else {
            Toast.makeText(currentContext, R.string.insertItemError, Toast.LENGTH_LONG);
            Log.w("DatabaseHelper", "Error while inserting channel occurred!");
        }
    }

    public static void deleteChannel(Channel channel)
    {
        database.delete(CHANNELS_TABLE_NAME, LINK_COLUMN + " = '" + channel.getLink() + "'", null);
    }
}
