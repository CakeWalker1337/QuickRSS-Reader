package com.tenxgames.maxim.rss_parser.data;

import com.tenxgames.maxim.rss_parser.model.Article;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Maxim on 04.03.2018.
 */

//Интерфейс для создания запроса
public interface RssApi {
    //В параметрах запроса указан кастомный URL для подключения к серверу канала
    //Он перекрывает URL, заданный в билдере Retrofit
    @GET
    Call<Article> getData(@Url String url);
}
