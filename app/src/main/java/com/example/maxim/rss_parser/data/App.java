package com.example.maxim.rss_parser.data;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Maxim on 04.03.2018.
 */

public class App extends Application {

    private static RssApi rssApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://lenta.ru")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        rssApi = retrofit.create(RssApi.class);
    }

    public static RssApi getApi() {
        return rssApi;
    }
}
