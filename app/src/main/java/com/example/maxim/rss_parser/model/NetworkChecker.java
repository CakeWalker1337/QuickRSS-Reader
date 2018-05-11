package com.example.maxim.rss_parser.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Класс, отвечающий за проверку соединения с интернетом
 */

public class NetworkChecker {

    /**
     * Метод проверяющий наличие соединения с интернетом
     *
     * @param context - контекст приложения
     * @return true, если соединение есть, иначе false
     */
    public static boolean isOnline(Context context) {
        //Получаем сервис, отвечающий за соединения и смотрим наличие соединения с интернетом
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
