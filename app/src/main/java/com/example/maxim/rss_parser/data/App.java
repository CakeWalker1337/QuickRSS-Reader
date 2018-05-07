package com.example.maxim.rss_parser.data;

import android.app.Application;

import com.example.maxim.rss_parser.model.ConnectionInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okio.Timeout;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Maxim on 04.03.2018.
 */

public class App extends Application {

    private static RssApi rssApi;
    private Retrofit retrofit;
    private OkHttpClient client;

    @Override
    public void onCreate() {
        super.onCreate();
        client = new OkHttpClient.Builder()
                .addInterceptor(new ConnectionInterceptor(getApplicationContext()))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://www")
                .client(client)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        rssApi = retrofit.create(RssApi.class);
    }

    public static RssApi getApi() {
        return rssApi;
    }
}
