package com.example.maxim.rss_parser.data;

import android.app.Application;

import com.example.maxim.rss_parser.model.ConnectionInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Maxim on 04.03.2018.
 */

/*
* Класс для построения объекта для соединения с серверами.
* */
public class App extends Application {


    private static RssApi rssApi; //Объект интерфейса гет-запроса, будет использоваться для
    //отправки запросов
    private Retrofit retrofit;//Объект-билдер для построения объекта, отвечающего за запросы
    private OkHttpClient client; //Объект клиента для установки доп параметров подключения

    public static RssApi getApi() {
        return rssApi;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Создаем клиент подключения с параметрами
        client = new OkHttpClient.Builder()
                //Добавляем перехватчик события отсутствия интернета.
                //Он будет приниматься во внимание во время выполнения запроса и кидать
                //эксепшн NoConnectionException
                .addInterceptor(new ConnectionInterceptor(getApplicationContext()))
                //Добавляем параметры таймаутов ответа сервера
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        //Создаем сам API для подключения
        retrofit = new Retrofit.Builder()
                //Добавляем базовый URL (он будет игнорироваться из-за параметров запроса,
                // но его обязательно нужно указать)
                .baseUrl("http://www")
                //Добавляем созданного клиента
                .client(client)
                // Добавляем XML-конвертер для парсинга RSS-канала
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        rssApi = retrofit.create(RssApi.class);
    }
}
