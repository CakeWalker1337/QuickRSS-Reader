package com.example.maxim.rss_parser.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.model.Channel;
import com.example.maxim.rss_parser.model.Item;

import java.util.ArrayList;


public class DatabaseHelper {

    public static final String DB_NAME = "rssParser";

    private static SQLiteDatabase database;
    private static Context currentContext;

    public static void openDB(Context context) {
        try {
            database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        } catch (SQLiteException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void createTables()
    {
        DatabaseHelper.pushNonResultQuery("CREATE TABLE IF NOT EXISTS `rssChannels`(`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `title` TEXT NOT NULL," +
                " `description` TEXT NOT NULL" +
                " `link` TEXT NOT NULL);");
        DatabaseHelper.pushNonResultQuery("CREATE TABLE IF NOT EXISTS `rssItems`(`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                " `channelId` INTEGER NOT NULL DEFAULT '0'," +
                " `title` TEXT NOT NULL," +
                " `description` TEXT NOT NULL," +
                " `link` TEXT NOT NULL" +
                " `pubDate` TEXT NOT NULL);");
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

    public static ArrayList<Channel> selectChannels()
    {
        try {
            Cursor query = database.rawQuery(currentContext.getString(R.string.selectChannelQuery), null);
            if (!query.moveToFirst())
            {
                query.close();
                return null;
            }

            ArrayList<Channel> result = new ArrayList<>();

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
            return result;

        } catch (SQLException e) {
            Toast.makeText(currentContext, e.toString(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static ArrayList<Item> selectItems()
    {
        try {
            Cursor query = database.rawQuery("SELECT * FROM `rssItems`;", null);
            if (!query.moveToFirst())
            {
                query.close();
                return null;
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

    public static void saveChannel(Channel channel)
    {
        pushNonResultQuery("INSERT INTO `rssChannels` (`title`, `description`, `link`) VALUES ('" +
                channel.getTitle() + "', '" +
                channel.getDescription() + "', '" +
                channel.getLink() +"');");
    }

    public static void saveItem(Item item)
    {
        pushNonResultQuery("INSERT INTO `rssChannels` (`channelId`, `title`, `description`, `link`) VALUES ('" +
                item.getChannelId() + "', '" +
                item.getTitle() + "', '" +
                item.getDescription() + "', '" +
                item.getLink() + "', '" +
                item.getPubDate() +"');");
    }

}
