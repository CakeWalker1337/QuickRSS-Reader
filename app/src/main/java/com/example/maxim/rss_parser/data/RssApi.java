package com.example.maxim.rss_parser.data;

import com.example.maxim.rss_parser.model.Article;
import com.example.maxim.rss_parser.model.Channel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Maxim on 04.03.2018.
 */

public interface RssApi {
    @GET
    Call<Article> getData(@Url String url);
}
