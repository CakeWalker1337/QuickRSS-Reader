package com.example.maxim.rss_parser.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.maxim.rss_parser.model.Channel;
import com.example.maxim.rss_parser.model.Item;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Класс, взаимодействующий с БД
 */
public class DatabaseHelper {

    //Переменные, определяющие некоторые части запросов: названия таблиц, колонок, знаков
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


    private static SQLiteDatabase database; //хэндл базы данных

    /**
     * Метод, открывающий подключение к БД
     *
     * @param context - контекст для открытия БД.
     */
    public static void openDB(Context context) {
        try {
            database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        } catch (SQLiteException e) {
            Log.w("DatabaseHelper", "Error while opening DB occurred!");
            e.printStackTrace();
        }
    }

    /**
     * Метод, создающий таблицы для хранения новостей и каналов
     */
    public static void createTables() {

//        pushNonResultQuery("DROP TABLE IF EXISTS " + CHANNELS_TABLE_NAME + END_OF_QUERY);
//        pushNonResultQuery("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME + END_OF_QUERY);

        //Запрос без ответа
        pushNonResultQuery("CREATE TABLE IF NOT EXISTS " +
                CHANNELS_TABLE_NAME + " (" +
                ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE_COLUMN + " TEXT NOT NULL, " +
                DESCRIPTION_COLUMN + " TEXT NOT NULL, " +
                LINK_COLUMN + " TEXT NOT NULL)" +
                END_OF_QUERY);
        //Запрос без ответа
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

    /**
     * Метод, закрывающий подключение к БД
     */
    public static void closeDB() {
        database.close();
    }

    /**
     * Метод, отправляющий запрос без ответа к БД
     *
     * @param query - текст запроса
     */
    public static void pushNonResultQuery(String query) {
        try {
            //Отправка запроса
            database.execSQL(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод, получающий все каналы с БД
     *
     * @return массив каналов или пустой массив, если нет ни одного канала
     */
    public static ArrayList<Channel> selectAllChannels() {
        ArrayList<Channel> result = new ArrayList<>();
        try {

            //Выбираем все каналы
            Cursor query = database.rawQuery(SELECT_ALL_FROM + CHANNELS_TABLE_NAME + END_OF_QUERY,
                    null);
            //Если ничего не найдено, закрываем курсор и возвращаем пустой массив
            if (!query.moveToFirst()) {
                query.close();
                return result;
            }

            //Получаем каналы по запросу
            do {
                Channel channel = new Channel();
                channel.setId(query.getInt(0));
                channel.setTitle(query.getString(1));
                channel.setDescription(query.getString(2));
                channel.setLink(query.getString(3));
                result.add(channel);
            }
            while (query.moveToNext());
            //Закрываем курсор, возвращаем заполненный массив каналов
            query.close();

        } catch (SQLException e) {
            //Отлов ошибки с БД
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Метод, получающий все новости с БД
     *
     * @return массив новостей или пустой массив, если нет ни одной новости
     */
    public static ArrayList<Item> selectAllItems() {
        try {
            //Выбираем все новости
            Cursor query = database.rawQuery(SELECT_ALL_FROM +
                    ITEMS_TABLE_NAME + END_OF_QUERY, null);
            //если ничего не найдено, возвращаем пустой лист новостей
            if (!query.moveToFirst()) {
                query.close();
                return new ArrayList<>();
            }

            ArrayList<Item> result = new ArrayList<>();
            //Получаем новости с запроса
            do {
                //Каждый итем должен иметь ненулевой канал, поэтому
                //получаем канал из айди канала итема
                Item item = new Item();
                Channel channel = new Channel();
                item.setChannel(channel);
                item.setId(query.getInt(0));
                item.setChannelId(query.getInt(1));
                item.setTitle(query.getString(2));
                item.setDescription(query.getString(3));
                item.setLink(query.getString(4));
                item.setPubDate(query.getString(5));
                Cursor query2 = database.rawQuery("SELECT " + TITLE_COLUMN + " FROM " +
                        CHANNELS_TABLE_NAME + " WHERE " + ID_COLUMN + " = " +
                        item.getChannelId() + ";", null);

                //Если итем есть, а канал удалён, удаляем итем
                if (query2.moveToFirst()) {
                    channel.setTitle(query2.getString(0));
                    item.setChannel(channel);
                    result.add(item);
                } else
                    deleteItem(item);

                query2.close();
            }
            while (query.moveToNext());
            //Закрываем подключение, отправляем результат
            query.close();
            return result;

        } catch (SQLException e) {
            //Отлов ошибки с БД
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Метод, создающий новую запись канала в БД
     *
     * @param channel - канал для записи в БД
     */
    public static void insertChannel(Channel channel) {
        //Заносим параметры запроса на создание записи
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE_COLUMN, channel.getTitle());
        contentValues.put(DESCRIPTION_COLUMN, channel.getDescription());
        contentValues.put(LINK_COLUMN, channel.getLink());

        //Отправляем запрос, получаем айдишник канала в БД или ошибку, если айди = -1
        long id = database.insert(CHANNELS_TABLE_NAME, null, contentValues);
        if (id != -1)
            channel.setId((int) id);
        else {
            Log.w("DatabaseHelper", "Error while inserting channel occurred!");
        }
    }

    /**
     * Метод, создающий новую запись новости в БД
     *
     * @param item - новость для записи в БД
     */
    public static void insertItem(Item item) {
        //Заносим параметры запроса на создание записи
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHANNEL_ID_COLUMN, item.getChannelId());
        contentValues.put(TITLE_COLUMN, item.getTitle());
        contentValues.put(DESCRIPTION_COLUMN, item.getDescription());
        contentValues.put(LINK_COLUMN, item.getLink());
        contentValues.put(PUB_DATE_COLUMN, item.getPubDate());

        //Отправляем запрос, получаем айдишник итеаа в БД или ошибку, если айди = -1
        long id = database.insert(ITEMS_TABLE_NAME, null, contentValues);
        if (id != -1)
            item.setId((int) id);
        else {
            Log.w("DatabaseHelper", "Error while inserting channel occurred!");
        }
    }

    /**
     * Метод, создающий записи элементов массива новостей в БД
     *
     * @param items - массив новостей для записи
     */
    public static void insertItems(ArrayList<Item> items) {
        try {
            //Ставим трай в случае, если при незаконченном выполнении метода он вызовется из
            // другого потока, т.к. выполняется асинхронно.
            for (Item item : items) {
                //Для каждого итема используем метод создания новой записи в базе
                insertItem(item);
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод, удаляющий все новости из БД
     */
    public static void deleteAllItems() {
        database.delete(ITEMS_TABLE_NAME, CHANNEL_ID_COLUMN + " > 0", null);
    }

    /**
     * Метод, удаляющий канал из БД
     */
    public static void deleteChannel(Channel channel) {
        database.delete(CHANNELS_TABLE_NAME, LINK_COLUMN + " = '" + channel.getLink() + "'", null);
    }

    /**
     * Метод, удаляющий одну новость из БД
     */
    public static void deleteItem(Item item) {
        database.delete(ITEMS_TABLE_NAME, ID_COLUMN + " = '" + item.getId() + "'", null);
    }
}
